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
package doug.nutana.http.test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.ServiceReference;

import doug.nutana.core.WriteStream;
import doug.nutana.http.Http;
import doug.nutana.http.HttpServer;
import doug.nutana.http.HttpServerRequest;
import doug.nutana.http.HttpServerResponse;

/**
 * Simple test server.
 */
public class TestServer implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		ServiceReference<Http> ref = Activator.getContext().getServiceReference(Http.class);
		Http httpService = Activator.getContext().getService(ref);
		
		HttpServer server = httpService.createServer();
		server.onRequest(new HttpServer.RequestListener() {
			@Override
			public void handleRequest(HttpServerRequest request, HttpServerResponse response) {
				response.setHeader("Content-Type", "text/plain");
				response.writeHead(200);
				response.setEncoding(Charset.forName("UTF-8"));
				response.write("Hello from Nutana!");
				response.end();
			}
		});
		server.listen(new InetSocketAddress(8001));
		System.out.println("Listening...");

		synchronized (this) {
			wait();
		}
		
		return 0;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
