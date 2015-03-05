///**
// * NamesT.java
// * @author John Green
// * 25-Oct-2003
// * www.joanju.com
// * 
// * Copyright (c) 2003-2004 Joanju Limited.
// * All rights reserved. This program and the accompanying materials 
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// * 
// */
//
//package org.prorefactor.refactor.unittest;
//
//
//import java.io.File;
//
//import org.prorefactor.core.unittest.UnitTestBase2;
//import org.prorefactor.refactor.FileStuff;
//import org.prorefactor.refactor.RefactorSession;
//import org.prorefactor.refactor.TempDirWrap;
//import org.prorefactor.refactor.tfnames.NamesLint;
//import org.prorefactor.refactor.tfnames.TFNamesRefactor;
//
//import com.joanju.ProparseLdr;
//
//
//
///**
// * Tester for org.prorefactor.refactor.names
// */
//public class NamesT extends UnitTestBase2 {
//
//	public NamesT(String arg0) {
//		super(arg0);
//	}
//
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(NamesT.class);
//	}
//
//	private ProparseLdr parser = ProparseLdr.getInstance();
//	private RefactorSession rpack = RefactorSession.getInstance();
//	private String outDir = "tmp";
//
//
//
//	private void parserErrCheck() {
//		if (parser.errorGetStatus() != 0)
//			fail(parser.errorGetText());
//	}
//
//
//
//	private String refactorFile(String testFileName, TempDirWrap wrap, TFNamesRefactor refactor)
//			throws Exception {
//		rpack.loadProject("sports2000");
//		NamesLint lint = new NamesLint(refactor);
//		String outFileName = FileStuff.prepareTarget(outDir, testFileName);
//		File outFile = new File(outFileName);
//		outFile.delete();
//		parser.parse(testFileName);
//		parserErrCheck();
//		int topNode = parser.getHandle();
//		parser.nodeTop(topNode);
//		String output = wrap.run(topNode, lint, refactor);
//		parserErrCheck();
//		return output;
//	}
//
//
//
//	/**
//	 * Test TempDirWrap.run()
//	 */
//	public void test1() throws Exception {
//		String testFileName = "data/names.p";
//		String outFileName = FileStuff.prepareTarget(outDir, testFileName);
//		TempDirWrap wrap = new TempDirWrap(outDir);
//		TFNamesRefactor refactor = new TFNamesRefactor();
//		refactor.fixCase = true;
//		refactor.unabbreviate = true;
//		refactor.useLowercase = true;
//		String output = refactorFile(testFileName, wrap, refactor);
//		if (output==null) output = "";
//		assertEquals("", output);
//		assertEquals("", parser.diff("data/names.expect.p", outFileName));
//	} // test1()
//
//
//
//	// This tests the handling of multiple outcomes for a single include file.
//	public void test2() throws Exception {
//		String expectMessage = "Include file refactoring has multiple outcomes, file not written:";
//		File messagesFile = new File(RefactorSession.getMessagesFileName());
//		messagesFile.delete();
//		TempDirWrap wrap = new TempDirWrap(outDir);
//		TFNamesRefactor refactor = new TFNamesRefactor();
//		refactor.fixCase = true;
//		refactor.unabbreviate = true;
//		refactor.useLowercase = true;
//		refactorFile("data/names/billto.p", wrap, refactor);
//		String message = refactorFile("data/names/customer.p", wrap, refactor);
//		assertEquals(expectMessage, message.substring(31, 96));
//		refactorFile("data/names/shipto.p", wrap, refactor);
//		assertTrue(messagesFile.exists());
//	}
//
//
//
//}
//
