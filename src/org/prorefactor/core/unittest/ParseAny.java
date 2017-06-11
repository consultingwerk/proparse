/** ParseAny.java
 * June 2010 by John Green
 * 
 * Copyright (c) 2010 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.core.unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.treeparser.ParseUnit;


/** Reads settings from ./parseany.properties.
 * Ex:<pre>
 * # Use only unix style forward slashes '/' in the properties file.
 * projectPropsDir = /work/myproject/proparsesettings
 * topParseDir = /work/myproject
 * extensions = p w cls
 * </pre>
 * <p>
 * An optional property is "parseListFile", which would
 * be the fully qualified name of a file which contains
 * a list of fully qualified names of files to parse.
 * If parseListFile is used, then topParseDir and extensions are ignored.
 * </p>
 */
public class ParseAny extends TestCase {
	
	File topDir;
	File parseListFile;
	String [] extensions;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loadProperties();
	}

	private void loadProperties() throws Exception {
		File propsfile = new File("parseany.properties");
		if (! propsfile.exists())
			throw new IOException("The file 'parseany.properties' must be in your working directory.");
		Properties props = new Properties();
		props.load(new FileInputStream(propsfile));
		
		String projectPropsDirName = props.getProperty("projectPropsDir");
		if (StringUtils.isEmpty(projectPropsDirName))
			throw new Exception("projectPropsDir must be defined");
		RefactorSession.getInstance().loadProjectPropertiesFromDirectory(projectPropsDirName);
		
		String listFileProp = props.getProperty("parseListFile");
		if (! StringUtils.isEmpty(listFileProp)) {
			parseListFile = new File(listFileProp);
			if (! (parseListFile.exists()))
				throw new Exception(listFileProp + " does not exist");
		} else {
			String extensionsProp = props.getProperty("extensions");
			if (StringUtils.isEmpty(extensionsProp))
				throw new Exception("extensions must be defined");
			extensions = StringUtils.split(extensionsProp);

			String topDirProp = props.getProperty("topParseDir");
			if (StringUtils.isEmpty(topDirProp))
				throw new Exception("topParseDir must be defined");
			topDir = new File(topDirProp);
			if (! (topDir.exists() && topDir.isDirectory()))
				throw new Exception(topDirProp + " is not a directory");
		}
	}
	
	private void parseOne(File file) throws Exception {
		System.out.println(file.getPath());
		ParseUnit pu = new ParseUnit(file);
		//pu.treeParser(new JPTreeParser());
		pu.treeParser01();
	}
	
	private void parseFromTopDir() throws Exception {
		Collection files = FileUtils.listFiles(topDir, extensions, true);
		for (Iterator it = files.iterator(); it.hasNext();) {
			File file = (File) it.next();
			parseOne(file);
		}
	}
	
	private void parseFromList() throws Exception {
		List lines = FileUtils.readLines(parseListFile);
		for (Object obj : lines) {
			String line = ((String)obj).trim();
			if (StringUtils.isEmpty(line))
				continue;
			parseOne(new File(line));
		}
	}

	public void test01() throws Exception {
		if (parseListFile != null)
			parseFromList();
		else
			parseFromTopDir();
		System.out.println("ParseAny completed OK");
	}

}
