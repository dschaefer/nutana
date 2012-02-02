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
package doug.nutana.http.internal;

import java.io.IOException;
import java.net.SocketAddress;

import doug.nutana.http.Http;
import doug.nutana.http.HttpClient;
import doug.nutana.http.HttpServer;

public class HttpImpl implements Http {

	@Override
	public HttpServer createServer() throws IOException {
		return new HttpServerImpl();
	}

	@Override
	public HttpClient createClient(SocketAddress address) {
		return null;
	}

}
