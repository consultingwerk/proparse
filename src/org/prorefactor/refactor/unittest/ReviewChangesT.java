///**
// * ReviewChangesT.java
// * @author John Green
// * 28-Oct-2002
// * www.joanju.com
// * 
// * Copyright (c) 2002 Joanju Limited.
// * All rights reserved. This program and the accompanying materials 
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// * 
// */
//
//package org.prorefactor.refactor.unittest;
//
//import java.io.IOException;
//
//import org.prorefactor.core.unittest.UnitTestBase;
//import org.prorefactor.core.unittest.UnitTestException;
//import org.prorefactor.refactor.ReviewChangesDialog;
//
//
//
//public class ReviewChangesT extends UnitTestBase {
//
//
//	/**
//	 * Constructor for ReviewChangesT.
//	 * @throws UnitTestException
//	 */
//	public ReviewChangesT() throws UnitTestException {
//		super();
//	}
//
//
//
//	public static void main(String[] args) {
//		System.out.println("Test the Review Changes dialog.");
//		try {
//			ReviewChangesT tester = new ReviewChangesT();
//			tester.runAllTests();
//			System.out.println("All tests passed.");
//		} catch (UnitTestException e) {
//			System.out.println("Review Changes dialog tests failed:");
//			System.out.println(e.getMessage());
//		}
//	} // main()
//
//
//
//	/**
//	 * @see org.prorefactor.core.unittest.UnitTestBase#runAllTests()
//	 */
//	public void runAllTests() throws UnitTestException {
//		test1();
//	} // runAllTests()
//
//
//
//	public void test1() throws UnitTestException {
//
//		// Restore our test file
//		try {
//			org.prorefactor.core.Util.fileCopy("data/substitute.orig.p", "data/substitute.p");
//		} catch (IOException e) {
//			throw new UnitTestException(e.getMessage());
//		}
//		ReviewChangesDialog reviewDialog = new ReviewChangesDialog(
//			"data/substitute.p", "data/substitute.mod.p"
//			);
//		int[] changedLines = {4, 4, 4, 4};
//		reviewDialog.setChangedLines(changedLines);
//		reviewDialog.open();
//		reviewDialog.getUserInput();
//	} // test1()
//
//
//
//} // class ReviewChangesT
//
