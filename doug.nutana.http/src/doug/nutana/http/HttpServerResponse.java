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

public interface HttpServerResponse {

	void setHeader(String key, String value);
	
	Map<String, String> getHeaders();
	
	void setTrailer(String key, String value);
	
	Map<String, String> getTrailers();

	void writeHead(int statusCode);
	
	void writeHead(int statusCode, String reason);
	
	WriteStream getWriteStream();
	
}
