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

public interface Net {

	Server createServer() throws IOException;

	interface ConnectListener {
		void handleConnect(Socket socket);
		void handleError(Throwable exception);
	}
	
	void connect(SocketAddress address, ConnectListener listener) throws IOException;
	
}
