/**
 * PstringT.java
 * @author John Green
 * 24-Oct-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.core.unittest;

import org.prorefactor.core.Pstring;


public class PstringT extends UnitTestBase2 {

	public PstringT(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PstringT.class);
	}

	public void testBasicFunctions() {
		Pstring pstring = new Pstring("\"No more 'Hello world'!\":T");
		assertTrue(
				"Pstring.justText() failed"
			,	pstring.justText().equals("No more 'Hello world'!")
			);
	}

}
