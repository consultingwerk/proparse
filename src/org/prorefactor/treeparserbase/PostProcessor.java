/**
 * PostProcessor.java
 * @author John Green
 * Sep 27, 2004
 * www.joanju.com
 *
 * Copyright (C) 2004 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparserbase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/** Static class with main(), so it can be called from an Ant build.
 * Trims the data from the static initialization of _tokenNames,
 * because that generates too large of a static initializer (> 64k).
 */
public class PostProcessor {
	
	public static void main(String [] args) {
		System.out.println("PostProcessor is processing " + args[0]);
		try {
			trimTokenNames(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("PostProcessor is done.");
	}

	
	public static void trimTokenNames(String inName) throws IOException {
		File origFile = new File(inName);
		File tempFile = new File(inName + ".temp");
		BufferedReader reader = new BufferedReader(new FileReader(origFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		for (;;) {
			String line = reader.readLine();
			if (line==null) break;
			if (line.equals("\tpublic static final String[] _tokenNames = {")) {
				writer.write("\tpublic static final String[] _tokenNames = { };");
				while (! reader.readLine().equals("\t};")) { }
			} else {
				writer.write(line);
			}
			writer.newLine();
		}
		reader.close();
		writer.close();
		origFile.delete();
		if (! tempFile.renameTo(origFile))
			throw new IOException(
					"Failed to rename " 
					+ tempFile.getName()
					+ " to "
					+ origFile.getName()
					);
	}


}
