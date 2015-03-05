/* FieldLookupResult.java
 * Created on May 6, 2004
 * John Green
 *
 * Copyright (C) 2004 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparser;

import org.prorefactor.widgettypes.FieldLevelWidgetI;


/**
 * For field lookups, we need to be able to pass back
 * the BufferScope object as well as the Field object.
 */
public class FieldLookupResult {
	public boolean isAbbreviated = false;
	public boolean isUnqualified = false;
	public BufferScope bufferScope = null;
	public Variable variable = null;
	public FieldLevelWidgetI fieldLevelWidget = null;
	public FieldBuffer field = null;
	public Event event = null;
}
