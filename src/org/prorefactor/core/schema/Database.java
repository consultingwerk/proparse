/**
 * Database.java
 * @author John Green
 * 20-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002, 2004, 2006 Joanju Software (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.core.schema;

import com.joanju.Xferable;
import com.joanju.DataXferStream;

import java.util.Comparator;
import java.util.TreeSet;
import java.io.IOException;

/**
 * Database objects are created by the Schema class,
 * and they are used when looking up table names from 4gl comile units.
 * "id" field is a database number, starting at one.
 * Might be the logical database number - depends on how you use this.
 */
public class Database implements Xferable {

	public Database() {}

	public Database(String name) {
		this.setName(name);
	}

	private String name;
	private TreeSet<Table> tableSet = new TreeSet<Table>(Table.NAME_ORDER);
	
	/** Comparator for sorting by name. */
	public static final Comparator<Database> NAME_ORDER = new Comparator<Database>() {
		public int compare(Database d1, Database d2) {
			return d1.getName().compareToIgnoreCase(d2.getName());
		}
	};

	public void add(Table table) {
		tableSet.add(table);
	}

	public String getName() { return name; }

	public TreeSet<Table> getTableSet() { return tableSet; }

	public void setName(String name) { this.name = name; }


	/** Implement Xferable. */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeRef(name);
	}
	/** Implement Xferable. */
	public void writeXferSchema(DataXferStream out) throws IOException {
		out.schemaRef("name");
	}


}
