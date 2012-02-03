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

import java.nio.ByteBuffer;

import doug.nutana.core.ReadStream;
import doug.nutana.net.Socket;

/**
 * Manages multiple requests on a socket connection.
 */
public class HttpConnection {

	private final HttpServerImpl server;
	private final Socket socket;
	
	private HttpServerRequestImpl currentRequest;
	
	public HttpConnection(HttpServerImpl server, Socket socket) {
		this.server = server;
		this.socket = socket;
		
		currentRequest = new HttpServerRequestImpl(this);
		
		socket.getReadStream().onData(new ReadStream.DataListener() {
			@Override
			public void handleData(ByteBuffer buffer) {
				currentRequest.handleData(buffer);
			}
		});

	}
	
	public void handleRequest(HttpServerRequestImpl request) {
		server.fireRequest(request, new HttpServerResponseImpl(socket));
		currentRequest = new HttpServerRequestImpl(this);
	}
	
}
