/**
 * AllProRefactorTests.java
 * @author John Green
 * 16-Mar-2006
 * www.joanju.com
 * 
 * Copyright (c) 2006 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.core.unittest;


import junit.framework.Test;
import junit.framework.TestSuite;
// TODO add back, or split out
// import org.prorefactor.refactor.unittest.AllRefactorTests;


public class AllProRefactorTests {


	public static Test suite() {
		TestSuite suite = new TestSuite("All ProRefactor core and refactoring tests");
		//$JUnit-BEGIN$
		suite.addTest(AllPRCoreTests.suite());
//		suite.addTest(AllRefactorTests.suite());
		//$JUnit-END$
		return suite;
	}

}
