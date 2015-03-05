/* Environment.java


Notes:
- This class is partly redundant with RefactorSession. Now that Proparse and
ProRefactor are integrated, the Proparse calls to Environment should be
replaced with calls to RefactorSession.


Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
 */
package com.joanju.proparse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.io.File;

import org.prorefactor.refactor.RefactorSession;


/** Stores parser configuration and handles environment specifics like path.
 * In Proparse-C++, this was enforced as a singleton.
 * In Java, I'm allowing for the possibility that different
 * parser environments might exist in different threads.
 */
public class Environment {

	/** This class is not quite a singleton.
	 * In Proparse-C++, this was enforced as a singleton.
	 * In Java, I'm allowing for the possibility that different
	 * parser environments might exist in different threads.
	 * @see Environment#instance()
	 */
	public Environment() { }

	// The defaults used here are also described in the user docs.
	// PROVERSION gets no default. If {&PROVERSION} is used but undefined,
	// the parser stops with an error telling you to set it.

	/** determines whether {&proparse_directive ...} within the
	 * parsed code is turned into a 'special' node or not. */
	boolean proparseDirectives = true;
	int opsysNum = OPSYS_WINDOWS;
	String batchMode = "NO";
	String keywordAll;
	/** If non-blank, the preprocessor listing gets written to this file */
	String listingFile;
	private boolean multiParse = false;
	String opsys = "WIN32";
	/** The original text that they set this flag with.
	 * Store this in case they want to get() it and compare it. */
	String proparseDirectivesText;
	String proversion;
	String warning;
	String windowSystem = "MS-WIN95";
	ArrayList<String> path = new ArrayList<String>();

	private HashMap<String, SymbolScope> superCache = new HashMap<String, SymbolScope>();

	public static final int OPSYS_WINDOWS = 1;
	public static final int OPSYS_UNIX = 2;
	private static Environment instance = null;

	private static HashSet<String> validFlags = new HashSet<String>();

	static {
		validFlags.addAll(Arrays.asList(
				"batch-mode",
				"keyword-all",
				"listing-file",
				"multi-parse",
				"opsys",
				"propath",
				"proversion",
				"show-proparse-directives",
				"window-system"
				));
	}



	/** Adds an inheritance scope regardless of the multiParse flag.
	 * Deals with name's letter case.
	 */
	void addToSuperCache(String name, SymbolScope scope) {
		superCache.put(name.toLowerCase(), scope);
	}


	void addToPath(String dirName) {
		path.add(dirName);
	}


	/** This gets called by DoParse at cleanup time, if multiParse==false. */
	void clearSuperCache() {
		superCache.clear();
	}


	boolean configIsValidFlag(String flag) {
		return validFlags.contains(flag.trim().toLowerCase());
	}


	public String configGet(String flag) {
		flag = flag.trim().toLowerCase();
		if (flag.equals("batch-mode"))
			return batchMode;
		if (flag.equals("keyword-all"))
			return keywordAll;
		if (flag.equals("listing-file"))
			return listingFile;
		if (flag.equals("multi-parse"))
			return multiParse ? "true" : "false" ;
		if (flag.equals("opsys"))
			return opsys;
		if (flag.equals("propath")) {
			String retpath = "";
			boolean delim = false;
			for (String p : path) {
				if (delim)
					retpath += ",";
				retpath += p;
				delim = true;
			}
			return retpath;
		}
		if (flag.equals("proversion"))
			return proversion;
		if (flag.equals("show-proparse-directives"))
			return proparseDirectivesText;
		if (flag.equals("window-system"))
			return windowSystem;
		return "";
	}


	public void configSet(String flag, String val) {
		RefactorSession refactorSession = RefactorSession.getInstance();
		flag = flag.trim().toLowerCase();
		if (flag.equals("batch-mode")) {
			batchMode = val;
			refactorSession.getProgressSettings().batchmode = configValToBool(val);
		} else if (flag.equals("keyword-all")) {
			keywordAll = val;
			refactorSession.getProparseSettings().keywordall = val;
		} else if (flag.equals("listing-file")) {
			listingFile = val;
		} else if (flag.equals("multi-parse")) {
			multiParse = configValToBool(val);
		} else if (flag.equals("opsys")) {
			refactorSession.getProgressSettings().opsys = val;
			// Store exactly the string they gave us, then check if it's "unix"
			opsys = val;
			if (val.trim().toLowerCase().equals("unix"))
				opsysNum = OPSYS_UNIX;
			else
				opsysNum = OPSYS_WINDOWS;
		} else if (flag.equals("propath")) {
			refactorSession.getProgressSettings().propath = val;
			path.clear();
			path.addAll(Arrays.asList(val.split(",")));
		} else if (flag.equals("proversion")) {
			refactorSession.getProgressSettings().proversion = val;
			proversion = val;
		} else if (flag.equals("show-proparse-directives")) {
			proparseDirectivesText = val;
			proparseDirectives = configValToBool(val);
		} else if (flag.equals("window-system")) {
			refactorSession.getProgressSettings().windowSystem = val;
			windowSystem = val;
		}
	} // configSet()


	boolean configValToBool(String val) {
		String s = val.trim().toLowerCase();
		return (s.equals("true") || s.equals("yes"));
	}



	/** Find a file on the PROPATH.
	 * Returns an empty string if not found.
	 * Does not normalize the file name.
	 */
	String findFile(String fileName) {
		// If we have an absolute path-filename, we don't search the path.
		// If we have a relative (starts with dot) path-filename, ditto.
		int len = fileName.length();
		if	(	(	len > 0
				&&	(	fileName.charAt(0) == '/'
					||	fileName.charAt(0) == '\\'
					)
				)
			||	(	len > 1
				&&	fileName.charAt(1) == ':'  // Windows drive letter, ex: "C:"
				)
			||	(	len > 1
				&&	fileName.charAt(0) == '.'  // Relative path, "./" or "../"
				)
			) {
			if (new File(fileName).exists())
				return fileName;
		}
		for (String p : path) {
			String tryPath = p + File.separatorChar + fileName;
			if (new File(tryPath).exists())
				return tryPath;
		}
		return "";
	}


	/** Get the default instance.
	 * In Proparse-C++, this class was enforced as a singleton.
	 * In Java, I'm allowing for the possibility that different
	 * parser environments might exist in different threads.
	 */
	public static Environment instance() {
		if (instance==null)
			instance = new Environment();
		return instance;
	}


	boolean isMultiParse() { return multiParse; }


	/** The lookup deals with the name's letter case. */
	SymbolScope lookupSuper(String superName) {
		return superCache.get(superName.toLowerCase());
	}


}
