/** 3 May 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005-2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import java.util.ArrayList;
import java.io.IOException;

import org.prorefactor.core.TokenTypes;
import com.joanju.DataXferStream;


/** A Symbol defined with DEFINE DATASET. */
public class Dataset extends Symbol {

	/** Only to be used for persistence/serialization. */
	public Dataset() {}

	public Dataset(String name, SymbolScope scope) {
		super(scope);
		setName(name);
	}
	
	/** Keep the buffers, in order, as part of the DATASET signature. */
	ArrayList<TableBuffer> buffers = new ArrayList<TableBuffer>();

	
	
	/** The treeparser calls this at RECORD_NAME in
	 * <code>RECORD_NAME in FOR RECORD_NAME (COMMA RECORD_NAME)*</code>.
	 */
	public void addBuffer(TableBuffer buff) {
		buffers.add(buff);
	}
	
	
	@Override
	public Symbol copyBare(SymbolScope scope) {
		return new Dataset(getName(), scope);
	}

	
	/** For this subclass of Symbol, fullName() returns the same value as getName(). */
	@Override
	public String fullName() { return getName(); }
	
	
	/** Get the list of buffers (in order) which make up this dataset's signature. */
	public ArrayList<TableBuffer> getBuffers() { return buffers; }

	
	/** Returns TokenTypes.DATASET.
	 * @see org.prorefactor.treeparser.Symbol#getProgressType()
	 */
	@Override
	public int getProgressType() {
		return TokenTypes.DATASET;
	}


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(buffers);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
		out.schemaRef("buffers");
	}
}
