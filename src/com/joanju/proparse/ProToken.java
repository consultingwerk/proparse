/* ProToken.java

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import com.joanju.Xferable;
import com.joanju.DataXferStream;

import java.io.IOException;
import java.util.ArrayList;


public class ProToken extends antlr.CommonHiddenStreamToken implements Xferable {

	protected final ArrayList<ArrayList<ProToken>> containedIn;
	
	/** Only to be used for persistence/serialization. */
	public ProToken() {
		this.containedIn = new ArrayList<ArrayList<ProToken>>();
	}

	public ProToken(IntegerIndex<String> filenameList, int type, String s) {
		super(type, s);
		this.filenameList = filenameList;
		this.containedIn = new ArrayList<ArrayList<ProToken>>();
	}

	public ProToken(IntegerIndex<String> filenameList, int type, String txt, int file, int line, int col, int macroSourceNum) {
		super(type, txt);
		this.filenameList = filenameList;
		fileIndex = file;
		this.macroSourceNum = macroSourceNum;
		this.line = line;
		this.col = col;
		this.containedIn = new ArrayList<ArrayList<ProToken>>();
	}

	public ProToken(ProToken orig) {
		super(orig.getType(), orig.getText());
		this.filenameList = orig.filenameList;
		this.fileIndex = orig.fileIndex;
		this.macroSourceNum = orig.macroSourceNum;
		this.line = orig.line;
		this.col = orig.col;
		this.containedIn = new ArrayList<ArrayList<ProToken>>();
	}
	
	protected void addedToArrayList (ArrayList<ProToken> list)
	{
		if (!this.containedIn.contains(list))
			this.containedIn.add(list);
	}
	
	public void replaceInArrayLists (ProToken newToken)
	{
		for (ArrayList<ProToken> list: this.containedIn)
			if (list.contains(this))
				list.set(list.indexOf(this), newToken);
	}

	int fileIndex;
	int macroSourceNum;
	IntegerIndex<String> filenameList;




	public int getFileIndex() {return fileIndex;}

	/** A reference to the collection of filenames from the parse. */
	public IntegerIndex<String> getFilenameList() {return filenameList;}


	@Override
	public String getFilename() {
		if (filenameList==null || fileIndex<0 || fileIndex>filenameList.size())
			return "";
		String ret = filenameList.getValue(fileIndex);
		if (ret==null)
			ret = "";
		return ret;
	}


	public int getMacroSourceNum() {return macroSourceNum;}
	

	/** Convenience method for (ProToken) getHiddenAfter(). */
	public ProToken getNext() {return (ProToken) getHiddenAfter();}


	/** Convenience method for (ProToken) getHiddenBefore(). */
	public ProToken getPrev() {return (ProToken) getHiddenBefore();}


	public void setHiddenAfter(ProToken t) {
		super.setHiddenAfter(t);
	}


	public void setHiddenBefore(ProToken t) {
		super.setHiddenBefore(t);
	}


	public void setFileIndex(int fileIndex) {this.fileIndex = fileIndex;}

	/** A reference to the collection of filenames from the parse. */
	public void setFilenameList(IntegerIndex<String> filenameList) {
		this.filenameList = filenameList;
	}
	
	public void setMacroSourceNum(int macroSourceNum) {this.macroSourceNum = macroSourceNum;}


	/** Implement Xferable. */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeInt(getType());
		out.writeInt(getMacroSourceNum());
		out.writeInt(getFileIndex());
		out.writeInt(getLine());
		out.writeInt(getColumn());
		out.writeRef(getText());
	}
	public void writeXferSchema(DataXferStream out) throws IOException {
		out.schemaInt("type");
		out.schemaInt("macroSourceNum");
		out.schemaInt("fileIndex");
		out.schemaInt("line");
		out.schemaInt("column");
		out.schemaRef("text");
	}


}
