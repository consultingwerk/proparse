/* ApplicationSettings.java
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


/** Application-wide settings */
public class ApplicationSettings extends Settings {

	/** This does not create the file if it does not exist. */
	public ApplicationSettings(String propsFilename) {
		super(propsFilename);
		propertiesDescription = "Application Settings";
	}

	public String externalEditor = "";

	/** This does not create the file if it does not exist. */
	@Override
	public void loadSettings() throws Exception {
		super.loadSettings();
		externalEditor = getVal(externalEditor, "External_editor");
	}

	@Override
	public void saveSettings() throws Exception {
		properties.put("External_editor", externalEditor);
		super.saveSettings();
	}

}
