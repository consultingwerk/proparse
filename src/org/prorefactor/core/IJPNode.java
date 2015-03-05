/* IJPNode.java
 * Created on Jan 12, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core;

/** This interface exists so that we can refer to it, rather than to JPNode directly,
 * from the tree parsers. The tree parsers are expensive to recompile, and JPNode tends
 * to undergo plenty of changes.
 */
public interface IJPNode {

	/** Attribute "state2" is necessary for the tree parsers, so it is one
	 * attribute which must always be available.
	 */
	public int getState2();

}
