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
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import doug.nutana.net.Net;
import doug.nutana.net.Server;

public class NetImpl implements Net {

	@Override
	public Server createServer() throws IOException {
		return new ServerImpl();
	}

	@Override
	public void connect(SocketAddress address, ConnectListener listener) throws IOException {
		final AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();

		socket.connect(address, listener, new CompletionHandler<Void, ConnectListener>() {
			@Override
			public void completed(Void result, ConnectListener listener) {
				listener.handleConnect(new SocketImpl(socket));
			}
			@Override
			public void failed(Throwable exc, ConnectListener listener) {
				listener.handleError(exc);
			}
		});
	}

}
