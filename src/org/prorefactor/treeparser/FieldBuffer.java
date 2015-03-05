/**
 * Field.java
 * @author John Green
 * 20-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002-2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.TokenTypes;
import org.prorefactor.core.schema.Field;
import org.prorefactor.core.schema.Schema;
import com.joanju.DataXferStream;

import java.io.IOException;


/**
 * FieldBuffer is the Symbol object linked to from the AST
 * for schema, temp, and work table fields, and FieldBuffer
 * provides the link to the Field object.
 */
public class FieldBuffer extends Symbol implements Primative {

	/** Only to be used for persistence/serialization. */
	public FieldBuffer() {}

	/** When you create a FieldBuffer object, you do not set the name,
	 * because that comes from the Field object.
	 */
	public FieldBuffer(SymbolScope scope, TableBuffer buffer, Field field) {
		super(scope);
		this.buffer = buffer;
		this.field = field;
		buffer.addFieldBuffer(this);
	}

	Field field;
	TableBuffer buffer;

	
	
	
	public void assignAttributesLike(Primative likePrim) {
		field.assignAttributesLike(likePrim);
	}
	
	
	/** Could this FieldBuffer be referenced by the input name?
	 * Input Field.Name must already be all lowercase.
	 * Deals with abbreviations, unqualified table/database, and db aliases.
	 */
	public boolean canMatch(Field.Name input) {
		// Assert that the input name is already lowercase.
		assert input.generateName().toLowerCase().equals(input.generateName());
		Field.Name self = new Field.Name(this.fullName().toLowerCase());
		if (input.db!=null) {
			Schema schema = Schema.getInstance();
			if (this.buffer.getTable().getDatabase() != schema.lookupDatabase(input.db)) return false;
		}
		if (input.table!=null) {
			if (buffer.isDefaultSchema()) {
				if (! self.table.startsWith(input.table)) return false;
			} else {
				// Temp/work/buffer names can't be abbreviated.
				if (! self.table.equals(input.table)) return false;
			}
		}
		if (! self.field.startsWith(input.field)) return false;
		return true;
	}
	

	/** @deprecated
	 * INVALID. Do not use. There is never any reason to copy a FieldBuffer,
	 * since they are created by the tree parser on the fly. They are not
	 * defined formally in the syntax.
	 */
	@Override
	public Symbol copyBare(SymbolScope scope) {
		assert false;
		return null;
	}


	/** Get "database.buffer.field" for schema fields, or
	 * "buffer.field" for temp/work table fields.
	 */
	@Override
	public String fullName() {
		StringBuilder buff = new StringBuilder(buffer.fullName());
		buff.append(".");
		buff.append(field.getName());
		return buff.toString();
	}

	

	public TableBuffer getBuffer() { return buffer; }
	
	/** Gets the underlying Field's className (or null if not a class).
	 * @see Primative#getClassName()
	 */
	public String getClassName() { return field.getClassName(); }
	
	/** Gets the underlying Field's dataType. */
	public DataType getDataType() { return field.getDataType(); }

	/** The extent comes from the underlying Field. */
	public int getExtent() { return field.getExtent(); }
	
	public Field getField() { return field; }
	
	
	/** Returns the Field name. There is no "field buffer name". */
	@Override
	public String getName() { return field.getName(); }
	
	
	/** Always returns FIELD.
	 * @see org.prorefactor.treeparser.Symbol#getProgressType().
	 * To see if this field buffer is for a schema table, temp-table, or work-table,
	 * see Table.getStoreType().
	 * @see org.prorefactor.core.schema.Table#getStoretype().
	 */
	@Override
	public int getProgressType() { return TokenTypes.FIELD; }

	
	
	/** @see org.prorefactor.treeparser.Symbol#isExported() */
	@Override
	public boolean isExported() { return buffer.isExported(); }

	
	
	/** @see org.prorefactor.treeparser.Symbol#isImported() */
	@Override
	public boolean isImported() { return buffer.isImported(); }

	
	/** Sets the underlying Field's className. */
	public Primative setClassName(String className) { field.setClassName(className); return this; }
	/** Sets the underlying Field's className. */
	public Primative setClassName(JPNode typeNameNode) {
		field.setClassName(typeNameNode);
		return this;
	}

	
	/** Sets the underlying Field's dataType. */
	public Primative setDataType(DataType dataType) { field.setDataType(dataType); return this; }
	
	
	/** Sets the extent of the underlying Field. */
	public Primative setExtent(int extent) { field.setExtent(extent); return this; }
	
	
	/** Invalid - do not call. Name comes from the Field. */
	@Override
	public void setName(String name) { assert false; }


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(buffer);
		out.writeRef(field);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
		out.schemaRef("buffer");
		out.schemaRef("field");
	}


}
