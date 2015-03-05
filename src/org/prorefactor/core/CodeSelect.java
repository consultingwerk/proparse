/* CodeSelect.java
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

import java.util.ArrayList;



/** Provides methods for selecting surrounding code based on the AST,
 * and shrinking and growing that selection.
 */
public class CodeSelect {

	public CodeSelect(int file, int line, int col, PositionIndex index) {
		this.startingNode = index.getNodeFrom(file, line, col);
		if (startingNode==null) return;
		stack.add(startingNode);
		for (JPNode n = startingNode.parent(); n!=null; n = n.parent()) {
			stack.add(n);
		}
	}

	private int currBranch = -1;
	private ArrayList stack = new ArrayList();
	private JPNode startingNode;



	/** Move cursor down tree towards initial position.
	 * Returns null if already at the lowest node.
	 */
	public JPNode contract() {
		if (currBranch < 1) return null;
		JPNode node = (JPNode) stack.get(--currBranch);
		return node;
	}



	/** Given the top node in a branch, find the file/line/column of the end of
	 * the last character of the last node in the branch.
	 * This does not work for Program_root: the last child is a synthetic node,
	 * Program_tail.
	 */
	public static int[] branchEndPos(JPNode node) {
		JPNode end = node.lastDescendant();
		if (end==null) return null;
		return new int[]
			{end.getFileIndex(), end.getLine(), end.getColumn() + end.getText().length()};
	}



	/** Move cursor up tree to parent
	 * Returns null if already at highest parent node.
	 */
	public JPNode expand() {
		if (currBranch+1 > stack.size()-1) return null;
		JPNode node = (JPNode) stack.get(++currBranch);
		return node;
	}



} // class
