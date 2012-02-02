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
package doug.nutana.net.internal;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.List;

import doug.nutana.net.Server;
import doug.nutana.net.Socket;

/**
 * @author Dad
 *
 */
public class ServerImpl implements Server {

	private final AsynchronousServerSocketChannel server;
	
	public ServerImpl() throws IOException {
		server = AsynchronousServerSocketChannel.open();
	}

	@Override
	public void listen(SocketAddress address) throws IOException {
		server.bind(address).accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			@Override
			public void completed(AsynchronousSocketChannel socket, Void attachment) {
				fireConnection(new SocketImpl(socket));
			};
			@Override
			public void failed(Throwable exc, Void attachment) {
				fireError(exc);
			}
		});
	}

	@Override
	public void close() throws IOException {
		server.close();
	}
	
	private List<ConnectionListener> connectionListeners;
	
	@Override
	public void onConnection(ConnectionListener listener) {
		if (connectionListeners == null)
			connectionListeners = new LinkedList<>();
		connectionListeners.add(listener);
	}
	
	protected void fireConnection(Socket socket) {
		if (connectionListeners != null)
			for (ConnectionListener listener : connectionListeners)
				listener.handleConnection(socket);
	}

	private List<ErrorListener> errorListeners;
	
	@Override
	public void onError(ErrorListener listener) {
		if (errorListeners == null)
			errorListeners = new LinkedList<>();
		errorListeners.add(listener);
	}
	
	protected void fireError(Throwable exception) {
		if (errorListeners != null)
			for (ErrorListener listener : errorListeners)
				listener.handleError(exception);
	}
	
}
