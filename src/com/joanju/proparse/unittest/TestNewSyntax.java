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
public class TestNewSyntax extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Environment env = Environment.instance();
		env.configSet("proversion", "11.7");
	}

	public void test01() throws Exception {
		File directory = new File("data/newsyntax/11.7");
		String[] extensions = { "p", "w", "cls" };
		Collection files = FileUtils.listFiles(directory, extensions, true);
		for (Iterator it = files.iterator(); it.hasNext();) {
			File file = (File) it.next();
			System.out.println("Parse: " + file.getAbsolutePath());

			DoParse pu = new DoParse(file.getAbsolutePath());
			pu.doParse();

			// pu.treeParser01();
			// TreeParser03 tp3 = new TreeParser03();
			// tp3.setSupport(new TP03Support());
			// pu.treeParser(tp3);
		}
	}
	
	public void test02() throws Exception {
		File directory = new File("data/newsyntax/11.7");
		String[] extensions = { "p", "w", "cls" };
		Collection files = FileUtils.listFiles(directory, extensions, true);
		RefactorSession.getInstance().setProjectBinariesEnabledOn();
		
		for (Iterator it = files.iterator(); it.hasNext();) {
			File file = (File) it.next();
			System.out.println("Parse: " + file.getAbsolutePath());

			ParseUnit pu = new ParseUnit(file);
			pu.treeParser01();

			// TreeParser03 tp3 = new TreeParser03();
			// tp3.setSupport(new TP03Support());
			// pu.treeParser(tp3);
		}
	}

}
