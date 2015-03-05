/** 3 May 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.treeparser;


/**
 * A Symbol defined with DEFINE <widget-type> or any of the other various
 * syntaxes which implicitly define a widget.
 * This includes FRAMEs, WINDOWs, MENUs, etc.
 */
public abstract class Widget extends Symbol implements WidgetI {

	/** Only to be used for persistence/serialization. */
	protected Widget() {}

	public Widget(String name, SymbolScope scope) {
		super(scope);
		setName(name);
	}

	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.WidgetI#fullName()
	 */
	public String fullName() { return getName(); }

}
