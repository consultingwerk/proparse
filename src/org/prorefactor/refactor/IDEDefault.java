/* Created on Mar 26, 2006
 * John Green
 *
 * Copyright (C) 2006 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.File;


public class IDEDefault implements IDE {

	public IDEDefault(RefactorSession refpack) {
		this.refpack = refpack;
	}

	RefactorSession refpack;
	
	/** @see IDE#getProjectRelativePath(File)
	 * This implementation returns the current project name from RefactorSession,
	 * and getPath() from the input file.
	 */
	public String [] getProjectRelativePath(File file) {
		String [] ret = new String[2];
		ret[0] = refpack.getProjectName();
		ret[1] = file.getPath();
		return ret;
	}

}
