/* RefactorSession.java
 * Created on Sep 30, 2003
 * John Green
 *
 * Copyright (C) 2003-2007 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.prorefactor.core.schema.Schema;
import org.prorefactor.refactor.settings.ApplicationSettings;
import org.prorefactor.refactor.settings.ProgressProjectSettings;
import org.prorefactor.refactor.settings.ProparseProjectSettings;

import com.joanju.proparse.Environment;


/**
 * This "Singleton" class provides an interface to an org.prorefactor.refactor session.
 * Much of this class was originally put in place for use of Proparse within
 * an Eclipse environment, with references to multiple projects within Eclipse.
 */
public class RefactorSession {

	private RefactorSession() {
		readAppSettings();
	}
	
	private boolean projectBinariesEnabled = false;
	private long timeStamp;
	private ApplicationSettings appSettings = null;
	private Environment env = Environment.instance();
	private IDE ide = new IDEDefault(this);
	private String projectName = null;
	private ProgressProjectSettings progressSettings = null;
	private ProparseProjectSettings proparseSettings = null;
	private Schema schema = Schema.getInstance();
	private String contextDirName = "";

	private static RefactorSession theInstance;

	
	
	
	private void configureProparse() throws Exception {

		schema.clear();
		if (proparseSettings.schemaFile!=null && proparseSettings.schemaFile.length()>0) {
			schema.loadSchema(proparseSettings.schemaFile);
		}

		env.configSet("batch-mode", progressSettings.batchmode ? "true" : "false");
		env.configSet("keyword-all", proparseSettings.keywordall);
		env.configSet("opsys", progressSettings.opsys);
		env.configSet("propath", progressSettings.propath);
		env.configSet("proversion", progressSettings.proversion);
		env.configSet("window-system", progressSettings.windowSystem);

		schema.aliasDelete(null); // deletes all
		String [] alias = progressSettings.dbAliases.split(",");
		for (int i = 0; i < alias.length - 1; i = i + 2) {
			schema.aliasCreate(alias[i], alias[i+1]);
		}

	}


	public void disableParserListing() {
		env.configSet("listing-file", "");
	}


	public void enableParserListing() {
		env.configSet("listing-file", getListingFileName());
	}


	public ApplicationSettings getAppSettings() {
		return appSettings;
	}

	public static String getAppSettingsFilename() {
		return getContextDirName() + "prorefactor/settings/application.properties";
	}
	
	/** The directory that the "prorefactor" config and data files directory is to be found in.
	 * This static method checks to see if the RefactorSession Singleton
	 * has been instantiated, and returns the context dir from that if so, otherwise it returns
	 * an empty string.
	 * Any application which assigned the contextDirName should have done so with a trailing slash.
	 */
	public static String getContextDirName() {
		if (theInstance!=null) return theInstance.contextDirName;
		else return "";
	}

	public IDE getIDE() { return ide; }

	/** Get a string for the indent for the current project.
	 * Returns a tab or some number of spaces (ex: "   ").
	 */
	public String getIndentString() {
		String indentString;
		if (getProparseSettings().indentTab) indentString = "\t";
		else {
			int spaces = getProparseSettings().indentSpaces;
			char [] ca = new char[spaces];
			Arrays.fill(ca, ' ');
			indentString = new String(ca);
		}
		return indentString;
	}

	/** Get the Singleton instance. */
	public static RefactorSession getInstance() {
		if (theInstance == null)
			theInstance = new RefactorSession();
		return theInstance;
	}

	/** Get the listing file name, makes sure the directory exists. */
	public static String getListingFileName() {
		String ret = getContextDirName() + "prorefactor/temp/listingfile.txt";
		(new File(ret)).getParentFile().mkdirs();
		return ret;
	}

	public static String getMessagesFileName() {
		return getContextDirName() + "prorefactor/refactor.messages";
	}
	
	/** Are the project binaries (.pub, .msg) enabled? */
	public boolean getProjectBinaraiesEnabled() { return projectBinariesEnabled; }

	/** Returns instance context directory plus "prorefactor/projects/". */
	public static String getProjectsDirName() {
		return getContextDirName() + "prorefactor/projects/";
	}

	public ProgressProjectSettings getProgressSettings() {
		if (progressSettings == null)
			progressSettings = new ProgressProjectSettings(null);
		return progressSettings;
	}

	public static String getProgressSettingsFilename(String projectName) {
		return getProjectsDirName() + projectName + "/progress.properties";
	}

	/** Returns the name of the currently loaded project */
	public String getProjectName() {
		return projectName;
	}

	/** Returns the Settings for the currently loaded project */
	public ProparseProjectSettings getProparseSettings() {
		if (proparseSettings == null)
			proparseSettings = new ProparseProjectSettings(null, null);
		return proparseSettings;
	}

	public static String getProparseSettingsFilename(String projectName) {
		return getProjectsDirName() + projectName + "/proparse.properties";
	}
	
	/** Returns the instance context directory plus "prorefactor/" */
	public static String getProrefactorDirName() {
		return getContextDirName() + "prorefactor/";
	}

