/** 29-Dec-06 by John Green
 * Copyright (C) 2006 Joanju Software. All rights reserved.
 */
package org.prorefactor.reports;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.prorefactor.Console;
import org.prorefactor.core.schema.Field;
import org.prorefactor.core.schema.Schema;
import org.prorefactor.io.LogWriter;
import org.prorefactor.refactor.PUB;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;


/** Provides console UI for the Where Used report.
 * Project settings must be loaded prior to launching this.
 */
public class WhereUsedConsole extends Console {

	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	HashSet<String> fieldNames = new HashSet<String>();
	BufferedWriter reportOut;
	String dbTableName;
	String fieldName;
	
	public void go() throws Exception {
	
		out.println("Enter the name of the directory to scan.");
		File dir = super.promptDirectory();
		if (dir==null) return;
		
		out.println();
		out.println("Enter the fully qualified ldbname.tablename.fieldname.");
		out.print("Field name: ");
		String fullFieldName = reader.readLine().toLowerCase();
		if (fullFieldName==null || fullFieldName.length()==0) return;
		String [] parts = fullFieldName.split("\\.");
		if (parts.length != 3) {
			out.println("Name must be fully qualified ldbname.tablename.fieldname.");
			return;
		}
		fieldName = parts[2];
		dbTableName = parts[0] + "." + parts[1];
		Field reportField = Schema.getInstance().lookupField(parts[0], parts[1], parts[2]);
		if (reportField==null) {
			out.println("That field is not in the project's schema.");
			return;
		}
		
		out.println();
		out.println("Enter the name of the file to write the report to.");
		out.print("Output file: ");
		String outFileName = reader.readLine();
		if (outFileName==null || outFileName.length()==0) return;
		reportOut = new BufferedWriter(new FileWriter(new File(outFileName)));
		
		LogWriter log = new LogWriter(out);
		
		try {
			Collection files = findCompileUnits(dir);
			int currFileNum = 0;
			String totalFiles = "/" + files.size() + " ";
			for (Iterator it = files.iterator(); it.hasNext(); ) {
				File cuFile = (File) it.next();
				currFileNum++;
				log.log(Integer.toString(currFileNum) + totalFiles + cuFile.toString());
				processOne(new ParseUnit(cuFile), log);
			}
		} finally {
			reportOut.close();
			log.close();
		}
		
	}
	
	
	private void processOne(ParseUnit pu, LogWriter log) throws Exception {
		try {
			PUB pub = pu.getPUB();
			boolean wasCurrent = pub.loadTo(PUB.SCHEMA);
			if (wasCurrent==false) pub.build();
			if (usesField(pub) == false) return;
			reportOut.write(pu.getFile().toString());
			reportOut.newLine();
		} catch (RefactorException e) {
			log.logException(pu.getFile().toString(), e);
		}
	}
	
	
	private boolean usesField(PUB pub) {
		fieldNames.clear();
		pub.copySchemaFieldLowercaseNamesInto(fieldNames, dbTableName);
		return fieldNames.contains(fieldName);
	}


}
