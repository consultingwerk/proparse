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
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//
//import org.prorefactor.core.Util;
//import org.prorefactor.core.unittest.UnitTestBase2;
//import org.prorefactor.refactor.RefactorSession;
//import org.prorefactor.refactor.action.ExtractMethod;
//import org.prorefactor.refactor.settings.ProparseProjectSettings;
//
//
//
///**
// * Tester for org.prorefactor.refactor.extractmethod
// */
//public class ExtractMethodT extends UnitTestBase2 {
//
//	public ExtractMethodT(String arg0) {
//		super(arg0);
//	}
//
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(ExtractMethodT.class);
//	}
//
//	private RefactorSession refpack = RefactorSession.getInstance();
//
//
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		refpack.loadProject("sports2000");
//		ProparseProjectSettings projectSettings = refpack.getProparseSettings();
//		projectSettings.indentTab = true;
//		projectSettings.capKeyword = false;
//	}
//
//
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//
//
//	public void test1() throws Exception {
//		// TODO At some point, I'm going to want to have a conversion tool
//		// for:  line/col  <-->  offset.
//		// Right now I just use UltraEdit in Hex mode.
//		// See org.eclipse.jface.text.IDocument and org.prorefactor.eclipse.ProRefactorEclipse.
//
//		int beginOffset = 321;
//		int endOffset = 496;		
//		int [] beginPos = new int[]{17,1};
//		int [] endPos = new int[]{25,0};
//		File expectDir = new File("data/extractmethod/t01/expect");
//		File origDir = new File("data/extractmethod/t01/orig");
//		File testDir = new File("data/extractmethod/t01/test");
//		File testFile = new File("data/extractmethod/t01/test/t01a.p");
//		String newMethodName = "my_prog";
//
//		testDir.mkdirs();
//		Util.wipeDirectory(testDir, false);
//		Util.copyAllFiles(origDir, testDir);
//		
//		ExtractMethod refactor = new ExtractMethod(null);
//		refactor.run(testFile, beginPos, endPos, testFile);
//		refactor.setMethodName(newMethodName);
//
//		StringBuffer buff = Util.readFile(testFile);
//		refactor.setSelectedText(buff.substring(beginOffset, endOffset));
//		buff.replace(beginOffset, endOffset, refactor.generateCallText());
//		buff.append(refactor.generateMethodText());
//		BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
//		writer.write(buff.toString());
//		writer.close();
//
//		assertEquals(null, super.testCompareFiles(expectDir, testDir));
//	}
//
//
//
//}
