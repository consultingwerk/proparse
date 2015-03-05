/*
IntegerIndex.java

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import java.util.HashMap;
import java.util.ArrayList;

/** Generic class for working with integer indexed data of type T.
 * ie: You want to be able to lookup the value at integer index n,
 * and you also want to be able to find the integer index of some data of type T.
 * Integer indexes start from zero, as in Java array indexes.
 */
public class IntegerIndex<T> {

	private HashMap<T, Integer> typeInt = new HashMap<T, Integer>();
	private HashMap<Integer, T> intType = new HashMap<Integer, T>();
	private int nextIndex = 0;




	/** Add the value if it's not already there.
	 * Returns the new or existing index.
	 */
	public int add(T val) {
		Integer ret = typeInt.get(val);
		if (ret!=null)
			return ret;
		ret = nextIndex++;
		typeInt.put(val, ret);
		intType.put(ret, val);
		return ret;
	}


	public void clear() {
		typeInt.clear();
		intType.clear();
		nextIndex = 0;
	}


	/** Returns -1 if not found. */
	public int getIndex(T val) {
		Integer ret = typeInt.get(val);
		if (ret==null)
			return -1;
		return ret;
	}


	/** Returns null if not found. */
	public T getValue(int index) {return intType.get(index);}


	/** Returns an array list in order from zero to number of indexes of all the values. */
	public ArrayList<T> getValues() {
		ArrayList<T> list = new ArrayList<T>();
		for (int i=0; i<nextIndex; ++i) {
			list.add(intType.get(i));
		}
		return list;
	}


	public boolean hasIndex(int index) {return intType.containsKey(index);}


	public boolean hasValue(T value) {return typeInt.containsKey(value);}


	public int size() {return nextIndex;}


}
