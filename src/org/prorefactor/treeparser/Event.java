/** 6 May 2010 by John Green
 * 
 * Copyright (c) 2010 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import java.io.IOException;

import org.prorefactor.core.TokenTypes;
import com.joanju.DataXferStream;


/** A Symbol defined with DEFINE EVENT. */
public class Event extends Symbol {

	/** Only to be used for persistence/serialization. */
	public Event() {}

	public Event(String name, SymbolScope scope) {
		super(scope);
		setName(name);
	}
	
	
	@Override
	public Symbol copyBare(SymbolScope scope) {
		return new Event(getName(), scope);
	}

	
	/** For this subclass of Symbol, fullName() returns the same value as getName(). */
	@Override
	public String fullName() { return getName(); }
	
	
	/** Returns TokenTypes.EVENT.
	 * @see org.prorefactor.treeparser.Symbol#getProgressType()
	 */
	@Override
	public int getProgressType() {
		return TokenTypes.EVENT;
	}


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
	}

}
