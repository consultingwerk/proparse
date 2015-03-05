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


/** Settings for an individual project.
 * These settings can all be derived from a PROGRESS
 * session with various commands like OPSYS and PROPATH.
 */
public class ProgressProjectSettings extends Settings {

	public ProgressProjectSettings(String propsFilename) {
		super(propsFilename);
		propertiesDescription = "Progress Project Settings";
	}

	public boolean batchmode = false;
	public String dbAliases = "";
	public String opsys = "WIN32";
	public String propath = "";
	public String proversion = "";
	public String windowSystem = "MS-WIN95";

	public void loadSettings() throws Exception {
		super.loadSettings();

		String tmp = properties.getProperty("batch_mode");
		batchmode = (tmp!=null && tmp.equals("true"));

		dbAliases = getVal(dbAliases, "database_aliases");
		opsys = getVal(opsys, "opsys");
		propath = getVal(propath, "propath");
		proversion = getVal(proversion, "proversion");
		windowSystem = getVal(windowSystem, "window_system");
	}

	public void saveSettings() throws Exception {
		properties.put("batch_mode", batchmode ? "true" : "false");
		properties.put("database_aliases", dbAliases);
		properties.put("opsys", opsys);
		properties.put("propath", propath);
		properties.put("proversion", proversion);
		properties.put("window_system", windowSystem);

		super.saveSettings();
	}

}
