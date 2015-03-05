/**
 * RunHandle.java
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

/**
 * Represents a procedure handle value, used in a run
 * statement of the form: run <proc> in <handle>.
 *
 */
public class RunHandle implements Value {

	private String fileName;

	/**
	 * @param string
	 */
	public void setValue(Object fileName) {
		this.fileName = (String) fileName;
	}

	/**
	 * Get the name of the external procedure
	 * associated with the runHandle
	 * @return
	 */
	public Object getValue() {
		return fileName;
	}

}
