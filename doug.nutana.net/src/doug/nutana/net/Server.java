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
package doug.nutana.net;

import java.io.IOException;
import java.net.SocketAddress;

public interface Server {

	// control
	
	void listen(SocketAddress address) throws IOException;
	
	void close() throws IOException;
	
	// on connection
	
	interface ConnectionListener {
		void handleConnection(Socket socket);
	}
	
	void onConnection(ConnectionListener listener);

	// on error
	
	interface ErrorListener {
		void handleError(Throwable exception);
	}
	
	void onError(ErrorListener listener);
	
}
