/**
 * TableBuffer.java
 * @author John Green
 * 6-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002, 2004 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import java.util.Collection;
import java.util.HashMap;
import java.io.IOException;

import org.prorefactor.core.IConstants;
import org.prorefactor.core.TokenTypes;
import org.prorefactor.core.schema.Field;
import org.prorefactor.core.schema.Schema;
import org.prorefactor.core.schema.Table;
import com.joanju.DataXferStream;


/** A TableBuffer is a Symbol which provides a link from the syntax tree to a Table object.
 */
public class TableBuffer extends Symbol {

	/** Only to be used for persistence/serialization. */
	public TableBuffer() {}

	/** Constructor for a named buffer.
	 * @param name Input "" for an unnamed or default buffer
	 */
	public TableBuffer(String name, SymbolScope scope, Table table) {
		super(scope);
		this.setName(name);
		this.table = table;
		if (name.length()==0) {
			isDefault = true;
			// The default buffer for temp/work tables is not really "unnamed"
			if (table.getStoretype()!=IConstants.ST_DBTABLE) this.setName(table.getName());
		}
	}

	private boolean isDefault = false;
	private HashMap<Field, FieldBuffer> fieldBuffers = new HashMap<Field, FieldBuffer>();
	private Table table = Schema.nullTable;

	

	void addFieldBuffer(FieldBuffer fieldBuffer) {
		fieldBuffers.put(fieldBuffer.getField(), fieldBuffer);
	}
	
	
	
	/** For temp/work table, also adds Table definition to the scope if it doesn't already exist. */
	@Override
	public Symbol copyBare(SymbolScope scope) {
		Table t;
		if (this.table.getStoretype()==IConstants.ST_DBTABLE) {
			t = this.table;
		} else {
			// Make sure temp/work table definition exists in target root scope.
			SymbolScopeRoot rootScope = scope.getRootScope();
			t = rootScope.lookupTableDefinition(table.getName());
			if (t==null) t = table.copyBare(rootScope);
		}
		String useName = this.isDefault ? "" : super.getName();
		return new TableBuffer(useName, scope, t);
	}



	/** Get the "database.buffer" name for schema buffers,
	 * get "buffer" for temp/work table buffers.
	 */
	public String fullName() {
		if (table.getStoretype()!=IConstants.ST_DBTABLE) return getName();
		StringBuilder buff = new StringBuilder();
		buff.append(table.getDatabase().getName());
		buff.append(".");
		buff.append(getName());
		return buff.toString();
	}
	
	
	/** Get a list of FieldBuffer symbols that have been created for this TableBuffer. */
	public Collection<FieldBuffer> getFieldBufferList() {
		return fieldBuffers.values();
	}

	
	
	/** Always returns BUFFER, whether this is a named buffer or a default buffer.
	 * @see org.prorefactor.treeparser.Symbol#getProgressType().
	 * To see if this buffer Symbol is for a schema table, temp-table, or work-table,
	 * see Table.getStoreType().
	 * @see org.prorefactor.core.schema.Table#getStoretype().
	 */
	public int getProgressType() { return TokenTypes.BUFFER; }


	
	/** Get or create a FieldBuffer for a Field. */
	public FieldBuffer getFieldBuffer(Field field) {
		assert field.getTable() == this.table;
		FieldBuffer ret = fieldBuffers.get(field);
		if (ret!=null) return ret;
		ret = new FieldBuffer(this.getScope(), this, field);
		fieldBuffers.put(field, ret);
		return ret;
	}


	
	/** Get the name of the buffer (overrides Symbol.getName).
	 * Returns the name of the table for default (unnamed) buffers.
	 */
	public String getName() {
		if (super.getName().length()==0) return table.getName();
		return super.getName();
	}

	
	public Table getTable() { return table; }
	
	
	/** Is this the default (unnamed) buffer? */
	public boolean isDefault() { return isDefault; }


	/** Is this a default (unnamed) buffer for a schema table? */
	public boolean isDefaultSchema() { 
		return isDefault && table.getStoretype()==IConstants.ST_DBTABLE; 
	}


	public void setTable(Table table) { this.table = table; }


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(fieldBuffers);
		out.writeBool(isDefault);
		out.writeRef(table);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
		out.schemaRef("fieldBuffers");
		out.schemaBool("isDefault");
		out.schemaRef("table");
	}


}
