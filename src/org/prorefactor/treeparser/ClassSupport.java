/* July 2010 by John Green.
 * Copyright (c) 2010 Joanju Software (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparser;

import org.prorefactor.core.JPNode;

public class ClassSupport {

	/** This little method is used during tree parsing by both Variable and Field. */
	public static String qualifiedClassName(JPNode typeNameNode) {
		String s = typeNameNode.attrGetS(JPNode.AK_QUALIFIEDCLASS);
		if (s != null && s.length() > 0)
			return s;
		else
			return typeNameNode.getText();
	}

}
