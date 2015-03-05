/**
 * IConstants.java
 * @author John Green
 * 15-Oct-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.core;


/**
 * Constants commonly used when working with Proparse.
 * See Proparse documentation, "Node Attributes Reference".
 * Joanju uses 49000-49999 for scratch and otherwise non-persistent attributes.
 * Attributes 50000+ are reserved for non-Joanju use.
 */
public interface IConstants {

	//
	// Proparse.DLL Internals for attributes
	//

	/** See Proparse documentation, "Node Attributes Reference". */
	public int FALSE = 0;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int TRUE = 1;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int STORETYPE = 1100;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int ST_DBTABLE = 1102;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int ST_TTABLE = 1103;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int ST_WTABLE = 1104;

	/**
	 * For attribute key "storetype", this attribute value
	 * indicates that the reference is to a local variable within the
	 * 4gl compile unit.
	 * This node attribute is set by TreeParser01.
	 */
	public int ST_VAR = 1105;		// belongs to TreeParser01

	/** See Proparse documentation, "Node Attributes Reference". */
	public int OPERATOR  = 1200;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int STATE2 = 1300;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int STATEHEAD = 1400;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int PROPARSEDIRECTIVE = 1500;



	//
	// From version 1.2
	//


	/** See Proparse documentation, "Node Attributes Reference". */
	public int NODE_TYPE_KEYWORD = 1600;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int ABBREVIATED = 1700;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int FULLTEXT = 1800;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int FROM_USER_DICT = 1900;
	/** See Proparse documentation, "Node Attributes Reference". */
	public int INLINE_VAR_DEF = 2000;

	
	
	//
	// From version 1.3
	//
	
	/** See Proparse documentation, "Node Attributes Reference". */
	public int SOURCENUM = 2300;

	/** See Proparse documentation, "Node Attributes Reference". */
	public String QUALIFIED_CLASS_STRING = "qualified-class";
	public int QUALIFIED_CLASS_INT = 2400;

	
	//
	// From TreeParser01
	//


	/**
	 * Node attribute key, set to 1 ("true") if the node is
	 * an unqualified table field reference.
	 * For example, "customer.name" is qualified, but
	 * "name" is unqualified.
	 * This node attribute is set by TreeParser01.
	 */
	public int UNQUALIFIED_FIELD = 10150;
	/**
	 * Node attribute key, the value of which is a org.prorefactor.treeparser.CQ
	 * "Context Qualifier" value representing read, write, init, etc.
	 * Set by TreeParser01, and as of 2004.7.16, 
	 * this is only set for Field_ref and RECORD_NAME nodes.
	 * @see org.prorefactor.treeparser.CQ
	 */
	public int CONTEXT_QUALIFIER = 10160;



	//
	// From "org.prorefactor.refactor"
	//
	
	/** A scanner token which is scheduled to be cut from the token list */
	public int TO_BE_CUT = 11010;

	
	// Joanju uses 49000-49999 for scratch and otherwise non-persistent attributes.
	// Attributes 50000+ are reserved for non-Joanju use.


} // interface IConstants
