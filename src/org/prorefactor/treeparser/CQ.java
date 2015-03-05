/* CQ.java
 * Created on May 10, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparser;

/** Context qualifiers.
 * This class may not be instantiated.
 */
public class CQ {
	
	private CQ() {}

	/** Is the symbol reference also an initializer?
	 * i.e. An input parameter. Also used in FIND statement for record buffer. */
	public static final int INIT = 1;

	/** Referencing the symbol's <b>value</b>. */
	public static final int REF = 2;

	/** Reference and update the symbol's value.
	 * Usually this is in an UPDATE statement, which displays and updates. */
	public static final int REFUP = 3;

	/** Updating the symbol's value. */
	public static final int UPDATING = 5;

	/** We are strictly referencing the symbol - not its value.
	 * Used both for field and table symbols. For table symbols,
	 * the lookup is done by schema symbols first, buffer symbols second. */
	public static final int SYMBOL = 4;

	/** Referencing a buffer symbol.
	 * The lookup is done by buffer symbols first, schema symbols second. */
	public static final int BUFFERSYMBOL = 12;

	/** A temp or work table symbol. */
	public static final int TEMPTABLESYMBOL = 13;

	/** A schema table symbol. */
	public static final int SCHEMATABLESYMBOL = 14;

	/** INIT, but for a "weak" scoped buffer */
	public static final int INITWEAK = 15;



	/** Is symbol's value "read" in this context? */
	public static boolean isRead(int cq) {
		switch (cq) {
		case CQ.INIT :
		case CQ.INITWEAK :
		case CQ.REF :
		case CQ.REFUP :
			return true;
		}
		return false;
	}

	

	/** Is the symbol's value "written" in this context? */
	public static boolean isWrite(int cq) {
		switch (cq) {
		case CQ.REFUP :
		case CQ.UPDATING :
			return true;
		}
		return false;
	}

}