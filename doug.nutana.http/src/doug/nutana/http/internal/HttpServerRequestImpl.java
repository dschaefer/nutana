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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import doug.nutana.http.HttpServerRequest;

public class HttpServerRequestImpl extends HttpServerRequest {

	private final HttpConnection connection;
	
	private String method;
	private String url;
	private String version;
	private Map<String, String> headers = new HashMap<>();
	private Map<String, String> trailers = new HashMap<>();
	
	// state machine
	private boolean haveCR = false;
	private boolean atStartLine = true;
	private boolean inHeader = true;
	private boolean atSize = true;
	
	private StringBuffer line = new StringBuffer();
	private int size = 0;
	
	private static final Pattern startLinePattern = Pattern.compile("([^ ]+) ([^ ]+) (.*)");
	private static final Pattern attributePattern = Pattern.compile("(.*): *(.*) *");
	
	public HttpServerRequestImpl(HttpConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getHeader(String key) {
		return headers.get(key);
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String getTrailer(String key) {
		return trailers.get(key);
	}

	@Override
	public Map<String, String> getTrailers() {
		return trailers;
	}

	@Override
	public void pause() {
		// TODO
	}

	@Override
	public void resume() {
		// TODO
	}

	@Override
	public void close() throws IOException {
		// TODO
	}
	
	@Override
	public void fireData(ByteBuffer buffer) {
		super.fireData(buffer);
	}
	
	@Override
	public void fireEnd() {
		super.fireEnd();
	}
	
	public void handleData(ByteBuffer buffer) {
		while (buffer.hasRemaining())
			if (inHeader)
				handleHeaderData(buffer);
			else
				handleChunkData(buffer);
	}

	private void handleHeaderData(ByteBuffer buffer) {
		while (buffer.hasRemaining()) {
			byte c = buffer.get();
			if (!haveCR && c == 13) {
				haveCR = true;
			} else if (haveCR && c == 10) {
				haveCR = false;
				if (atStartLine) {
					// RFC says to skip blank lines
					if (line.length() == 0)
						return;
					handleStartLine();
					atStartLine = false;
				} else {
					if (line.length() > 0) {
						handleAttribute();
					} else if (headers.get("ContentLength") != null || headers.get("TransferEncoding") != null) {
						connection.handleRequest(this);
						inHeader = false;
						return;
					} else {
						connection.handleRequest(this);
						atStartLine = true;
					}
				}
				line.delete(0, line.length());
			} else {
				line.append((char)c);
			}
		}
	}
	
	private void handleStartLine() {
		Matcher matcher = startLinePattern.matcher(line);
		if (matcher.matches()) {
			method = matcher.group(1);
			url = matcher.group(2);
			version = matcher.group(3);
		}
	}
	
	private void handleAttribute() {
		Matcher matcher = attributePattern.matcher(line);
		if (!matcher.matches())
			return;
		
		headers.put(matcher.group(1), matcher.group(2));
	}
	
	private void handleChunkData(ByteBuffer buffer) {
		while (buffer.hasRemaining()) {
			if (atSize) {
				byte c = buffer.get();
				if (!haveCR && c == 13) {
					haveCR = true;
				} else if (haveCR && c == 10) {
					haveCR = false;
					if (size > 0) {
						atSize = false;
					} else {
						fireEnd();
						inHeader = true;
						return;
					}
				} else if (c >= '0' && c <= '9') {
					size = size * 16 + (c - '0');
				} else if (c >= 'a' && c <= 'f') {
					size = size * 16 + (c - 'a' + 10);
				} else if (c >= 'A' && c <= 'F') {
					size = size * 16 + (c - 'A' + 10);
				}
			} else {
				if (size == 0) {
					// read the terminating CRLF
					byte c = buffer.get();
					if (!haveCR && c == 13) {
						haveCR = true;
					} else if (haveCR && c == 10) {
						haveCR = false;
						atSize = true;
						size = 0;
					} // else huh?
				} else {
					int p0 = buffer.position();
					int l0 = buffer.limit();
					int n0 = l0 - p0;
					int n = size <= n0 ? size : n0;
					int p1 = p0 + n;
					size -= n;
					
					buffer.limit(p1);
					
					fireData(buffer);
					
					buffer.position(p1);
					buffer.limit(l0);
				}
			}
		}
	}
	
}
