/* Created on May 3, 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.widgettypes;

import org.prorefactor.core.TokenTypes;
import org.prorefactor.treeparser.Symbol;
import org.prorefactor.treeparser.SymbolScope;
import org.prorefactor.treeparser.Widget;


public class Image extends Widget implements FieldLevelWidgetI {

	/** Only to be used for persistence/serialization. */
	public Image() {}

	public Image(String name, SymbolScope scope) { super(name, scope); }

	@Override
	public Symbol copyBare(SymbolScope scope) {
		return new Image(getName(), scope);
	}

	/** Returns TokenTypes.IMAGE. */
	public int getProgressType() { return TokenTypes.IMAGE; }

}
