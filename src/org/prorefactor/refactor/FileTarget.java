/** 11-May-07 by John Green
 * 
 * Copyright (C) 2007 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/** A simple, sortable data structure for file, line, and column.
 * I use this as a key for JDBM records.
 */
public class FileTarget implements Comparable, Serializable {

	/** Filename used for sorting as input, so you might want to input canonicalpath. */
	public FileTarget(String filename, int line, int column) {
		this.filename = filename;
		this.line = line;
		this.column = column;
	}

	public String filename;
	public int line;
	public int column;
	private static final long serialVersionUID = 1L;

	
	public int compareTo(Object o2) {
		FileTarget that = (FileTarget)o2;
		int ret;
		ret = filename.compareTo(that.filename);
		if (ret!=0) return ret;
		ret = line - that.line;
		if (ret!=0) return ret;
		return column - that.column;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof FileTarget)) return false;
		FileTarget that = (FileTarget)obj;
		return filename.equals(that.filename) && line==that.line && column==that.column;
	}
	
	
	@Override
	public int hashCode() {
		return filename.hashCode() * 65537 + line * 257 * column;
	}

	
	@Override
	public String toString() {
		try {
			return new JSONObject().put("file",filename).put("line",line).put("col",column).toString();
		} catch (JSONException e) {
			return e.toString();
		}
	}

	
}
