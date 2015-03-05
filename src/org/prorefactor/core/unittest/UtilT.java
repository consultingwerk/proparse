/* UtilT.java
 * Created on Feb 5, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class UtilT extends UnitTestBase2 {

	public UtilT(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(UtilT.class);
	}

	final public void testPermsList() {
		ArrayList testList = new ArrayList();
		testList.add(Arrays.asList(new String[] {"11", "12"}));
		testList.add(Arrays.asList(new String[] {"21"}));
		testList.add(Arrays.asList(new String[] {"31", "32", "33"}));
		ArrayList expectList = new ArrayList();
		expectList.add(Arrays.asList(new String[] {"11", "21", "31"}));
		expectList.add(Arrays.asList(new String[] {"11", "21", "32"}));
		expectList.add(Arrays.asList(new String[] {"11", "21", "33"}));
		expectList.add(Arrays.asList(new String[] {"12", "21", "31"}));
		expectList.add(Arrays.asList(new String[] {"12", "21", "32"}));
		expectList.add(Arrays.asList(new String[] {"12", "21", "33"}));
		List resultList = org.prorefactor.core.Util.permsList(testList);
		assertTrue(resultList.toString(), resultList.equals(expectList));
	}

}
