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
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.ServiceReference;

import doug.nutana.http.HttpServer;
import doug.nutana.http.HttpServerRequest;
import doug.nutana.http.HttpServerResponse;
import doug.nutana.net.Net;
import doug.nutana.net.Server;
import doug.nutana.net.Socket;

public class HttpServerImpl implements HttpServer {

	private final Server server;
	
	public HttpServerImpl() throws IOException {
		ServiceReference<Net> ref = Activator.getContext().getServiceReference(Net.class);
		Net net = Activator.getContext().getService(ref);
		server = net.createServer();
		server.onConnection(new Server.ConnectionListener() {
			@Override
			public void handleConnection(Socket socket) {
				new HttpServerRequestImpl(HttpServerImpl.this, socket);
			}
		});
		server.onError(new Server.ErrorListener() {
			@Override
			public void handleError(Throwable exception) {
				fireError(exception);
			}
		});
	}
	
	@Override
	public void listen(SocketAddress address) throws IOException {
		server.listen(address);
	}

	@Override
	public void close() throws IOException {
		server.close();
	}

	private List<RequestListener> requestListeners;
	
	@Override
	public void onRequest(RequestListener listener) {
		if (requestListeners == null)
			requestListeners = new LinkedList<>();
		requestListeners.add(listener);
	}
	
	protected void fireRequest(HttpServerRequest request, HttpServerResponse response) {
		if (requestListeners != null)
			for (RequestListener listener : requestListeners)
				listener.handleRequest(request, response);
	}

	public void handleRequest(HttpServerRequestImpl request) {
		Socket socket = request.getSocket();
		fireRequest(request, new HttpServerResponseImpl(socket));
		new HttpServerRequestImpl(this, socket);
	}
	
	private List<ErrorListener> errorListeners;
	
	@Override
	public void onError(ErrorListener listener) {
		if (errorListeners == null)
			errorListeners = new LinkedList<>();
		errorListeners.add(listener);
	}
	
	public void fireError(Throwable exception) {
		if (errorListeners != null)
			for (ErrorListener listener : errorListeners)
				listener.handleError(exception);
	}
	
}
