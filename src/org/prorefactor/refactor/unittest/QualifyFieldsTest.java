///**
// * ExtractMethodT.java
// * @author John Green
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
//import java.io.File;
//import java.io.FileWriter;
//
//import org.prorefactor.core.unittest.UnitTestBase2;
//import org.prorefactor.refactor.RefactorSession;
//import org.prorefactor.refactor.action.QualifyFieldsRefactor;
//
//
//
///**
// * Tester for org.prorefactor.refactor.extractmethod
// */
//public class QualifyFieldsTest extends UnitTestBase2 {
//
//	public QualifyFieldsTest(String arg0) {
//		super(arg0);
//	}
//
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(QualifyFieldsTest.class);
//	}
//
//	private RefactorSession refpack = RefactorSession.getInstance();
//
//	private File testDir01 = new File("data/qualifyfields/t01/test");
//
//
//
//	public void doTest01(String filename) throws Exception {
//		final String dir01 = "data/qualifyfields/t01/";
//		File source = new File(dir01 + "orig/" + filename);
//		File target = new File(dir01 + "test/" + filename);
//		File expect = new File(dir01 + "expect/" + filename);
//		QualifyFieldsRefactor refactor = new QualifyFieldsRefactor();
//		refactor.run(source, source);
//		String newCode = refactor.generateNewSource();
//		target.createNewFile();
//		FileWriter writer = new FileWriter(target);
//		writer.write(newCode);
//		writer.close();
//		assertEquals(null, super.testCompareSingle(expect, target));
//	}
//
//	
//	
//	public void test_init() throws Exception {
//		refpack.loadProject("sports2000");
//		testDir01.mkdirs();
//		org.prorefactor.core.Util.wipeDirectory(testDir01, false);
//	}
//
//	public void test01a() throws Exception { doTest01("t01a.p"); }
//
//	public void test01b() throws Exception { doTest01("t01b.p"); }
//
//	public void test01c() throws Exception { doTest01("t01c.p"); }
//
//	public void test01d() throws Exception { doTest01("t01d.p"); }
//
//	public void test01e() throws Exception { doTest01("t01e.p"); }
//
//	public void test01f() throws Exception { doTest01("t01f.p"); }
//
//	public void test01g() throws Exception { doTest01("t01g.p"); }
//
//	public void test01h() throws Exception { doTest01("t01h.p"); }
//
//	public void test01i() throws Exception { doTest01("t01i.p"); }
//
//	public void test01j() throws Exception { doTest01("t01j.p"); }
//
//
//} // class
