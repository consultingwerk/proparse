///**
// * NoUndoT.java
// * @author John Green
// * 11-Jul-2003
// * www.joanju.com
// * 
// * Copyright (c) 2003 Joanju Limited.
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
//import org.prorefactor.core.unittest.UnitTestBase2;
//import org.prorefactor.refactor.*;
//import org.prorefactor.refactor.noundo.*;
//
//import com.joanju.ProparseLdr;
//
//
//
///**
// * Tester for org.prorefactor.refactor.noundo
// */
//public class NoUndoT extends UnitTestBase2 {
//
//	public NoUndoT(String arg0) {
//		super(arg0);
//		loadParser();
//	}
//
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(NoUndoT.class);
//	}
//
//	private ProparseLdr parser;
//
//	private void loadParser() {
//		try {
//			parser = ProparseLdr.getInstance();
//		} catch (Throwable e) {
//			fail("Failed to load proparse.dll");
//		}
//	}
//
//	private void parserErrCheck() {
//		if (parser.errorGetStatus() != 0)
//			fail(parser.errorGetText());
//	}
//
//	/**
//	 * Test NoundoWrap.run()
//	 */
//	public void testNoUndo() {
//		String testFile = "data/no-undo.p";
//		String outDir = "tmp";
//		parser.configSet("show-proparse-directives", "true");
//		// We look to the project settings for whether cap keywords are used.
//		// Determines "NO-UNDO" vs. "no-undo".
//		RefactorSession refPack = RefactorSession.getInstance();
//		try {
//			refPack.loadProject("unittest");
//		} catch (Exception e) {
//			fail("Failed to load project settings: " + e.getMessage());
//		}
//		NoundoWrap wrap = new NoundoWrap(outDir);
//		String retVal = wrap.run(testFile);
//		if (retVal!=null) assertEquals("", retVal);
//		parserErrCheck();
//		String outFile = FileStuff.prepareTarget(outDir, testFile);
//		assertEquals("", parser.diff("data/no-undo.expect.p", outFile));
//	} // testNoUndo()
//
//} // class NoUndoT
//
