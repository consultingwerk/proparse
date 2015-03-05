/**
 * @author John Green
 * 17-Mar-2006
 * www.joanju.com
 * 
 * Copyright (c) 2006 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.util;

import java.util.LinkedHashMap;
import java.util.Map;


/** Extends LinkedHashMap to provide a "recently used" cache of a specified size.
 * There's a good chance you will want to wrap this to be thread-safe:
 *   Map<K,V> m = Collections.synchronizedMap(new Cache<K,V>(...));
 */
public class Cache<K, V> extends LinkedHashMap<K, V> {

	static final long serialVersionUID = 5906218476888067680L;
	private final int capacity;

	public Cache(int capacity) {
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry eldest) {
		return size() > capacity;
	}

}
