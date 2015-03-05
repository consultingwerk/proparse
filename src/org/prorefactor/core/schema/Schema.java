/**
 * Schema.java
 * @author John Green
 * 17-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002-2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.core.schema;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.prorefactor.treeparser.DataType;



/**
 * Schema is a singleton with methods and fields for
 * working with database schema names, and references
 * to those from 4gl compile units.
 */
public class Schema {

	private Schema() { initRefresh(); }

	
	public static Database nullDatabase = new Database("");
	private static Schema theInstance;
	public static Table nullTable = new Table("");

	// See initRefresh() for member initializations.
	private HashMap<String, String> aliases;
	private TreeSet<Database> dbSet;
	private TreeSet<Table> allTables;



	/** Get databases sorted by name. */
	public TreeSet<Database> getDbSet() {return dbSet;}

	
	/**
	 * Schema is a "Singleton"
	 */
	public static Schema getInstance() {
		if (theInstance == null)
			theInstance = new Schema();
		return theInstance;
	}
	public void clear() { initRefresh(); }
	private void initRefresh() {
		aliases = new HashMap<String, String>();
		dbSet = new TreeSet<Database>(Database.NAME_ORDER);
		allTables = new TreeSet<Table>(ALLTABLES_ORDER);
	}



	static final Comparator<Table> ALLTABLES_ORDER = new Comparator<Table>() {
		public int compare(Table s1, Table s2) {
			int ret = s1.getName().compareToIgnoreCase(s2.getName());
			if (ret != 0)
				return ret;
			return s1.getDatabase().getName().compareToIgnoreCase(s2.getDatabase().getName());
		}
	};



