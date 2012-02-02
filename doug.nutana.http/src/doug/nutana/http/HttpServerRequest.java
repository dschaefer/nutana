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

import doug.nutana.core.ReadStream;

public abstract class HttpServerRequest extends ReadStream {

	public abstract String getMethod();
	
	public abstract String getURL();
	
	public abstract String getVersion();
	
	public abstract String getHeader(String key);
	
	public abstract Map<String, String> getHeaders();
	
	public abstract String getTrailer(String key);
	
	public abstract Map<String, String> getTrailers();
	
}
