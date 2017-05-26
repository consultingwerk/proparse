/**
 * Authors: John Green
 * July 4, 2006.
 * 
 * Copyright (c) 2006-2011 Joanju (www.joanju.com).
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.core.unittest;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.treeparser.ParseUnit;


/** Test the tree parsers against problematic syntax.
 * These tests just run the tree parsers against the data/bugsfixed directory.
 * If no exceptions are thrown, then the tests pass.
 * The files in the "bugsfixed" directories are subject to change, so no other
 * tests should be added other than the expectation that they parse clean.
 */
public class BugFixTests extends TestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RefactorSession refpack = RefactorSession.getInstance();
		refpack.loadProjectForced("unittest");
	}

	public void test01() throws Exception {
		File directory = new File("data/bugsfixed");
		String [] extensions = {"p", "w", "cls"};
		Collection files = FileUtils.listFiles(directory, extensions, true);
		for (Iterator it = files.iterator(); it.hasNext();) {
			File file = (File) it.next();
			ParseUnit pu = new ParseUnit(file);
			pu.parse();
			//pu.treeParser(new JPTreeParser());
			pu.treeParser01();
		}
	}

}


/*
import org.prorefactor.core.JPNodeLister;
import org.prorefactor.core.TokenTypes;

			boolean debug = true;
			if (debug && file.getName().equals("bug06.p")) {
				JPNodeLister lister = new JPNodeLister(pu.getTopNode(), "C:\\temp\\nodelister.txt", new TokenTypes());
				lister.print();
			}

*/