	public String getProRefactorProjectDir() {return getProjectsDirName() + projectName;}

	public String getProRefactorProjectDir(String inputProjectName) {return getProjectsDirName() + inputProjectName;}

	/** Get the rollback directory, creates it if it doesn't exist. */
	public File getRollbackDir() { 
		File ret = new File(getRollbackDirName()); 
		ret.mkdirs();
		return ret;
	}

	/** Get the name of the rollback directory, creates it if it doesn't exist. */
	public static String getRollbackDirName() {
		String ret = getContextDirName() + "prorefactor/rollback";
		new File(ret).mkdirs();
		return ret;
	}

	/** Get the temp directory, creates it if it doesn't exist. */
	public File getTempDir() {
		File ret = new File(contextDirName + "prorefactor/temp");
		ret.mkdirs();
		return ret;
	}
	
	
	/** Make sure that Proparse's configuration gets reloaded */
	public static void invalidateCurrentSettings() {
		if (theInstance!=null) theInstance.projectName = null;
	}


	/** Only loads the project's settings if it's not already the current project.
	 * Uses relative path for prorefactor/projects/_projectname_.
	 */
	public void loadProject(String nameToLoad) throws Exception {
		if (nameToLoad==null || nameToLoad.length()==0)
			throw new Exception("No project selected");
		if (	this.projectName!=null
			&&	this.projectName.equals(nameToLoad)
			&&	schemaFileIsCurrent()
			) return;
		try {appSettings.loadSettings();} catch (FileNotFoundException e) {}
		progressSettings = new ProgressProjectSettings(getProgressSettingsFilename(nameToLoad));
		try {progressSettings.loadSettings();} catch (FileNotFoundException e) {}
		proparseSettings = 
			new ProparseProjectSettings(
				getProparseSettingsFilename(nameToLoad)
				, nameToLoad
				);
		try {proparseSettings.loadSettings();} catch (FileNotFoundException e) {}
		configureProparse();
		this.projectName = nameToLoad;
		timeStamp = System.currentTimeMillis();
	}


	/** Loads the project, whether it's already loaded or not */
	public void loadProjectForced(String nameToLoad) throws Exception {
		invalidateCurrentSettings();
		loadProject(nameToLoad);
	}
	
	
	/** If you don't know what project a file is in, then use this.
	 * Will continue to use the current project if the file is in it, otherwise
	 * the first project found to contain the file will be loaded.
	 */
	public void loadProjectForFile(File file) throws Exception {
		String s = getIDE().getProjectRelativePath(file)[0];
		loadProject(s);
	}
	
	
	/** Read progress and prorefactor properties, and schema, from a fully qualified path.
	 * RefactorSession was originally designed for reading project
	 * settings from a relative prorefactor/projects directory, for multiple projects
	 * in one Eclipse session.
	 * This method is different - it is for reading progress.properties and
	 * prorefactor.properties from a fully qualified directory name.
	 * It is expected that the schema file name will be specified in proparse.properties. 
	 */
	public void loadProjectPropertiesFromDirectory(String fullyQualifiedDirName) throws Exception {
		progressSettings = new ProgressProjectSettings(fullyQualifiedDirName + "/progress.properties");
		progressSettings.loadSettings();
		proparseSettings = new ProparseProjectSettings(fullyQualifiedDirName + "/proparse.properties", "");
		proparseSettings.loadSettings();
		configureProparse();
	}


	/** This does not create the file if it does not exist.
	 * Not creating the file is important, because when the singleton instance
	 * is created, it won't have any context directory set up, which might
	 * be necessary depending on the application.
	 *
	 */
	private void readAppSettings() {
		appSettings = new ApplicationSettings(getAppSettingsFilename());
		try {
			appSettings.loadSettings();
		} catch (Throwable e) {}
	}

	
	/** Check that the schema file has not been modified since load. */
	private boolean schemaFileIsCurrent() {
		assert (proparseSettings != null);
		File schemaFile = new File(proparseSettings.schemaFile);
		if (! schemaFile.exists()) return true; // scratch projects might have no schema
		return (schemaFile.lastModified() < timeStamp);
	}
	
	
	/** The directory that the "prorefactor" config and data files directory is to be found in.
	 * Should never be null. 
	 * <b>Important: </b>Calling application is responsible for appending a trailing slash on the string.
	 */
	public void setContextDirName(String contextDirName) {
		this.contextDirName = contextDirName;
		readAppSettings();
	}


	public RefactorSession setIDE(IDE ide) { this.ide = ide; return this; }
	

	/** Disable the project directory binary output files (.pub, .msg).
	 * These are disabled by default.
	 */
	public void setProjectBinariesEnabledOff() {
		projectBinariesEnabled = false;
	}


	/** Enable the project directory binary output files (.pub, .msg).
	 * These are disabled by default.
	 */
	public void setProjectBinariesEnabledOn() {
		projectBinariesEnabled = true;
	}


}
