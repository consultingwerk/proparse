/** 10-Feb-07 by John Green
 * Copyright (C) 2007 Joanju Software. All rights reserved.
 */
package org.prorefactor.macrolevel;

import com.joanju.Xferable;
import org.prorefactor.refactor.RefactorException;

import java.io.Serializable;


/** Interface for a node in the macro event tree. */
public interface MacroEvent extends Serializable, Xferable {

	public MacroRef getParent();
	
	/** Returns int[3] of file/line/column. */
	public int [] getPosition() throws RefactorException;

}
