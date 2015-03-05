/** 6-Nov-2002
 * Authors: John Green
 * 
 * Copyright (c) 2002-2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.TokenTypes;

import com.joanju.DataXferStream;


/**
 * A Symbol defined with DEFINE VARIABLE or any of the other various
 * syntaxes which implicitly define a variable.
 */
public class Variable extends Symbol implements Primative, Value {

	/** Only to be used for persistence/serialization. */
	public Variable() {}

	public Variable(String name, SymbolScope scope) {
		super(scope);
		setName(name);
	}

	private int extent;
	private DataType dataType;
	private Object value;
	private String className = null;
	
	
	
	
	public void assignAttributesLike(Primative likePrim) {
		dataType = likePrim.getDataType();
		className = likePrim.getClassName();
		extent = likePrim.getExtent();
	}


	@Override
	public Symbol copyBare(SymbolScope scope) {
		Variable v = new Variable(getName(), scope);
		v.className = this.className;
		v.dataType = this.dataType;
		v.extent = this.extent;
		return v;
	}

	/** Return the name of the variable. For this subclass of
	 * Symbol, fullName() returns the same value as getName().
	 */
	@Override
	public String fullName() { return getName(); }

	/** @see Primative#getClassName() */
	public String getClassName() { return className; }
	
	public DataType getDataType() { return dataType; }
	
	public int getExtent() { return extent; }
	
	/** @see org.prorefactor.treeparser.Value#getValue() */
	public Object getValue() { return value; }
	
	/** Returns TokenTypes.VARIABLE. */
	@Override
	public int getProgressType() { return TokenTypes.VARIABLE; }

	public Primative setClassName(String s) { this.className = s; return this; }
	public Primative setClassName(JPNode typeNameNode) {
		this.className = ClassSupport.qualifiedClassName(typeNameNode);
		return this;
	}

	public Primative setDataType(DataType dataType) { this.dataType = dataType; return this; }
	
	public Primative setExtent(int extent) { this.extent = extent; return this; }

	/** @see org.prorefactor.treeparser.Value#setValue(java.lang.Object) */
	public void setValue(Object value) { this.value = value; }


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(className);
		out.writeRef(dataType);
		out.writeInt(extent);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);	//To change body of overridden methods use File | Settings | File Templates.
		out.schemaRef("className");
		out.schemaRef("dataType");
		out.schemaInt("extent");
	}


}
