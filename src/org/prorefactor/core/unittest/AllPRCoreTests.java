/**
 * AllTests.java
 * @author John Green
 * 6-Nov-2002
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


import junit.framework.Test;
import junit.framework.TestSuite;


public class AllPRCoreTests {


	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.prorefactor.core.unittest");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(AliasesT.class));
		suite.addTest(new TestSuite(PstringT.class));
		suite.addTest(new TestSuite(TP01Test01.class));
		suite.addTest(new TestSuite(TP01Test02.class));
		suite.addTest(new TestSuite(TP01Test03.class));
		suite.addTest(new TestSuite(TP01SymbolActionTest.class));
		suite.addTest(new TestSuite(TP01ProcessActionTest.class));
		suite.addTest(new TestSuite(TP01FramesTest.class));
		suite.addTest(new TestSuite(BugFixTests.class));
		suite.addTest(new TestSuite(TestNewSyntax.class));
		//$JUnit-END$
		return suite;
	}

} // class AllTests
