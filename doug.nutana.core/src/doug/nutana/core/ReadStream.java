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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public abstract class ReadStream {

	// control
	
	protected boolean paused = true;
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void close() throws IOException;
	
	public void pipe(final WriteStream writeStream) {
		onData(new DataListener() {
			@Override
			public void handleData(ByteBuffer buffer) {
				writeStream.write(buffer);
			}
		});
	}
	
	// encoding
	
	private CharsetDecoder decoder;
	private CharBuffer charBuffer;
	
	public void setEncoding(Charset charset) {
		decoder = charset.newDecoder();
		charBuffer = CharBuffer.allocate(1024);
	}
	
	public void clearEncoding() {
		decoder = null;
		charBuffer = null;
	}
	
	// on data
	
	public interface DataListener {
		void handleData(ByteBuffer buffer);
	}
	
	private List<DataListener> dataListeners;
	
	public void onData(DataListener listener) {
		onData(listener, false);
	}
	
	public void onData(DataListener listener, boolean keepPaused) {
		if (dataListeners == null)
			dataListeners = new LinkedList<>();
		dataListeners.add(listener);
		if (!keepPaused)
			resume();
	}

	protected void fireData(ByteBuffer buffer) {
		if (decoder == null) {
			if (dataListeners != null) {
				for (DataListener listener : dataListeners)
					listener.handleData(buffer);
				return;
			}
		} else {
			if (textListeners != null) {
				buffer.limit(buffer.position());
				buffer.rewind();
				
				CoderResult rc;
				do {
					charBuffer.clear();
					rc = decoder.decode(buffer, charBuffer, false);
					charBuffer.limit(charBuffer.position());
					charBuffer.rewind();
					for (TextListener listener : textListeners)
						listener.handleText(charBuffer);
					// TODO what happens if a listener clears encoding
					// or charBuffer not drained?
				} while (rc== CoderResult.OVERFLOW);
				
				return;
			}
		}
		
		// no listeners, eat the buffer
		buffer.position(buffer.limit());
	}

	// on text
	
	public interface TextListener {
		void handleText(CharBuffer charBuffer);
	}
	
	private List<TextListener> textListeners;
	
	public void onText(TextListener listener) {
		onText(listener, false);
	}
	
	public void onText(TextListener listener, boolean keepPaused) {
		if (textListeners == null)
			textListeners = new LinkedList<>();
		textListeners.add(listener);
		if (!keepPaused)
			resume();
	}
	
	// on end
	
	public interface EndListener {
		void handleEnd();
	}
	
	private List<EndListener> endListeners;
	
	public void onEnd(EndListener listener) {
		if (endListeners == null)
			endListeners = new LinkedList<>();
		endListeners.add(listener);
	}

	protected void fireEnd() {
		if (endListeners != null)
			for (EndListener listener : endListeners)
				listener.handleEnd();
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
