/**
 * ParseAction.java
 * @author Peter Dalbadie
 * 21-Sep-2004
 * 
 * Copyright (c) 2004,2006 ProRefactor.org.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


package org.prorefactor.core.unittest;

import org.prorefactor.treeparser.Routine;
import org.prorefactor.treeparser.SymbolScope;
import org.prorefactor.treeparser01.TP01Support;


/**
 * Test utility class. Use to get Routine objects by name
 * from a previously built symbol table - the root scope
 * in a TP01Support action class.
 */
public class RoutineHandler {

	private String name;
	private Routine routine;
		
	public RoutineHandler(String name, TP01Support symbolAction){
		this.name = name;
		this.routine = symbolAction.getRootScope().lookupRoutine(name);
	}

	public String getName(){
		return name;
	}
	
	public SymbolScope getRoutineScope(){
		return routine.getRoutineScope();
	}
}
