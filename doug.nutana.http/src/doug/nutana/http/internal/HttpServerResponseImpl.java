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
import java.util.HashMap;
import java.util.Map;

import doug.nutana.core.WriteStream;
import doug.nutana.http.HttpServerResponse;
import doug.nutana.net.Socket;

public class HttpServerResponseImpl implements HttpServerResponse {

	private final Socket socket;

	Map<String, String> headers = new HashMap<>();
	Map<String, String> trailers = new HashMap<>();

	public HttpServerResponseImpl(Socket socket) {
		this.socket = socket;
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
		// TODO Auto-generated method stub

	}

	private class ResponseWriteStream extends WriteStream {
		@Override
		protected void doWrite() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void end() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void close() throws IOException {
			// TODO Auto-generated method stub
			
		}
	}
	
	private ResponseWriteStream writeStream = new ResponseWriteStream();
	
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
