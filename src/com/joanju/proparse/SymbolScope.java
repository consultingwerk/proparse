/*
SymbolScope.java

Copyright (C) 2001-2010 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import org.prorefactor.core.schema.Schema;
import org.prorefactor.core.schema.Table;

import java.util.HashSet;
import java.util.HashMap;

public class SymbolScope {

	SymbolScope() {}
	SymbolScope(SymbolScope superScope) {
		this.superScope = superScope;
	}

	private String scopeName;
	private HashMap<String, TableRef> tableMap = new HashMap<String, TableRef>();
	private HashSet<String> functionSet = new HashSet<String>();
	private HashSet<String> methodSet = new HashSet<String>();
	private HashSet<String> varSet = new HashSet<String>();
	private Schema schema = Schema.getInstance();
	private SymbolScope superScope;


	// Field and table types
	enum FieldType {
		VARIABLE(1),
		DBTABLE(2),
		TTABLE(3),
		WTABLE(4);
		int intval;
		FieldType(int intval) {this.intval = intval;}
	}


	private class TableRef {
		FieldType tableType;
		String bufferFor;
		String fullName;
		String dbName;
	}





	void defBuffer(String bufferName, String tableName) {
		// Look for the tableName in tableMap /before/
		// adding the new ref. This is in case they have done:
		// DEFINE BUFFER customer FOR customer.  (groan)
		// ...otherwise we find ourself, with type not defined yet...
		tableName = tableName.toLowerCase();
		FieldType bufferType = isTableSchemaFirst(tableName);
		bufferName = bufferName.toLowerCase();
		TableRef newRef = new TableRef();
		newRef.bufferFor = tableName;
		newRef.tableType = bufferType;
		tableMap.put(bufferName, newRef);
		if (newRef.tableType == FieldType.DBTABLE) {
			Table table = schema.lookupTable(tableName);
			if (table!=null) {
				newRef.dbName = table.getDatabase().getName();
				newRef.fullName = newRef.dbName + "." + table.getName();
			}
			// Create a db.buffername entry.
			// If the db name was specified, then we have to use that
			// (whether it's a db alias or not) See bug #053.
			Table.Name tn = new Table.Name(tableName);
			String dbRefName =
					(tn.db!=null ? tn.db : table.getDatabase().getName())
					+ "." + bufferName
					;

			TableRef dbRef = new TableRef();
			dbRef.bufferFor = tableName;
			dbRef.tableType = bufferType;
			tableMap.put(dbRefName, dbRef);
		}
	}


	void defFunc(String name) {
		functionSet.add(name.toLowerCase());
	}


	void defMethod(String name) {
		methodSet.add(name.toLowerCase());
	}


	void defTable(String name, FieldType ttype) {
		TableRef newTable = new TableRef();
		newTable.tableType = ttype;
		tableMap.put(name.toLowerCase(), newTable);
	}


	void defVar(String name) {
		varSet.add(name.toLowerCase());
	}


	/** If this is an "inheritance scope", then getScopeName() returns the class name. */
	String getScopeName() { return scopeName; }

	SymbolScope getSuperScope() {return superScope;}


	/** Returns null if false, else, the table type */
	FieldType isTable(String inName) {
		// isTable is not recursive, but isTableDef is.
		// First: Qualified db.table.
		Table table = schema.lookupTable(inName);
		if (table!=null && inName.contains("."))
			return FieldType.DBTABLE;
		// Second: temp-table/work-table/buffer name.
		FieldType ret = isTableDef(inName);
		if (ret!=null)
			return ret;
		// Third: unqualified db table name.
		if (table!=null)
			return FieldType.DBTABLE;
		// Fourth: Check for built in buffer names.
		// Built in buffer for returned values from stored procedures.
		// My use of TTABLE as return type is arbitrary.
		if (inName.equals("proc-text-buffer"))
			return FieldType.TTABLE;
		// It's not a valid table name.
		return null;
	}


	FieldType isTableDef(String inName) {
		// Is the name a defined table? (ttable,wtable,buffername)
		// Progress does not allow tt/wt/buffer names to be abbreviated.
		// Progress does not allow tt/wt/buffer names to be ambigous.
		// Although tt and wt names cannot be scoped by context into a
		// procedure/function/trigger block, buffer names can.
		// All of these can be inherited from a super class.
		if (tableMap.containsKey(inName))
			return tableMap.get(inName).tableType;
		if (superScope!=null)
			return superScope.isTableDef(inName);
		return null;
	}


	FieldType isTableSchemaFirst(String inName) {
		// If we find that an non-abbreviated schema table name matches,
		// we return it even before a temp/work table match.
		Table table = schema.lookupTable(inName);
		if (table != null) {
			Table.Name name = new Table.Name(inName);
			if (table.getName().length() == name.table.length())
				return FieldType.DBTABLE;
		}
		return isTable(inName);
	}


	boolean isVar(String name) {
		// Variable names cannot be abbreviated.
		if (varSet.contains(name.toLowerCase()))
			return true;
		if (superScope!=null)
			return superScope.isVar(name);
		return false;
	}


	/** methodOrFunc should only be called for the "unit" scope, since it is
	 * the only one that would ever contain methods or user functions.
	 */
	int methodOrFunc(String name) {
		String lname = name.toLowerCase();
		// Methods take precedent over built-in functions. The compiler (10.2b) 
		// does not seem to try recognize by function/method signature.
		if (methodSet.contains(lname))
			return NodeTypes.LOCAL_METHOD_REF;
		if (functionSet.contains(lname))
			return NodeTypes.USER_FUNC;
		if (superScope!=null)
			return superScope.methodOrFunc(name);
		return 0;
	}


	/** Set to the class name if this is an "inheritance scope". */
	void setScopeName(String inName) { scopeName = inName; }


	/** returns <this> */
	SymbolScope setSuperScope(SymbolScope superScope) {
		this.superScope = superScope;
		return this;
	}


}
