/** 7-May-07 by John Green
 * Copyright (C) 2007 Joanju Software.
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.prorefactor.refactor.RefactorSession;


/** Interface to a ProRefactor session database - not currently used.
 */
public class ProRefactorDB {
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		String dbName = 
			RefactorSession.getProrefactorDirName().replace('\\', '/') 
			+ "db/prorefactor";
        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection(
        		"jdbc:hsqldb:file:" + dbName
        		, "sa"
        		, ""
        		);
        return conn;
	}

}
