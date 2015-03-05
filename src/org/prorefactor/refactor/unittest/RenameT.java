///**
// * RenameT.java
// * @author John Green
// * 8-Sep-2004
// * www.joanju.com
// * 
// * Copyright (c) 2004 Joanju Limited.
// * All rights reserved. This program and the accompanying materials 
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// */
//
//package org.prorefactor.refactor.unittest;
//
//import java.io.File;
//
//import org.prorefactor.core.unittest.UnitTestBase2;
//import org.prorefactor.refactor.FileStuff;
//import org.prorefactor.refactor.RefactorSession;
//import org.prorefactor.refactor.action.RenameSchema;
//
//
//
///** Test org.prorefactor.refactor.action.RenameSchema */
//public class RenameT extends UnitTestBase2 {
//
//	public RenameT(String arg0) {
//		super(arg0);
//	}
//
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(RenameT.class);
//	}
//
//
//	
//	public void test01() throws Exception {
//		String expectFileName = "data/rename/t01/expect/t01.p";
//		String namePairs = "sports2000.Customer Client sports2000.Customer.name sports2000.Client.legalName sports2000.Invoice.amount totalAmount";
//		String outDir = "tmp";
//		String testFileName = "data/rename/t01/orig/t01.p";
//		RefactorSession rpack = RefactorSession.getInstance();
//		rpack.loadProject("sports2000");
//		String outFileName = FileStuff.prepareTarget(outDir, testFileName);
//		File outFile = new File(outFileName);
//		if (outFile.exists()) assertTrue(outFile.delete());
//		RenameSchema refactor = new RenameSchema(namePairs, outDir);
//		refactor.run(new File(testFileName), testFileName);
//		assertEquals("", parser.diff(expectFileName, outFileName));
//	}
//
//
//
//}
//
