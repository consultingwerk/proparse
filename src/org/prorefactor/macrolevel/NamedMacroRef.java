/* NamedMacroRef.java
 * Created on Nov 29, 2003
 * John Green
 *
 * Copyright (C) 2003-2007 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.macrolevel;

import com.joanju.DataXferStream;

import java.io.IOException;


/** A reference to a macro argument, i.e. {1} or {&name}.
 * Origin might be an include argument or an &DEFINE.
 */
public class NamedMacroRef extends MacroRef {

	/** Only to be used for persistence/serialization. */
	public NamedMacroRef() {}

	NamedMacroRef(int listingFileLine) {
		super(listingFileLine);
	}

	public MacroDef macroDef;

	private static final long serialVersionUID = -7576618416022482176L;
	

	
	@Override
	public int getFileIndex() { return parent.getFileIndex(); }


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		super.writeXferBytes(out);
		out.writeRef(macroDef);
	}
	/** Implement Xferable. */
	@Override
	public void writeXferSchema(DataXferStream out) throws IOException {
		super.writeXferSchema(out);
		out.schemaRef("macroDef");
	}


}
