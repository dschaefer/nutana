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
package doug.nutana.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.junit.Test;
import org.osgi.framework.ServiceReference;

import doug.nutana.core.ReadStream;
import doug.nutana.core.WriteStream;
import doug.nutana.net.Net;
import doug.nutana.net.Server;
import doug.nutana.net.Socket;

public class EchoTest {

	private Throwable raisedException;
	
	@Test
	public void test1() throws Throwable {
		final int PORT = 8001;
		
		ServiceReference<Net> ref = Activator.getContext().getServiceReference(Net.class);
		Net net = Activator.getContext().getService(ref);
		
		Server echoServer = net.createServer();
		echoServer.onError(new Server.ErrorListener() {
			@Override
			public void handleError(Throwable exception) {
				setException(exception);
				test1end();
			}
		});
		echoServer.onConnection(new Server.ConnectionListener() {
			@Override
			public void handleConnection(final Socket socket) {
				socket.getReadStream().pipe(socket.getWriteStream());
			}
		});
		echoServer.listen(new InetSocketAddress(PORT));
		
		net.connect(new InetSocketAddress("localhost", PORT), new Net.ConnectListener() {
			@Override
			public void handleConnect(final Socket socket) {
				ReadStream readStream = socket.getReadStream();
				readStream.onData(new ReadStream.DataListener() {
					@Override
					public void handleData(ByteBuffer buffer) {
						test1checkBuffer(buffer);
						try {
							socket.close();
						} catch (IOException e) {
							setException(e);
						}
						test1end();
					}
				});
				readStream.onError(new ReadStream.ErrorListener() {
					@Override
					public void handleError(Throwable exception) {
						setException(exception);
						test1end();
					}
				});
				
				WriteStream writeStream = socket.getWriteStream();
				writeStream.onError(new WriteStream.ErrorListener() {
					@Override
					public void handleError(Throwable exception) {
						setException(exception);
						test1end();
					}
				});
				ByteBuffer buffer = writeStream.getBuffer();
				test1fillBuffer(buffer);
				writeStream.write(buffer);
			}
			
			@Override
			public void handleError(Throwable exception) {
				setException(exception);
				test1end();
			}
		});
		
		synchronized (this) {
			wait();
		}
		
		if (raisedException != null)
			throw raisedException;
	}

	void test1fillBuffer(ByteBuffer buffer) {
		for (byte b = 0; b < 10; ++b)
			buffer.put(b);
		buffer.limit(buffer.position());
		buffer.rewind();
	}
	
	void test1checkBuffer(ByteBuffer buffer) {
		try {
			for (byte b = 0; b < 10; ++b)
				Assert.assertEquals(b, buffer.get());
		} catch (Throwable exception) {
			raisedException = exception;
			return;
		}
	}
	
	synchronized void test1end() {
		notifyAll();
	}

	void setException(Throwable exception) {
		raisedException = exception;
		exception.printStackTrace();
	}
}
