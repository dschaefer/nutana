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
package doug.nutana.net.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import doug.nutana.net.Net;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		context.registerService(Net.class, new NetImpl(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
