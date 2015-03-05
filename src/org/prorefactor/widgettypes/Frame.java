/* Created on May 3, 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005-2006 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.widgettypes;


import org.prorefactor.core.TokenTypes;
import org.prorefactor.treeparser.Block;
import org.prorefactor.treeparser.FieldContainer;
import org.prorefactor.treeparser.Symbol;
import org.prorefactor.treeparser.SymbolScope;
import com.joanju.DataXferStream;

import java.io.IOException;


public class Frame extends FieldContainer {

	/** Only to be used for persistence/serialization. */
	public Frame() {}

	/** Unlike other symbols, Frames are automatically added to the scope, right here at creation time. */
	public Frame(String name, SymbolScope scope) {
		super(name, scope);
		scope.add(this);
	}

	private boolean initialized = false;
	private Block frameScopeBlock = null;

	

	@Override
	public Symbol copyBare(SymbolScope scope) {
		// Frames cannot be inherited, so we don't have to worry about the other frame attributes.
		return new Frame(getName(), scope);
	}

	
	public Block getFrameScopeBlock() { return frameScopeBlock; }

	
	/** Returns TokenTypes.FRAME. */
	public int getProgressType() { return TokenTypes.FRAME; }

	
	/** Initialize the frame and set the frame scope if not done already.
	 * Returns the frameScopeBlock.
	 * @see #isInitialized()
	 */
	public Block initialize(Block block) {
		if (initialized) return frameScopeBlock;
		initialized = true;
		if (frameScopeBlock==null) frameScopeBlock = block.addFrame(this);
		return frameScopeBlock;
	}
	
	
	/** Has this frame been "referenced"?
	 * In other words, has it or any of its fields been displayed yet?
	 * Has its scope been determined?
	 */
	public boolean isInitialized() { return initialized; }


	/** This should be called for a block with an explicit default.
	 * i.e. {DO|FOR|REPEAT} WITH FRAME.
	 */
	public void setFrameScopeBlockExplicitDefault(Block block) {
		frameScopeBlock = block;
		block.setDefaultFrameExplicit(this);
	}

	
	/** This should be called when we need to set a block with this
	 * unnamed frame as that block's implicit default.
	 * Returns the block that this unnamed/default frame got scoped to.
	 * That would be a REPEAT or FOR block, or else the frame's symbol scope.
	 */
	public Block setFrameScopeUnnamedDefault(Block block) {
		frameScopeBlock = block.setDefaultFrameImplicit(this);
		return frameScopeBlock;
	}


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(frameScopeBlock);
		// 'initialized' is a state variable.
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
		out.schemaRef("frameScopeBlock");
	}


}
