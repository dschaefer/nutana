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
package doug.nutana.core.internal;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class BufferManager {

	protected LinkedList<ByteBuffer> emptyBuffers = new LinkedList<>();

	public ByteBuffer getBuffer() {
		synchronized (emptyBuffers) {
			if (emptyBuffers.isEmpty())
				return ByteBuffer.allocateDirect(1024);
			else
				return emptyBuffers.removeFirst();
		}
	}
	
	public void returnBuffer(ByteBuffer buffer) {
		buffer.clear();
		synchronized (emptyBuffers) {
			emptyBuffers.add(buffer);
			// TODO fancier buffer management
		}
	}
	
}
