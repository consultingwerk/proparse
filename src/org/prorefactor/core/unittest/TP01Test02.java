/* TP01Test02.java
 * Created on May 26, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core.unittest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.prorefactor.core.Util;
import org.prorefactor.core.schema.Schema;
import org.apache.commons.io.FileUtils;


/**
 */
public class TP01Test02 extends UnitTestBase2 {

	public TP01Test02(String arg0) {
		super(arg0);
		outFile = new File(outName);
		snippetFile = new File(snippetName);
	}

	File outFile;
	File snippetFile;
	String expectName = "data/tp01tests/test02.expect.txt";
	String inName = "data/tp01tests/test02.in.txt";
	String outName = "data/tp01tests/test02.out.txt";
	String schemaName = "data/sports2000.schema";
	String snippetName = "data/tempsnippet.p";
	String snippetOutName = "data/tempout.p";
	String snippetSep = "--------------------------------" + System.getProperty("line.separator");



	public static void main(String[] args) {
		junit.textui.TestRunner.run(TP01Test01.class);
	}



	public void test01() throws Exception {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			Schema schema = Schema.getInstance();
			schema.clear();
			schema.loadSchema(schemaName);
			outFile.delete();
			/* SCL-3087 : Replaced FileReader with InputStreamReader to use the current codepage */
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inName), Charset.forName(System.getProperty("file.encoding"))));
			String line = null;
			snippet_loop:
			for (;;) {
				/* SCL-3087 : Replaced FileWriter with OutputStreamWriter to use the current codepage */
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(snippetFile), Charset.forName(System.getProperty("file.encoding"))));
				for (;;) {
					line = reader.readLine();
					if (line==null || line.startsWith("--")) break;
					writer.write(line);
					writer.newLine();
				}
				writer.close();
				AttributedWriter attWriter = new AttributedWriter();
				attWriter.write(snippetName, snippetOutName);
				Util.fileAppend(outName, snippetOutName);
				Util.fileAppendString(outName, snippetSep);
				if (line==null) break snippet_loop;
			} // snippet_loop
			snippetFile.delete();
		} finally {
			if (reader!=null) try { reader.close(); } catch (IOException e1) { }
		}
		assertTrue(FileUtils.contentEquals(new File(expectName), new File(outName)));
	}


}
