/* ICallbackb.java
 * Created on Mar 2, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core;

/** Callback with a boolean value. */
public interface ICallbackb {

	public void run(boolean value);

	public static ICallbackb nullCallback = new ICallbackb() {
		public void run(boolean value) { }
	};

} // interface
