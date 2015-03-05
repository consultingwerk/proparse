/**
 * TreeParserWrapper.java
 * @author John Green
 * 18-Oct-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002-2008 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.PRCException;
import org.prorefactor.core.TokenTypes;



/**
 * This class just makes it easier to interface with
 * an Antlr generated tree parser.
 */
public class TreeParserWrapper {

	/** Run a tree parser for a given JPNode. 
	 * @throws PRCException
	 */
	public static void run2(IJPTreeParser tp, JPNode theAST) throws PRCException {
		try {
			tp.program(theAST);
		} catch (Throwable e) {
			JPNode n = (JPNode) tp.get_retTree();
			if (n == null) {
				JPNode firstNatural = theAST.firstNaturalChild();
				String s2 = "";
				if (firstNatural != null)
					s2 = firstNatural.getFilename() + org.prorefactor.core.Util.LINESEP;
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				s2 += sw.toString();
				throw new PRCException(s2, e);
			}
			String s = e.getMessage();
			if (n != null) {
				boolean done = false;
				while (!done) {
					s	+=	" -> File: " + n.getFilename()
						+	" Line: " + n.getLine()
						+	" Column: " + n.getColumn()
						+	" Type: " + TokenTypes.getTokenName(n.getType())
						+	" Text: " + n.getText()
						;
					if (n.getLine()==0) {
						n = n.firstChild();
						if (n==null) {
							// this shouldn't happen, but we'll check anyway.
							done = true;
						}
					} else
						done = true;
				}
			}
			throw new PRCException(s, e);
		}
	}

}
