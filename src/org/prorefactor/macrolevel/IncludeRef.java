/* IncludeRef.java
 * Created on Nov 29, 2003
 * John Green
 *
 * Copyright (C) 2003-2007 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.macrolevel;

import com.joanju.DataXferStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

/**
 */
public class IncludeRef extends MacroRef {

	/** Only to be used for persistence/serialization. */
	public IncludeRef() {}

	IncludeRef(int listingFileLine) {
		super(listingFileLine);
	}

	public boolean usesNamedArgs;
	public int fileIndex;
	private ArrayList<MacroDef> includeArgs = new ArrayList<MacroDef>();
	private HashMap<String, MacroDef> argMap = new HashMap<String, MacroDef>();
	private String fileRefName = "";

	private static final long serialVersionUID = 6085433112733922276L;


	public void addNamedArg(MacroDef arg) {
		includeArgs.add(arg);
		argMap.put(arg.name.toLowerCase(), arg);
	}



	public void addNumberedArg(MacroDef arg) {
		includeArgs.add(arg);
	}


	/** Count from 1, the way that the arguments are referenced in ABL. */
	public MacroDef getArgNumber(int num) {
		if (num>0 && num <= includeArgs.size())
			return includeArgs.get(num - 1);
		return null;
	}

	
	@Override
	public int getFileIndex() { return fileIndex; }


	/** Get the string that was used for referencing the include file name.
	 * For example, if the code was {includeMe.i}, then the string
	 * "includeMe.i" is returned.
	 * Note: For Proparse versions earlier than 3.1C, this will return
	 * and empty string.
	 */
	public String getFileRefName() {return fileRefName;}


	public MacroDef lookupNamedArg(String name) {
		if (!usesNamedArgs) return null;
		return argMap.get(name.toLowerCase());
	}


	public int numArgs() {
		return includeArgs.size();
	}


	public void setFileRefName(String fileRefName) {this.fileRefName = fileRefName;}


	public MacroDef undefine(String name) {
		MacroDef theArg = argMap.get(name);
		if (theArg != null) {
			argMap.remove(name);
			argMap.put("", theArg);
			return theArg;
		}
		return null;
	}


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(argMap);
		out.writeInt(fileIndex);
		out.writeRef(fileRefName);
		out.writeRef(includeArgs);
		out.writeBool(usesNamedArgs);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
		out.schemaRef("argMap");
		out.schemaInt("fileIndex");
		out.schemaRef("fileRefName");
		out.schemaRef("includeArgs");
		out.schemaBool("usesNamedArgs");
	}


}
