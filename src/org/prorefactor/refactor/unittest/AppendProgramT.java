///**
// * AppendProgramT.java
// * @author John Green
// * 06-Jan-2004
// * www.joanju.com
// * 
// * Copyright (c) 2004 Joanju Limited.
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
//
//import java.io.File;
//
//import org.prorefactor.core.unittest.UnitTestBase2;
//import org.prorefactor.refactor.RefactorSession;
//import org.prorefactor.refactor.Rollback;
//import org.prorefactor.refactor.appendprogram.AppendProgram;
//
///**
// * Tester for org.prorefactor.refactor.appendprogram
// */
//public class AppendProgramT extends UnitTestBase2 {
//
//	public AppendProgramT(String arg0) {
//		super(arg0);
//	}
//
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(AppendProgramT.class);
//	}
//
//	private RefactorSession refpack = RefactorSession.getInstance();
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		try {
//			refpack.loadProject("sports2000");
//			refpack.enableParserListing();
//		} catch (Exception e) {
//			fail(org.prorefactor.core.Util.getExceptionText(e));
//		}
//	}
//
//
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		refpack.disableParserListing();
//	}
//
//
//
//	public void test1() throws Exception {
//		File expectDir = new File("data/appendprogram/t01/expect");
//		File origDir = new File("data/appendprogram/t01/orig");
//		File testDir = new File("data/appendprogram/t01/test");
//		File file1 = new File("data/appendprogram/t01/test/t01.p");
//		File file2 = new File("data/appendprogram/t01/test/t01b.p");
//		testDir.mkdirs();
//
//		org.prorefactor.core.Util.wipeDirectory(testDir, false);
//		org.prorefactor.core.Util.copyAllFiles(origDir, testDir);
//
//		AppendProgram refactor = new AppendProgram(new Rollback());
//		refactor.run(file1, file2);
//		
//		assertEquals(null, super.testCompareFiles(expectDir, testDir));
//
//		// We expect a certain number of messages from this refactoring...
//		assertEquals(1, refactor.messageList.size());
//		// Bubble Declarations is called by Append Program. Check it for String messages.
//		assertEquals(0, refactor.bubbler.messages.size());
//	}
//
//
//
//} // class
