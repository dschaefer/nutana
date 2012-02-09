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
package doug.nutana.net.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import doug.nutana.core.Service;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static <T> T require(Class<T> clazz) {
		return Service.require(context, clazz);
    }

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
