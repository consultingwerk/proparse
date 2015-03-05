/* BufferScope.java
 * Created on Apr 20, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparser;

import com.joanju.Xferable;
import com.joanju.DataXferStream;

import java.io.IOException;

/** A record of a BufferSymbol scope to a Block.
 * Tells us if the scope is "strong" or not.
 */
public class BufferScope implements Xferable {

	/** Only to be used for persistence/serialization. */
	public BufferScope() {}

	public BufferScope(Block block, TableBuffer symbol, int strength) {
		this.block = block;
		this.symbol = symbol;
		this.strength = strength;
	}

	private int strength;
	private Block block;
	private TableBuffer symbol;

	public static final int STRONG = 1;
	public static final int WEAK = 2;
	public static final int REFERENCE = 3;
	/** A "hidden cursor" is a BufferScope which has no side-effects on surrounding
	 * blocks like strong, weak, and reference scopes do. These are used within
	 * a CAN-FIND function. (2004.Sep:John: Maybe in triggers too? Haven't checked.)
	 */
	public static final int HIDDEN_CURSOR = 4;



	public Block getBlock() { return block; }

	/** Returns STRONG, WEAK, or REFERENCE */
	int getStrength() { return strength; }
	
	public TableBuffer getSymbol() { return symbol; }

	public boolean isStrong() { return strength==STRONG; }

	public boolean isWeak() { return strength==WEAK; }

	public void setBlock(Block block) { this.block = block; }

	public void setStrength(int strength) { this.strength = strength; }


	/** Implement Xferable. */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeRef(block);
		out.writeInt(strength);
		out.writeRef(symbol);
	}
	/** Implement Xferable. */
	public void writeXferSchema(DataXferStream out) throws IOException {
		out.schemaRef("block");
		out.schemaInt("strengthCode");
		out.schemaRef("tableBuffer");
	}


}
