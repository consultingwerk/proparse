/* ILint.java
 * Created on Oct 25, 2003
 * John Green
 *
 * Copyright (C) 2003 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.util.TreeSet;


/**
 * Interface to a Lint object.
 * Useful for lint-and-refactor wrappers.
 */
public interface ILint {

	/** Get the lint's RefactorTarget set */
	public TreeSet getTargetSet();
	
	/** Run this lint for a given syntax tree node handle.
	 * @return Any error message, empty or null string otherwise.
	 */
	public String run(int topNode);

}
