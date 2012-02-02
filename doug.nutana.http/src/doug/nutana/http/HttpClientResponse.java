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

public interface HttpClientResponse {

	int getStatus();
	
	String getReason();
	
	String getVersion();
	
	String getHeader(String key);
	
	Map<String, String> getHeaders();
	
	String getTrailer(String key);
	
	Map<String, String> getTrailers();
	
	ReadStream getReadStream();
	
}
