/* Created on 31-Jan-2006
 * Authors: John Green
 *
 * Copyright (c) 2006-2008 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.schema.Field;
import com.joanju.DataXferStream;

/** Frame and Browse widgets are FieldContainers.
 * This class provides the services for looking up fields/variables in a Frame or Browse.
 */
public abstract class FieldContainer extends Widget {

	/** Only to be used for persistence/serialization. */
	protected FieldContainer() {}

	public FieldContainer(String name, SymbolScope scope) { super(name, scope); }

	private ArrayList<JPNode> statementList = new ArrayList<JPNode>();
	private HashSet<FieldBuffer> fieldSet = new HashSet<FieldBuffer>();
	private HashSet<Symbol> enabledFields = new HashSet<Symbol>();
	private HashSet<Symbol> otherSymbols = new HashSet<Symbol>();
	private HashSet<Variable> variableSet = new HashSet<Variable>();


	

	/** Add a statement node to the list of statements which operate on this FieldContainer.
	 * Intended to be used by the tree parser only.
	 */
	public void addStatement(JPNode node) {
		statementList.add(node);
	}
	
	
	/** Add a FieldBuffer or Variable to this Frame or Browse object.
	 * Intended to be used by the tree parser only.
	 * The tree parser passes 'true' for ENABLE|UPDATE|PROMPT-FOR.
	 */
	public void addSymbol(Symbol symbol, boolean statementIsEnabler) {
		if (symbol instanceof FieldBuffer)
			fieldSet.add((FieldBuffer)symbol);
		else if (symbol instanceof Variable)
			variableSet.add((Variable)symbol);
		else
			otherSymbols.add(symbol);
		if (statementIsEnabler)
			enabledFields.add(symbol);
	}
	
	
	/** Get the fields and variables in the frame.
	 * The entries in the return list are of type Variable and/or FieldBuffer. 
	 */
	public ArrayList<Symbol> getAllFields() {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		ret.addAll(variableSet);
		ret.addAll(fieldSet);
		return ret;
	}


	/** Combines getAllFields() with all other widgets in the FieldContainer. */
	public ArrayList<Symbol> getAllFieldsAndWidgets() {
		ArrayList<Symbol> ret = getAllFields();
		ret.addAll(otherSymbols);
		return ret;
	}
	
	
	/** Get the enabled fields and variables in the frame.
	 * The entries in the return list are of type Variable and/or FieldBuffer. 
	 */
	public ArrayList<Symbol> getEnabledFields() {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		ret.addAll(enabledFields);
		return ret;
	}
	
	
	/** Get the list of nodes for the statements which operate on this FieldContainer. */
	public ArrayList<JPNode> getStatementList() {return statementList;}

	
	/** Check to see if a name matches a Variable or a FieldBuffer in this FieldContainer.
	 * Used by the tree parser at the INPUT function for resolving the name reference.
	 */
	public Symbol lookupFieldOrVar(Field.Name name) {
		if (name.table==null) for (Variable var : variableSet) {
			if (var.getName().equalsIgnoreCase(name.field)) return var;
		}
		for (FieldBuffer fieldBuffer : fieldSet) {
			if (fieldBuffer.canMatch(name)) return fieldBuffer;
		}
		return null;
	}


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(enabledFields);
		out.writeRef(fieldSet);
		out.writeRef(statementList);
		out.writeRef(variableSet);
		out.writeRef(otherSymbols);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);	//To change body of overridden methods use File | Settings | File Templates.
		out.schemaRef("enabledFields");
		out.schemaRef("fieldSet");
		out.schemaRef("statementList");
		out.schemaRef("variableSet");
		out.schemaRef("otherSymbols");
	}


}
