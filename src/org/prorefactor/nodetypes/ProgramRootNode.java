/** 8-May-2006 by John Green
 *
 * Doesn't currently (July 2008) do anything special - 
 * this is its own subtype only for historical reasons.
 *
 * Copyright (C) 2006 Joanju Software. All rights reserved.
 */
package org.prorefactor.nodetypes;

import com.joanju.proparse.ProToken;
import com.joanju.DataXferStream;

import java.io.IOException;


public class ProgramRootNode extends BlockNode {

	public ProgramRootNode() {
		super();
	}
	public ProgramRootNode(int file, int line, int column) {
		super(file, line, column);
	}

	public ProgramRootNode(ProToken t) {super(t);}

	private static final long serialVersionUID = 7160983003100786995L;


	
	/** Every JPNode subtype has its own index. Used for persistent storage. */
	@Override
	public int getSubtypeIndex() { return 6; }


	/** Implement Xferable. */
	@Override
	public void writeXferBytes(DataXferStream out) throws IOException {
		// Link this Program_root to a copy of the filename array, so
		// that it goes out in the data dump.
		setLink(FILE_NAME_ARRAY, getFilenames());
		super.writeXferBytes(out);
	}


}
