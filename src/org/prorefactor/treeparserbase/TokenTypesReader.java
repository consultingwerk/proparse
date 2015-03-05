/* Created on 25-Nov-2005
 * Authors: john
 * 
 * Copyright (C) 2005-2006 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparserbase;

import org.prorefactor.core.TokenTypesI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


/** Provides a lookup of token names from numbers,
 * for ASTs which have more than the usual Proparse token types.
 * This is for specialized ASTs, and wouldn't be used for the usual
 * ASTs that flow from Proparse through ProRefactor.
 * This class gives us a map of token type numbers to token type names
 * and is useful in generating parsing debugging tools, such as token listers.
 * Reads the "XYZTokenTypes.txt" file in this class's package directory.
 * Subclass this in your own treeparsers directory in order to use it.
 * IMPORTANT: When subclassing:
 * 1. Copy the constructor to your subclass.
 * 2. Set your own value for the file name.
 */
public class TokenTypesReader implements TokenTypesI {

	// --- Copy this constructor to your subclass, change the file name ---
	public TokenTypesReader() throws IOException {
		inputStream = this.getClass().getResourceAsStream("ProParserTokenTypes.txt");
		initialize();
	}
	// --- End of what needs to be copied to subclasses ---

	boolean isInit = false;
	protected InputStream inputStream = null;
	HashMap<Integer, String> map = new HashMap<Integer, String>();

	
	void initialize() throws IOException {
		if (isInit) return;
		isInit = true;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		// Ignore the first two lines.
		reader.readLine();
		reader.readLine();
		for (String currLine = reader.readLine(); currLine!=null; currLine = reader.readLine()) {
			String [] parts = currLine.trim().split("=");
			map.put(
				// There may be two or three parts. Three parts ex: JAVAFOR="for"=10880
				Integer.parseInt(parts[parts.length-1])
				, parts[0]
				);
		}
	}
	
	
	public String getName(int type) {
		return map.get(type);
	}


}
