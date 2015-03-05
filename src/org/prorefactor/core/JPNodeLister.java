/* Created on 25-Nov-2005
 * Authors: john
 *
 * Copyright (c) 2002-2011 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;



/** Prints out the structure of a JPNode AST.
 * Prints nodes one per line, using indentation to show the tree structure.
 */
public class JPNodeLister {

	public JPNodeLister(JPNode topNode, String outfilename, TokenTypesI tokenTypes) {
		this.topNode = topNode;
		this.outfilename = outfilename;
		this.tokenTypes = tokenTypes;
	}

	public boolean showColumn = false;
	public boolean showFilename = false;
	public boolean showLinenum = false;
	public boolean showStoretype = false;
	public String lineSep = System.getProperty("line.separator");

	// Change the comments for the setter if you change the default here.
	private int indentby = 4;
	private int indentnum = 0;
	private FileWriter ofile;
	private HashMap<Integer, String> indentStrings = new HashMap<Integer, String>();
	private JPNode topNode;
	private String outfilename;
	// Change the comments for the setter if you change the default here.
	protected String spacer = "    ";
	protected TokenTypesI tokenTypes = null;




	/** This returns the line's text including the text indent, but not including the newline.
	 * Override this method in order to generate your own line text.
	 * If you override this, you can use indent() to get the current indent string, or use
	 * getIndentby() and your own indent string generator.
	 */
	protected String generateLineText(JPNode node) {
		StringBuilder buff = new StringBuilder();
		buff
			.append(indent())
			.append(tokenTypes.getName(node.getType()))
			.append(spacer)
			.append(node.getText())
			.append(spacer)
			;
		if (showLinenum)
			buff
				.append(node.getLine())
				.append(" ")
				;
		if (showColumn)
			buff
				.append(node.getColumn())
				.append(" ")
				;
		if (showFilename)
			buff
				.append(node.getFilename())
				.append(spacer)
				;
		if (showStoretype) {
			String storetype = node.attrGetS(IConstants.STORETYPE);
			if (storetype.length() != 0)
				buff
					.append(storetype)
					.append(spacer)
					;
		}
		return buff.toString();
	}

	
	/** Get the current indent based on indentby */
	protected final String indent() {
		String indent = indentStrings.get(indentnum);
		if (indent==null) {
			char[] indentArray = new char[indentnum];
			java.util.Arrays.fill(indentArray, ' ');
			indent = new String(indentArray);
			indentStrings.put(indentnum, indent);
		}
		return indent;
	}
	
	
	/** Call this method to write the output file. */
	public void print() throws IOException {
		ofile = new FileWriter(outfilename);
		print_sub(topNode);
		ofile.close();
	}


	private void print_sub(JPNode node) throws IOException {
		printline(node);
		JPNode child = node.firstChild();
		indentnum += indentby;
		while (child!=null) {
			if (child.firstChild()!=null)
				print_sub(child);
			else
				printline(child);
			child = child.nextSibling();
		}
		indentnum -= indentby;
	}
	

	private void printline(JPNode node) throws IOException {
		ofile.write(generateLineText(node));
		ofile.write(lineSep);
	}


	/** Number of spaces to indent by.
	 * Default indentby is four spaces. You can change the number of indent spaces,
	 * or use your own indent generator when you override generateLineText.
	 */
	public JPNodeLister setIndentby(int indentby) { this.indentby = indentby; return this; }

	/** The String spacer is used to separate tokens or components of what is printed on one line.
	 * Default is four spaces.
	 */
	protected JPNodeLister setSpacer(String spacer) { this.spacer = spacer; return this; }


}
