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

import java.util.Map;

import doug.nutana.core.WriteStream;

public abstract class HttpServerResponse extends WriteStream {

	public abstract void setHeader(String key, String value);
	
	public abstract Map<String, String> getHeaders();
	
	public abstract void setTrailer(String key, String value);
	
	public abstract Map<String, String> getTrailers();

	public abstract void writeHead(int statusCode);
	
	public abstract void writeHead(int statusCode, String reason);
	
}
