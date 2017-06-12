/**
 * Authors: John Green
 * April 23, 2007.
 * 
 * Copyright (c) 2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.joanju.proparse.unittest;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.DoParse;
import com.joanju.proparse.Environment;

/**
 * Test all tree parsers against new syntax. These tests just run the tree
 * parsers against the data/newsyntax directory. If no exceptions are thrown,
 * then the tests pass. The files in the "newsyntax" directories are subject to
 * change, so no other tests should be added other than the expectation that
 * they parse clean.
 */
public class TestIncludeRef extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	public void test01() throws Exception {
		File directory = new File("data/newsyntax/Consultingwerk");
		String[] extensions = { "p", "w", "cls" };
		Environment.instance().configSet("propath", "/Users/marian/Work/acorn/projects/proparse-mike/data/newsyntax");

		Collection files = FileUtils.listFiles(directory, extensions, true);
		for (Iterator it = files.iterator(); it.hasNext();) {
			File file = (File) it.next();
			System.out.println("Parse: " + file.getAbsolutePath());

			DoParse du = new DoParse(file.getAbsolutePath());
			du.doParse();

			ParseUnit pu = new ParseUnit(file);
			pu.treeParser01();
		}
	}
	


}
