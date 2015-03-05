/**
 * RefactorTarget.java
 * @author John Green
 * 17-Jul-2003
 * www.joanju.com
 * 
 * Copyright (c) 2003 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.refactor;


/**
 * Represents a (potential) target for refactoring.
 * Implements "Comparable" so that this class can be sorted.
 * Is sorted by filename, linenumber, column, in reverse, so that we can
 * use Iterator to move through each file, starting at the bottom
 * of each. (Currently required as a workaround for AST/source-file
 * line number synchronization after file changes.)
 */
public class RefactorTarget implements Comparable {


	public int[] changedLines;
	public int column;
	public String filename;
	public int linenum;
	public int nodeHandle;


	public RefactorTarget() {
		changedLines = new int[4];
	}



	/**
	 * This class comparison is based on filename, linenumber, and column, in reverse.
	 */
	public int compareTo(Object o) {
		RefactorTarget right = (RefactorTarget)o;
		int lastCmp = right.filename.compareTo(filename);
		if (lastCmp!=0) return lastCmp;
		lastCmp = right.linenum - linenum;
		return (lastCmp!=0 ? lastCmp : right.column - column);
	} // compareTo()



	/**
	 * This class equality match is based on class, filename, line, and column.
	 */
	public boolean equals(Object o) {
		if ((o != null) && (o.getClass().equals(this.getClass()))) {
			RefactorTarget right = (RefactorTarget)o;
			return (
				filename.equals(right.filename)
				&& linenum == right.linenum
				&& column == right.column
				);
		}
		return false;
	} // equals()



	// Never override equals() without overriding hashCode().
	// We just use the line * 100 + column for the hash code.
	// Should be good enough (if it ever gets used).
	public int hashCode() {
		return linenum * 100 + column;
	} // hashCode()



} // class RefactorTarget

