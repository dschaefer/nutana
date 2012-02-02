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
package doug.nutana.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.LinkedList;
import java.util.List;

import doug.nutana.core.internal.Activator;

public abstract class WriteStream {

	// writing
	
	protected LinkedList<ByteBuffer> pendingBuffers = new LinkedList<>();
	
	protected abstract void doWrite();
	
	public ByteBuffer getBuffer() {
		return Activator.getBufferManager().getBuffer();
	}
	
	protected void returnBuffer(ByteBuffer buffer) {
		Activator.getBufferManager().returnBuffer(buffer);
	}
	
	public void write(ByteBuffer buffer) {
		synchronized (pendingBuffers) {
			pendingBuffers.addLast(buffer);
			doWrite();
		}
	}

	public void write(CharBuffer charBuffer) {
		if (encoder == null)
			throw new IllegalStateException();
		CoderResult rc;
		do {
			ByteBuffer buffer = getBuffer();
			rc = encoder.encode(charBuffer, buffer, false);
			buffer.limit(buffer.position());
			buffer.rewind();
			write(buffer);
		} while (rc == CoderResult.OVERFLOW);
	}

	public void write(String text) {
		write(CharBuffer.wrap(text));
	}
	
	// ending
	
	public abstract void end();
	
	protected abstract void close() throws IOException;
	
	// encoding
	
	private CharsetEncoder encoder;
	
	public void setEncoding(Charset charset) {
		encoder = charset.newEncoder();
	}
	
	// on drain
	
	public interface DrainListener {
		void handleDrain();
	}
	
	private List<DrainListener> drainListeners;
	
	public void onDrain(DrainListener listener) {
		if (drainListeners == null)
			drainListeners = new LinkedList<>();
		drainListeners.add(listener);
	}
	
	protected void fireDrain() {
		if (drainListeners != null)
			for (DrainListener listener : drainListeners)
				listener.handleDrain();
	}

	// on error
	
	public interface ErrorListener {
		void handleError(Throwable exception);
	}
	
	private List<ErrorListener> errorListeners;
	
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