	static final Comparator IGNORECASE_ORDER = new Comparator() {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.compareToIgnoreCase(s2);
		}
	};



	/**
	 * Add a database alias.
	 * @param aliasname The name for the alias
	 * @param dbname The database's logical name
	 * @return Empty string.
	 */
	public String aliasCreate(String aliasname, String dbname) {
		aliases.put(aliasname.toLowerCase(), dbname);
		return "";
	}



	/**
	 * Delete a database alias.
	 * @param aliasname The name for the alias, null or empty string to delete all.
	 */
	public void aliasDelete(String aliasname) {
		if (aliasname == null || aliasname.length() == 0) {
			aliases.clear();
		} else {
			aliases.remove(aliasname.toLowerCase());
		}
	}



	/** Get an iterator through all tables, sorted by db.table name. */
	public Iterator getAllTablesIterator() {
		return allTables.iterator();
	}



	/**
	 * Load schema names and RECID from a flat file.
	 * @param from The filename to read from.
	 */
	public void loadSchema(String from) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(from));
		StreamTokenizer tokenstream  = new StreamTokenizer(reader);
		tokenstream.eolIsSignificant(false);
		tokenstream.wordChars('!', 'z');
		Database currDatabase = null;
		Table currTable = null;
		try {
			while (tokenstream.nextToken() != StreamTokenizer.TT_EOF) {
				String theString = tokenstream.sval;
				if (theString.equals("::")) {
					// database name
					tokenstream.nextToken();
					String dbname = tokenstream.sval;
					tokenstream.nextToken(); // database number is no longer stored
					currDatabase = new Database(dbname);
					dbSet.add(currDatabase);
				} else if (theString.equals(":")) {
					// table name
					tokenstream.nextToken();
					String tablename = tokenstream.sval;
					tokenstream.nextToken(); // table recid is no longer stored
					currTable = new Table(tablename, currDatabase);
					allTables.add(currTable);
				} else {
					// field name
					String fieldname = tokenstream.sval;
					tokenstream.nextToken(); // field recid is no longer stored
					tokenstream.nextToken();
					String typeName = tokenstream.sval;
					Field field = new Field(fieldname, currTable);
					field.setDataType(DataType.getDataType(typeName));
					if (field.getDataType()==null)
						throw new IOException("Unknown datatype: " + typeName);
					tokenstream.nextToken();
					field.setExtent((int) tokenstream.nval);
					// Fields are not needed or used in Proparse.dll.
				}
			} // while
		} catch (Throwable e) {
			IOException e2 = new IOException(
					"Schema load failed. Invalid schema file? " + from + "\n"
					+ e.toString()
					);
			e2.initCause(e);
			throw e2;
		}
		reader.close();
	} // loadSchema()



	/** Lookup Database, with alias checks. */
	public Database lookupDatabase(String inName) {
		Database db = lookupDatabase2(inName);
		if (db != null)
			return db;
		// Check for database alias
		String realName = aliases.get(inName.toLowerCase());
		if (realName == null)
			return null;
		return lookupDatabase2(realName);
	}



	/**
	 * Lookup Database by name.
	 * Called twice by lookupDatabase().
	 */
	private Database lookupDatabase2(String inName) {
		SortedSet dbTailSet = dbSet.tailSet(new Database(inName));
		if (dbTailSet.size() == 0)
			return null;
		Database db = (Database)(dbTailSet.first());
		if ( db == null || db.getName().compareToIgnoreCase(inName) != 0 )
			return null;
		return db;
	}
	
	
	
	/** Lookup a Field, given the db, table, and field names */
	public Field lookupField(String dbName, String tableName, String fieldName) {
		Table table = lookupTable(dbName, tableName);
		if (table==null) return null;
		return table.lookupField(fieldName);
	}



	/**
	 * Lookup a table by name.
	 * @param inName The string table name to lookup.
	 * @return A Table, or null if not found.
	 * If a name like "db.table" fails on the first lookup try,
	 * we next search dictdb for the table, in case it's something
	 * like "sports._file". In that case, the Table from the "dictdb"
	 * database would be returned. We don't keep meta-schema records
	 * in the rest of the databases.
	 */
	public Table lookupTable(String inName) {
		if (inName.indexOf('.') > -1) {
			Table firstTry = lookupTable2(inName);
			if (firstTry != null) return firstTry;
			return lookupMetaTable(inName);
		}
		return lookupTableCheckName(allTables.tailSet(new Table(inName)), inName);
	}
	
	
	
	/** Lookup a table, given a database name and a table name. */
	public Table lookupTable(String dbName, String tableName) {
		Database db = lookupDatabase(dbName);
		if (db==null) return null;
		return lookupTableCheckName(db.getTableSet().tailSet(new Table(tableName)), tableName);
	}



	// It turns out that we *do* have to test for uniqueness - we can't just leave
	// that job to the compiler. That's because when looking up schema names for
	// a DEF..LIKE x, if x is non-unique in schema, then we move on to temp/work/buffer names.
	private Table lookupTableCheckName(SortedSet set, String name) {
		String lname = name.toLowerCase();
		Iterator it = set.iterator();
		if (! it.hasNext()) return null;
		Table table = (Table)(it.next());
		// test that we got a match
		if (! table.getName().toLowerCase().startsWith(lname)) return null;
		// test that we got a unique match
		if (lname.length() < table.getName().length()  &&  it.hasNext()) {
			Table next = (Table)(it.next());
			if (next.getName().toLowerCase().startsWith(lname)) return null;
		}
		return table;
	}


	
	/** Lookup a qualified table name */
	private Table lookupTable2(String inName) {
		String [] parts = inName.split("\\.");
		return lookupTable(parts[0], parts[1]);
	}



	/**
	 * This is for looking up names like "sports._file". We return the dictdb Table.
	 */
	private Table lookupMetaTable(String inName) {
		String [] parts = inName.split("\\.");
		Database db = lookupDatabase("dictdb");
		if (db == null) return null;
		return lookupTableCheckName(db.getTableSet().tailSet(new Table(parts[1])), parts[1]);
	}



	/** Lookup an unqualified schema field name.
	 * Does not test for uniqueness. That job is left to the compiler.
	 * (In fact, anywhere this is run, the compiler would check that the
	 * field name is also unique against temp/work tables.)
	 * Returns null if nothing found.
	 */
	public Field lookupUnqualifiedField(String name) {
		Field field;
		for (Object allTable : allTables) {
			Table table = (Table) allTable;
			field = table.lookupField(name);
			if (field != null) return field;
		}
		return null;
	}


}
