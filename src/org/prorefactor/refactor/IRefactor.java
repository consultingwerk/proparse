/* IRefactor.java
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

/**
 * Interface to a Refactor object.
 * Useful for general purpose lint-and-refactor wrappers.
 */
public interface IRefactor {
	/** Run the refactor for a given Target and a given scan.
	 * @return Must return 1 for a successful change, negative number for
	 * an error. Zero and integers >1 currently have no special meaning.
	 */ 
	public int run(RefactorTarget target, int scanNum);
}
