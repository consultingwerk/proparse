/*
TokenTypesI.java

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package org.prorefactor.core;


/** Common interface for fetching token names from token numbers.
 * Necessary because it's possible to have extensions to a grammar
 * which do not use the 'base' token types.
 */
public interface TokenTypesI {

	public String getName(int type);

}
