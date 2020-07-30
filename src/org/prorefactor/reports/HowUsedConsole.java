/** 18-Dec-06 by John Green
 * Copyright (C) 2006 Joanju Software. All rights reserved.
 */
package org.prorefactor.reports;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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


/** Provides console UI for the How Used report.
 * Project settings must be loaded prior to launching this.
 */
public class HowUsedConsole extends Console {

	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	HashSet<String> fieldNames = new HashSet<String>();
	BufferedWriter reportOut;
	HowUsed howUsed;
	String dbTableName;
	String fieldName;
	
	public void go() throws Exception {

		Collection files = null;

		out.println();
		out.println("Enter the name of a file to read a list of");
		out.println("compile units to report on. The file should contain");
		out.println("one fully qualified compile unit file name per line.");
		out.println("*OR* enter blank to parse all .p, .w, and");
		out.println(".cls files in a directory tree.");
		out.print("File name (or blank): ");
		String listFileName = reader.readLine();
		File dir = null;
		if (listFileName.trim().length() > 0) {
			files = buildCUListFromFile(listFileName);
		} else {
			out.println("Enter the name of the directory to scan.");
			dir = super.promptDirectory();
			if (dir==null) return;
		}
		
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
		/* SCL-3087 : Replaced FileWriter with OutputStreamWriter to use the current codepage */
		reportOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFileName)), Charset.forName(System.getProperty("file.encoding"))));
		
		LogWriter log = new LogWriter(out);
		
		howUsed = new HowUsed(reportOut, log, reportField);
		
		try {
			if (dir!=null) {
				files = findCompileUnits(dir);
			}
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
			if (wasCurrent) {
				// re-load, but the whole thing
				pub.load();
				pu.treeParser01();
			}
			howUsed.report(pu);
			reportOut.newLine();
			reportOut.newLine();
			reportOut.flush();
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
