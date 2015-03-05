/**
 * TreeParser01T.java
 * @author John Green
 * 6-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002, 2004 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.core.unittest;

import org.prorefactor.core.schema.Schema;
import org.prorefactor.refactor.RefactorSession;
import org.apache.commons.io.FileUtils;

import java.io.File;


public class TP01Test01 extends UnitTestBase2 {

	public TP01Test01(String arg0) {
		super(arg0);
	}

	String expectName = "data/tp01tests/test01.expect.p";
	String inName = "data/tp01tests/test01.p";
	String outName = "data/tp01tests/test01.out.p";
	String schemaName = "proparse.schema";



	public static void main(String[] args) {
		junit.textui.TestRunner.run(TP01Test01.class);
	}



	public void test01() throws Exception {
		RefactorSession.getInstance().loadProject("unittest");
		Schema schema = Schema.getInstance();
		schema.clear();
		schema.loadSchema(schemaName);
		AttributedWriter writer = new AttributedWriter();
		writer.write(inName, outName);
		assertTrue(FileUtils.contentEquals(new File(expectName), new File(outName)));
	}


}
