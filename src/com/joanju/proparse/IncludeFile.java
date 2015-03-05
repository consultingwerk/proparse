/*
IncludeFile.java

A preprocessor contains one or more IncludeFiles.

There is a special IncludeFile object created for the top-level program (ex: .p or .w).

Every time the lexer has to scan an include file, we create an IncludeFile object,
for managing include file arguments and pre-processor scopes.

We keep an InputSource object, which has an input stream.

Each IncludeFile object will have one or more InputSource objects.

The bottom InputSource object for an IncludeFile is the input for the include file itself.

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

public class IncludeFile {

	IncludeFile(String referencedWithName, InputSource is) {
		inputVector.add(is);
		// {0} must return the name that this include file was referenced with.
		numdArgs.add(referencedWithName);
	}
	
	HashMap<String, String> defdNames = new HashMap<String, String>();
	LinkedList<InputSource> inputVector = new LinkedList<InputSource>();
	ArrayList<String> numdArgs = new ArrayList<String>();
	HashMap<String, String> namedArgs = new HashMap<String, String>();
	ArrayList<NamedArgIn> namedArgsIn = new ArrayList<NamedArgIn>();

	class NamedArgIn {
		NamedArgIn(String name, String arg) {
			this.name = name;
			this.arg = arg;
		}
		String name;
		String arg;
	}




	void defNamedArg(String name, String arg) {
		namedArgsIn.add(new NamedArgIn(name, arg));
		String lname = name.toLowerCase();
		// The first one defined is the one that gets used
		if (! namedArgs.containsKey(lname))
			namedArgs.put(lname, arg);
		// Named include arguments can also be referenced by number.
		numdArgs.add(arg);
	}


	String getAllNamedArgs() {
		String allArgs = "";
		String currArg;
		for (NamedArgIn aNamedArgsIn : namedArgsIn) {
			if (allArgs.length() > 0)
				allArgs += " ";
			currArg = aNamedArgsIn.name;
			allArgs += "&" + currArg + "=\"";
			allArgs += aNamedArgsIn.arg + "\"";
		}
		return allArgs;
	}


	/** Get a named arg.
	 * @param name Arg name. If blank, returns first blank (undefined) named arg.
	 * @return null if not found.
	 */
	String getNamedArg(String name) {
		// If name is blank, return the first blank (undefined) named argument (if any).
		if (name.length()==0) {
			for (NamedArgIn nargin : namedArgsIn) {
				if (nargin.name.length()==0)
					return nargin.arg;
			}
			return null;
		}
		return namedArgs.get(name.toLowerCase());
	}


	boolean undefNamedArg(String name) {
		String lname = name.toLowerCase();
		// Find the first one and clobber it
		boolean found = false;
		for (NamedArgIn nargin : namedArgsIn) {
			if (nargin.name.equalsIgnoreCase(name)) {
				// Erase the argument name, which seems to be what Progress does.
				nargin.name = "";
				found = true;
				break;
			}
		}
		if (!found)
			return false;
		// Now see if that named argument got assigned more than once
		found = false;
		for (NamedArgIn nargin : namedArgsIn) {
			if (nargin.name.equalsIgnoreCase(name)) {
				namedArgs.put(lname, nargin.arg);
				found = true;
				break;
			}
		}
		if (!found)
			namedArgs.remove(lname);
		return true;
	}


}
