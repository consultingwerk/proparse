/* JPNav.java
 * Created on Jun 2, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core;




/** This class contains static methods for navigating JPNode objects in an AST.
 * Methods here are specific to Proparse's tree structure.
 * For general node navigation (firstChild, etc.) see JPNode.
 * <p>
 * The new subclass of JPNode, FieldRefNode, allows
 * this class and its members to go away entirely.
 * @deprecated
 */
public class JPNav {

	/** Find the ID node for a Field_ref node
	 * @deprecated
	 * @see org.prorefactor.nodetypes.FieldRefNode#getIdNode()
	 */
	public static JPNode findFieldRefIdNode(JPNode refNode) {
		JPNode idNode = refNode.findDirectChild(TokenTypes.ID);
		assert idNode != null;
		return idNode;
	}

}
