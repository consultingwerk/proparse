/**
 * Field.java
 * @author John Green
 * 20-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002-2007 Joanju Software (joanju.com).
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.core.schema;

import java.util.Comparator;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ClassSupport;
import org.prorefactor.treeparser.DataType;
import org.prorefactor.treeparser.Primative;
import com.joanju.Xferable;
import com.joanju.DataXferStream;


/**
 * Field objects are created both by the Schema class and
 * they are also created for temp and work table fields
 * defined within a 4gl compile unit.
 */
public class Field implements Primative, Xferable {

	/** Only to be used for persistence/serialization. */
	public Field() {}

	/** Standard constructor.
	 * @param table Use null if you want to assign the field to a table as a separate step.
	 * @see #setTable(Table)
	 */
	public Field(String inName, Table table) {
		this.name = inName;
		this.table = table;
		if (table!=null) table.add(this);
	}
	/** Constructor for temporary "lookup" fields. "Package" visibility. */
	Field(String inName) {
		this.name = inName;
		this.table = Schema.nullTable;
	}

	private int extent;
	String name; // "Package" access for fast access in NAME_ORDER
	private DataType dataType;
	private String className = null;
	private Table table;
	
	/** This is a convenience class for working with a string field name, where
	 * there may or may not be a database or table qualifier in the name.
	 */
	public static class Name {
		public Name(String dbPart, String tablePart, String fieldPart) {
			db = dbPart;
			table = tablePart;
			field = fieldPart;
		}
		public Name(String name) {
			String [] parts = name.split("\\.");
			if (parts.length==1) {
				field = parts[0];
			} else if (parts.length==2) {
				table = parts[0];
				field = parts[1];
			} else {
				db = parts[0];
				table = parts[1];
				field = parts[2];
			}
		}
		public String db;
		public String table;
		public String field;
		public String generateName() {
			StringBuilder buff = new StringBuilder();
			if (table!=null && table.length()>0) {
				if (db!=null && db.length()>0) {
					buff.append(db);
					buff.append(".");
				}
				buff.append(table);
				buff.append(".");
			}
			buff.append(field);
			return buff.toString();
		}
	}


	
	
	public void assignAttributesLike(Primative likePrim) {
		dataType = likePrim.getDataType();
		className = likePrim.getClassName();
		extent = likePrim.getExtent();
	}


	/** Copy the bare minimum attributes to a new Field object.
	 * @param toTable The table that the field is being added to.
	 * @return The newly created Field, though you may not need
	 * it for anything since it has already been added to the Table.
	 */
	public Field copyBare(Table toTable) {
		Field f = new Field(this.name, toTable);
		f.dataType = this.dataType;
		f.extent = this.extent;
		f.className = this.className;
		return f;
	}
	

	/** Comparator for sorting by name. */
	static final Comparator<Field> NAME_ORDER = new Comparator<Field>() {
		public int compare(Field f1, Field f2) {
			return f1.name.compareToIgnoreCase(f2.name);
		}
	};

	
	/** @see Primative#getClassName() */
	public String getClassName() { return className; }
	public DataType getDataType() { return dataType; }
	public int getExtent() { return extent; }
	public String getName() { return name; }
	public Table getTable() { return table; }
	public Primative setClassName(String s) { this.className = s; return this; }
	public Primative setClassName(JPNode typeNameNode) {
		this.className = ClassSupport.qualifiedClassName(typeNameNode);
		return this;
	}
	public Primative setDataType(DataType dataType) { this.dataType = dataType; return this; }
	public Primative setExtent(int extent) {this.extent = extent; return this; }
	
	
	/** Use this to set the field to a table if you used null for the table in the constructor. */
	public void setTable(Table table) {
		this.table = table;
		table.add(this);
	}


	/** Implement Xferable. */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeRef(className);
		out.writeRef(dataType);
		out.writeInt(extent);
		out.writeRef(name);
		out.writeRef(table);
	}
	/** Implement Xferable. */
	public void writeXferSchema(DataXferStream out) throws IOException {
		out.schemaRef("className");
		out.schemaRef("dataType");
		out.schemaInt("extent");
		out.schemaRef("name");
		out.schemaRef("table");
	}


}
