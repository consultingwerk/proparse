/**
 * Expression.java
 * @author Peter Dalbadie
 * 21-Sep-2004
 * 
 * Copyright (c) 2004,2006 ProRefactor.org.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import org.prorefactor.core.JPNode;


/**
 * @author pcd
 */
public class Expression extends SemanticRecord {

	private Object value;

	/**
	 * @param node
	 */
	public Expression(JPNode node) {
		super(node);
	}
	
	/**
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}
}
