/* PositionIndex.java
 * Created on Feb 26, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;



/** Provides an index of JPNode objects, sorted by fileIndex, line, and column.
 */
public class PositionIndex {

	private SortedSet nodeSet = new TreeSet(FILE_POSITION);


	/**
	 * Comparator for sorting by fileIndex/line/col.
	 */
	private static final Comparator FILE_POSITION = new Comparator() {
		public int compare(Object o1, Object o2) {
			int ret;
			JPNode n1 = (JPNode) o1;
			JPNode n2 = (JPNode) o2;
			ret = n1.getFileIndex() - n2.getFileIndex();
			if (ret!=0) return ret;
			ret = n1.getLine() - n2.getLine();
			if (ret!=0) return ret;
			return n1.getColumn() - n2.getColumn();
		}
	};



	public void addNode(JPNode node) {
		nodeSet.add(node);
	}



	/** Get the node at a position, or the next node immediately after the position. */
	public JPNode getNodeFrom(int file, int line, int col) {
		SortedSet tailSet = nodeSet.tailSet(new JPNode(file, line, col));
		if (tailSet.size()==0) return null;
		return (JPNode) tailSet.first();
	}


} // class
