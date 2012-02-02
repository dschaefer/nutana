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
package doug.nutana.http;

import java.io.IOException;
import java.net.SocketAddress;

public interface HttpServer {

	void listen(SocketAddress address) throws IOException;

	void close() throws IOException;
	
	interface RequestListener {
		void handleRequest(HttpServerRequest request, HttpServerResponse response);
	}
	
	void onRequest(RequestListener listener);
	
	interface ErrorListener {
		void handleError(Throwable exc);
	}
	
	void onError(ErrorListener listener);
	
}
