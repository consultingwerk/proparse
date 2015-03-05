/** 11-May-07 by John Green
 *
 * Copyright (C) 2007 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.Serializable;
import java.util.Comparator;

/** Needed for JDBM btree entries, just delegates to FileTarget.compareTo(). */
public class FileTargetComparator implements Serializable, Comparator {
    private final static long serialVersionUID = 1L;
	public int compare(Object o1, Object o2) {
		return ((FileTarget)o1).compareTo((FileTarget)o2);
	}
}
