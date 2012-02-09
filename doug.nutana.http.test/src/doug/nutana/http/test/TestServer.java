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

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import doug.nutana.http.Http;
import doug.nutana.http.HttpServer;
import doug.nutana.http.HttpServerRequest;
import doug.nutana.http.HttpServerResponse;

import static doug.nutana.http.test.Activator.require;

/**
 * Simple test server.
 */
public class TestServer implements IApplication {

	private int count = 0;
	
	@Override
	public Object start(IApplicationContext context) throws Exception {
		
		Http httpService = require(Http.class);
		
		HttpServer server = httpService.createServer();
		server.onRequest(new HttpServer.RequestListener() {
			@Override
			public void handleRequest(HttpServerRequest request, HttpServerResponse response) {
				if (request.getURL().equals("/")) {
					response.setHeader("Content-Type", "text/plain");
					response.writeHead(200);
					response.write("Hello from Nutana! " + (++count));
					response.end();
				} else if (request.getURL().equals("/end")) {
					response.setHeader("Content-Type", "text/plain");
					response.writeHead(200);
					response.write("Bye!");
					response.end();
					stop();
				} else {
					response.writeHead(404);
					response.end();
				}
			}
		});
		server.listen(8001);
		System.out.println("Listening...");

		synchronized (this) {
			wait();
		}
		
		try {
			// Wait for buffers to clear
			// TODO need a real handshake for this with the server
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public synchronized void stop() {
		notifyAll();
	}

}
