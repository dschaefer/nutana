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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;

import doug.nutana.core.ReadStream;
import doug.nutana.core.WriteStream;
import doug.nutana.net.Socket;

public class SocketImpl implements Socket {

	private final AsynchronousSocketChannel socket;
	
	public SocketImpl(AsynchronousSocketChannel socket) {
		this.socket = socket;
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
	}
	
	private class SocketReadStream extends ReadStream {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		@Override
		public synchronized void pause() {
			paused = true;
		}

		@Override
		public synchronized void resume() {
			if (!paused)
				return;
			paused = false;
			
			buffer.clear();
			socket.read(buffer, null, new CompletionHandler<Integer, Void>() {
				@Override
				public void completed(Integer result, Void attachment) {
					buffer.limit(buffer.position());
					buffer.rewind();
					while (buffer.hasRemaining())
						fireData(buffer);
					synchronized (SocketReadStream.this) {
						if (!paused) {
							buffer.clear();
							socket.read(buffer, null, this);
						}
					}
				}
				@Override
				public void failed(Throwable exc, Void attachment) {
					paused = true;
					if (exc instanceof ClosedChannelException) {
						fireEnd();
					} else {
						fireError(exc);
					}
				}
			});
		}

		@Override
		public void close() throws IOException {
			socket.close();
		}
		
	}
	
	private final SocketReadStream readStream = new SocketReadStream();

	@Override
	public ReadStream getReadStream() {
		return readStream;
	}

	private class SocketWriteStream extends WriteStream {

		@Override
		protected void doWrite() {
			// pendingBuffers already locked
			ByteBuffer buffer = pendingBuffers.removeFirst();
			socket.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					returnBuffer(buffer);
					synchronized (pendingBuffers) {
						if (pendingBuffers.isEmpty()) {
							fireDrain();
						} else {
							ByteBuffer nextBuffer = pendingBuffers.removeFirst();
							socket.write(nextBuffer, nextBuffer, this);
						}
					}
				}
				@Override
				public void failed(Throwable exc, ByteBuffer buffer) {
					returnBuffer(buffer);
					fireError(exc);
				}
			});
		}

		@Override
		public void end() {
			// No special handling for end in regular sockets
		}

		@Override
		protected void close() throws IOException {
			socket.close();
		}
		
	}
	
	private final SocketWriteStream writeStream = new SocketWriteStream();
	
	@Override
	public WriteStream getWriteStream() {
		return writeStream;
	}
	
}
