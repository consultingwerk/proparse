/* ProgressProjectSettings.java
 * Created on Sep 16, 2003
 * John Green
 *
 * Copyright (C) 2003 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor.settings;

import org.prorefactor.refactor.RefactorSession;


/** Settings specific to an individual project, which have to
 * do with Proparse or refactoring configuration or preferences.
 */
public class ProparseProjectSettings extends Settings {

	public ProparseProjectSettings(String propsFilename, String projectName) {
		super(propsFilename);
		propertiesDescription = "Proparse/Refactor Project Settings";
		schemaFile = RefactorSession.getProjectsDirName() + projectName + "/proparse.schema";
	}

	public boolean capKeyword = true;
	public boolean indentTab = false;
	public int indentSpaces = 3;
	public String keywordall = "";
	public String rCodeDir = "";
	public String schemaFile;

	public void loadSettings() throws Exception {
		super.loadSettings();
		String tmp;

		tmp = properties.getProperty("capitalize_keywords");
		capKeyword = (tmp==null || tmp.equals("true"));

		tmp = properties.getProperty("indent_tab");
		if (tmp!=null) indentTab = (tmp.equals("true"));

		tmp = properties.getProperty("indent_spaces");
		if (tmp!=null) indentSpaces = Integer.parseInt(tmp);

		keywordall = getVal(keywordall, "keywordall");
		rCodeDir = getVal(rCodeDir, "r_code_dir");
		schemaFile = getVal(schemaFile, "schema_file");
	}

	public void saveSettings() throws Exception {
		properties.put("capitalize_keywords", capKeyword ? "true" : "false");
		properties.put("indent_tab", indentTab ? "true" : "false");
		properties.put("indent_spaces", Integer.toString(indentSpaces));
		properties.put("keywordall", keywordall);
		properties.put("r_code_dir", rCodeDir);
		properties.put("schema_file", schemaFile);

		super.saveSettings();
	}

}
