/* FileChange.java
 * Created on Dec 2, 2003
 * John Green
 *
 * Copyright (C) 2003 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.File;


/** An object of this class is used for tracking a file change
 * (new, modify, delete) made by automated refactorings.
 */
public class FileChange {
	public FileChange(int what, File source, File preserved, String ref) {
		whatHappened = what;
		sourceFile = source;
		preservedFile = preserved;
		refname = ref;
	}
	public static final int NEW = 0;
	public static final int DELETED = 1;
	public static final int MODIFIED = 2;
	public static final String [] whatLabel = {"New", "Deleted", "Modified"};
	public int whatHappened;
	public File sourceFile = null;
	public File preservedFile = null;
	/** The name that was originally used for referring to this file */
	public String refname;

} // FileChange
