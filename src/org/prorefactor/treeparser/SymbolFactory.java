/* Created on May 3, 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;

import org.prorefactor.core.TokenTypes;
import org.prorefactor.widgettypes.*;


/** Create a Symbol of the appropriate subclass. */
public final class SymbolFactory {

	public static Symbol create(int symbolType, String name, SymbolScope scope) {
		switch (symbolType) {
		case TokenTypes.DATASET:
			return new Dataset(name, scope);
		case TokenTypes.DATASOURCE:
			return new Datasource(name, scope);
		case TokenTypes.QUERY:
			return new Query(name, scope);
		case TokenTypes.STREAM:
			return new Stream(name, scope);
		// Widgets
		case TokenTypes.BROWSE:
			return new Browse(name, scope);
		case TokenTypes.BUTTON:
			return new Button(name, scope);
		case TokenTypes.FRAME:
			return new Frame(name, scope);
		case TokenTypes.IMAGE:
			return new Image(name, scope);
		case TokenTypes.MENU:
			return new Menu(name, scope);
		case TokenTypes.MENUITEM:
			return new MenuItem(name, scope);
		case TokenTypes.RECTANGLE:
			return new Rectangle(name, scope);
		case TokenTypes.SUBMENU:
			return new Submenu(name, scope);
		default:
			assert false : "Unexpected values for SymbolFactory" + " " + symbolType + " " + name;
			return null;
		}
	}

}
