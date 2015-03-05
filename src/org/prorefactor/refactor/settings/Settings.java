/* Settings.java
 * Created on Sep 18, 2003
 * John Green
 *
 * Copyright (C) 2003 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor.settings;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/** Abstract base class for other Settings classes.
 * This implements the mechanics for loading and saving settings.
 * The sub-classes are responsible for handling specific
 * settings attribute/value pairs.
 */
public abstract class Settings {

	/** This does not create the file if it does not exist. */
	public Settings(String propsFilename) {
		this.propsFilename = propsFilename;
	}

	protected Properties properties = new Properties();
	protected String propertiesDescription = "";
	protected String propsFilename;

	/** Returns property value, or original if property value is null */
	protected String getVal(String orig, String propertyName) {
		String tmp = properties.getProperty(propertyName);
		if (tmp!=null) return tmp;
		return orig;
	}

	/** This does not create the file if it does not exist. */
	public void loadSettings() throws Exception {
		FileInputStream in = null;
		try {
			in = new FileInputStream(propsFilename);
			properties.load(in);
		} finally {
			if (in!=null) in.close();
		}
	}

	public void saveSettings() throws Exception {
		FileOutputStream out = null;
		try {
			File theFile = new File(propsFilename);
			File theDir = theFile.getParentFile();
			theDir.mkdirs();
			out = new FileOutputStream(theFile);
			properties.store(out, propertiesDescription);
		} finally {
			if (out!=null) out.close();
		}
	}

}
