/*******************************************************************************
 * Copyright (c) 2012 Mariot Chauvin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mariot Chauvin - Initial API and implementation
 *******************************************************************************/


package doug.nutana.core;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public final class Service {

    public static <T> T require(BundleContext context, Class<T> clazz) {
        ServiceReference<T> ref = context.getServiceReference(clazz);
        return context.getService(ref);
    }
}    