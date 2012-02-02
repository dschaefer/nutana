/*******************************************************************************
 * Copyright (c) 2012 Doug Schaefer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Doug Schaefer - Initial API and implementation
 *******************************************************************************/
package doug.nutana.http.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import doug.nutana.core.WriteStream;
import doug.nutana.http.HttpServerResponse;
import doug.nutana.net.Socket;

public class HttpServerResponseImpl implements HttpServerResponse {

	private static final String VERSION = "HTTP/1.1"; //$NON-NLS-1$
	
	private final Socket socket;
	private final WriteStream socketWriteStream;

	Map<String, String> headers = new HashMap<>();
	Map<String, String> trailers = new HashMap<>();

	public HttpServerResponseImpl(Socket socket) {
		this.socket = socket;
		this.socketWriteStream = socket.getWriteStream();
	}

	@Override
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public void setTrailer(String key, String value) {
		trailers.put(key, value);
	}

	@Override
	public Map<String, String> getTrailers() {
		return trailers;
	}

	@Override
	public void writeHead(int statusCode) {
		writeHead(statusCode, getReasonPhrase(statusCode));
	}

	@Override
	public void writeHead(int statusCode, String reason) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VERSION);
		buffer.append(' ');
		buffer.append(statusCode);
		buffer.append(' ');
		buffer.append(reason);
		buffer.append("\r\n");
		
		for (Entry<String, String> entry : headers.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(": ");
			buffer.append(entry.getValue());
			buffer.append("\r\n");
		}
		
		buffer.append("\r\n");

		writeStream.writeAscii(buffer);
	}

	private class ChunkWriteStream extends WriteStream {
		@Override
		protected void doWrite() {
			while (!pendingBuffers.isEmpty()) {
				ByteBuffer srcBuffer = pendingBuffers.removeFirst();
	
				// Send size
				writeAscii(String.format("%x\r\n", srcBuffer.remaining())); //$NON-NLS-1$
				
				// Send the buffer
				// TODO would be nice to just pass the src down
				while (srcBuffer.hasRemaining()) {
					ByteBuffer destBuffer = socketWriteStream.getBuffer();
					destBuffer.put(srcBuffer); // assuming same size
					destBuffer.limit(destBuffer.position());
					destBuffer.rewind();
					socketWriteStream.write(destBuffer);
				}
				
				// Send terminator
				writeAscii("\r\n"); // $NON-NLS-1$
			}
			
			// No more buffers, ask for more
			fireDrain();
		}

		@Override
		public void end() {
			// last chunk
			writeAscii("0\r\n\r\n");
		}

		@Override
		protected void close() throws IOException {
			socket.close();
		}
		
		public void writeAscii(CharSequence text) {
			ByteBuffer writeBuffer = socketWriteStream.getBuffer();
			int n = text.length();
			for (int i = 0; i < n; ++i) {
				if (!writeBuffer.hasRemaining()) {
					socketWriteStream.write(writeBuffer);
					writeBuffer = socketWriteStream.getBuffer();
				}
				
				// Works because it's ascii
				writeBuffer.put((byte)text.charAt(i));
			}
			writeBuffer.limit(writeBuffer.position());
			writeBuffer.rewind();
			socketWriteStream.write(writeBuffer);
		}
	}
	
	private ChunkWriteStream writeStream = new ChunkWriteStream();
	
	@Override
	public WriteStream getWriteStream() {
		return writeStream;
	}

	private String getReasonPhrase(int responseCode) {
		switch (responseCode) {
		case 100:
			return "Continue";
		case 101:
			return "Switching Protocols";
		case 200:
			return "OK";
		case 201:
			return "Created";
		case 202:
			return "Accepted";
		case 203:
			return "Non-Authoritative Information";
		case 204:
			return "No Content";
		case 205:
			return "Reset Content";
		case 206:
			return "Partial Content";
		case 300:
			return "Multiple Choices";
		case 301:
			return "Moved Permanently";
		case 302:
			return "Found";
		case 303:
			return "See Other";
		case 304:
			return "Not Modified";
		case 305:
			return "Use Proxy";
		case 307:
			return "Temporary Redirect";
		case 400:
			return "Bad Request";
		case 401:
			return "Unauthorized";
		case 402:
			return "Payment Required";
		case 403:
			return "Forbidden";
		case 404:
			return "Not Found";
		case 405:
			return "Method Not Allowed";
		case 406:
			return "Not Acceptable";
		case 407:
			return "Proxy Authentication Required";
		case 408:
			return "Request Time-out";
		case 409:
			return "Conflict";
		case 410:
			return "Gone";
		case 411:
			return "Length Required";
		case 412:
			return "Precondition Failed";
		case 413:
			return "Request Entity Too Large";
		case 414:
			return "Request-URI Too Large";
		case 415:
			return "Unsupported Media Type";
		case 416:
			return "Requested range not satisfiable";
		case 417:
			return "Expectation Failed";
		case 500:
			return "Internal Server Error";
		case 501:
			return "Not Implemented";
		case 502:
			return "Bad Gateway";
		case 503:
			return "Service Unavailable";
		case 504:
			return "Gateway Time-out";
		case 505:
			return "HTTP Version not supported";
		default:
			return "Unknown";
		}
	}
}
