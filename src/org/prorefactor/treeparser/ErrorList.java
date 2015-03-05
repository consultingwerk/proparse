/**
 * ErrorList.java
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

import java.util.ArrayList;

/**
 * A list of SemanticError objects found during a
 * tree parse.
 *
 */
public class ErrorList {

	public ErrorList() { }

	private ArrayList<SemanticError> list = new ArrayList<SemanticError>();
	
	

	public boolean add(SemanticError o) { return list.add(o); }


	public void clear() { list.clear(); }


	public boolean equals(Object obj) { return list.equals(obj); }
	
	
	/** Get a copy of the error list. */
	public ArrayList<SemanticError> getCopy() { return new ArrayList<SemanticError>(list); }


	public boolean isEmpty() { return list.isEmpty(); }


	public int size() { return list.size(); }


}
