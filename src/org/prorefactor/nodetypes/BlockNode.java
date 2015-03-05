/* Created on Apr 29, 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.nodetypes;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.Block;
import com.joanju.proparse.ProToken;


public class BlockNode extends JPNode {

	/** For creating from persistent storage */
	public BlockNode() { super(); }

	public BlockNode(ProToken t) {super(t);}

	public BlockNode(int file, int line, int column) { super(file, line, column); }

	public Block getBlock() {
		Block block = (Block) getLink(JPNode.BLOCK);
		assert block != null;
		return block;
	}

	/** Every JPNode subtype has its own index. Used for persistent storage. */
	@Override
	public int getSubtypeIndex() { return 2; }

	public void setBlock(Block block) {
		setLink(JPNode.BLOCK, block);
	}
	
}
