/**
 * PUBTest.java
 * @author John Green
 * Sep 1, 2004
 * www.joanju.com
 *
 * Copyright (C) 2004 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor.unittest;

import java.io.File;
import java.util.ArrayList;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.TokenTypes;
import org.prorefactor.core.unittest.UnitTestBase2;
import org.prorefactor.refactor.PUB;
import org.prorefactor.refactor.RefactorSession;




/** Test "Parse Unit Binary" files.
 */
public class PUBTest extends UnitTestBase2 {

	public PUBTest(String arg0) {
		super(arg0);
	}

	private RefactorSession refpack = RefactorSession.getInstance();
	private String relPath = "data/pub/test01.p";
	private File parseFile;
	private PUB pub;
		
	public static void main(String[] args) {
		junit.textui.TestRunner.run(PUBTest.class);
	}

	protected void setUp() throws Exception {
		refpack.loadProject("sports2000");

		RefactorSession.getInstance().setProjectBinariesEnabledOn();
		parseFile = new File(relPath);
		pub = new PUB(parseFile.getCanonicalPath());
	}
	
	public void testBuild() throws Exception {
		pub.build();
		pub = new PUB(parseFile.getCanonicalPath());
		assertTrue(pub.load());
	}

	public void testIncludeName() throws Exception {
		assertTrue(pub.load());
		String [] fileIndex = pub.getTree().getFilenames();
		
		// Test that file at index 1 matches the include file name that we expect
		File iGet = new File(fileIndex[1]);
		File iBase = new File("data/pub/test01.i");
		assertTrue(iGet.getCanonicalPath().equals(iBase.getCanonicalPath()));
	}

	public void testTimeStamp() throws Exception {

		// Test that the file timestamp checking works
		long origTime = parseFile.lastModified();
		assertTrue(parseFile.setLastModified(System.currentTimeMillis() + 10000));
		assertFalse(pub.load());
		assertTrue(parseFile.setLastModified(origTime));
		assertTrue(pub.load());
		
	}

	public void testIncludeTimeStamp() throws Exception {
		// Test that the file timestamp checking works on included files
		File iBase = new File("data/pub/test01.i");
		long origTime = iBase.lastModified();
		iBase.setLastModified(System.currentTimeMillis() + 10000);
		assertFalse(pub.load());
		iBase.setLastModified(origTime);
		assertTrue(pub.load());
		
	}

	public void testSchemaLoad() throws Exception {
		assertTrue(pub.load());

		// Test that the schema load works
		ArrayList tables = new ArrayList();
		pub.copySchemaTableLowercaseNamesInto(tables);
		assertTrue(tables.size() == 1);
		assertTrue(tables.get(0).toString().equals("sports2000.customer"));
		ArrayList fields = new ArrayList();
		pub.copySchemaFieldLowercaseNamesInto(fields, "sports2000.customer");
		assertTrue(fields.size() == 1);
		assertTrue(fields.get(0).toString().equals("name"));

	}

	public void testImportTable() throws Exception {
		assertTrue(pub.load());

		// Test the import table.
		PUB.SymbolRef [] imports = pub.getImportTable();
		PUB.SymbolRef imp = imports[0];
		assertTrue(imp.progressType == TokenTypes.VARIABLE);
		assertTrue(imp.symbolName.equals("sharedChar"));
		
	}

	public void testExportTable() throws Exception {
		assertTrue(pub.load());

		// Test the export table.
		PUB.SymbolRef [] exports = pub.getExportTable();
		PUB.SymbolRef exp = exports[0];
		assertTrue(exp.progressType == TokenTypes.FRAME);
		assertTrue(exp.symbolName.equals("myFrame"));
		
	}

	public void testComments() throws Exception {
		assertTrue(pub.load());

		// Test that there are comments in front of the first real node
		JPNode topNode = pub.getTree();
		assertTrue(topNode.firstNaturalChild().getComments().length() > 2);
		
	}

	public void testText() throws Exception {
		assertTrue(pub.load());

		// Test that the ID nodes have text.
		JPNode topNode = pub.getTree();
		for (JPNode node : topNode.query(TokenTypes.ID)) {
			assertTrue(node.getText().length() > 0);
		}
		
	}
	
}
