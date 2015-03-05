/* RefactorException.java
 * Created on Dec 6, 2003
 * John Green
 *
 * Copyright (C) 2003-2011 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.File;


/** Exception to be thrown only by the refactoring libraries,
 * especially ScanLib, Refactor, etc. These exceptions thrown
 * from the refactoring libraries are intended to help the
 * programmer (me!) more easily track down mistakes made when
 * writing new refactorings.
 */
public class RefactorException extends Exception {

	private static final long serialVersionUID = -8895158616697265317L;
	
	int [] filePos = null;
	File file = null;
	
	public RefactorException(String message) {
		super(message);
	}

	public RefactorException(Throwable cause) {
		super(cause);
	}

	public RefactorException(String message, Throwable cause) {
		super(message, cause);
	}

	/** Create an exception where we don't have a file index.
	 */
	public RefactorException(File file, int line, int col, String message) {
		super(	file.toString()
				+ ":" + Integer.toString(line)
				+ ":" + Integer.toString(col)
				+ " " + message
				);
		this.file = file;
		this.filePos = new int [] { -1, line, col};
	}


} // class
