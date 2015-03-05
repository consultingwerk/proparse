/**
 * Util.java
 * @author John Green
 * 27-Oct-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.core;


import java.io.*;
import java.util.*;


/**
 * General purpose utilities
 */
public class Util {

	/** OS specific text file line seperator - '\n' on unix, "\r\n" on Windows */
	public static final String LINESEP = System.getProperty("line.separator");


	/** Copy all files from one directory to another.
	 * Target directory does not have to exist - this will create it.
	 * Is not recursive (does not copy subdirectories).
	 */
	static public void copyAllFiles(File sourceDir, File targetDir) throws IOException {
		if (! sourceDir.exists() || ! sourceDir.isDirectory())
			throw new IOException(sourceDir.toString() + " does not exist or is not a directory.");
		if (targetDir.exists() && ! targetDir.isDirectory())
			throw new IOException(targetDir.toString() + " is not a directory.");
		if (! targetDir.exists()) {
			if (! targetDir.mkdirs())
				throw new IOException("Could not create directory " + targetDir.toString());
		}
		File [] files = sourceDir.listFiles();
		for (int i=0; i < files.length; i++) {
			if (! files[i].isFile()) continue;
			fileCopy(
				files[i].getAbsolutePath()
				, targetDir.getAbsolutePath() + "/" + files[i].getName()
				);
		}
	} // copyAllFiles



	/** Append one file to another.
	 * @param target The file that gets appended to.
	 * @param source The file to append.
	 */
	static public void fileAppend(String target, String source) throws IOException {
		fileThing(source, target, true);
	}



	/** Append a string to a file.
	 * @param target The file that gets appended to.
	 * @param source The string to append.
	 */
	static public void fileAppendString(String target, String source) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(target, true));
		out.write(source);
		out.close();
	}



	/** Copy a file.
	 * @param from filename to copy from
	 * @param to filename to copy to
	 */
	static public void fileCopy(String from, String to) throws IOException {
		fileThing(from, to, false);
	}



	static private void fileThing(String from, String to, boolean append) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(from));
		BufferedWriter out = new BufferedWriter(new FileWriter(to, append));
		int c;
		while ((c = in.read()) != -1) out.write(c);
		in.close();
		out.close();
	} // fileCopy()



	public static String getExceptionText(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print(e.toString() + "\n" + e.getMessage() + "\n");
		e.printStackTrace(pw);
		return sw.toString();
	} // getExceptionText



	/** Assuming an x,y range,
	 * this function returns whether an input x and y are within
	 * the specified range of x,y begin and x,y end.
	 * We use this primarily for checking if a line/column are within
	 * the specified range. The "range" may be open ended, see parameter
	 * descriptions.
	 * @param x The x value to check that it is within range
	 * @param y The y value to check that it is within range
	 * @param begin An array of 2 integers to specify the beginning of the x,y
	 * range. May be null to indicate that the beginning is open ended.
	 * @param end An array of 2 integers to specify the ending of the x,y
	 * range. May be null to indicate that the beginning is open ended.
	 * @return
	 */
	public static boolean isInRange(int x, int y, int [] begin, int [] end) {
		if (begin!=null) {
			if (x<begin[0] || (x==begin[0] && y<begin[1])) return false;
		}
		if (end!=null) {
			if (x>end[0] || (x==end[0] && y>end[1])) return false;
		}
		return true;
	} // isInRange



	/** Given a mainlist of sublists, return a new newlist of
	 * newsublists, such that:
	 *   - the number of newsublists is the number of "cycles"
	 *     ("permutations"?)
	 *   - each newsublist has the same number of objects in it
	 *     as the original mainlist had sublists. There is one
	 *     object from each of the original sublists.
	 * (i.e. If the mainlist contains 3 sublists, and the number
	 * of items in the sublists is: 2, 1, 4, then the number of 
	 * cycles is: 2 * 1 * 4 = 8. The newlist will contain 8 newsublists
	 * with three objects each.)
	 * (Might be easier to think of the return list as a matrix of m
	 * by n, where m (rows) is the number of original sublists, and n
	 * (columns) is the number of cycles/permutations.)
	 * I'm sure there's proper terminology and algorithms for this, but
	 * I don't know what they are.
	 */
	public static List permsList(List topList) {
		int numSublists = topList.size();
		if (numSublists==0) return topList;
		List retList = new ArrayList();
		List firstElements = (List) topList.get(0);
		List tailPerms = permsList(topList.subList(1,numSublists));
		for (Iterator it = firstElements.iterator(); it.hasNext();) {
			Object item = it.next();
			if (tailPerms.size() > 0) {
				for (Iterator it2 = tailPerms.iterator(); it2.hasNext();) {
					List subPermList = (List) it2.next();
					List addList = new ArrayList();
					addList.add(item);
					addList.addAll(subPermList);
					retList.add(addList);
				}
			} else {
				List addList = new ArrayList();
				addList.add(item);
				retList.add(addList);
			}
		}
		return retList;
	} // permsList



	/** Read the contents of a file into a StringBuffer */
	public static StringBuffer readFile(File from) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(from));
		StringWriter sWriter = new StringWriter();
		BufferedWriter out = new BufferedWriter(sWriter);
		int c;
		while ((c = in.read()) != -1) out.write(c);
		in.close();
		out.close();
		return sWriter.getBuffer();
	}



	/**	
	 * Split a string into pieces based on the given divider character.
	 * Used for Java 1.3 compatability - String.split() is new as of 1.4.
	 * @deprecated Use Java 1.4 String.split() instead.	
	 * @param s the string to split	
	 * @param divider the character on which to split.  Occurrences of	
	 * this character are not included in the output	
	 * @return An ArrayList of the pieces.
	 */	
	public static ArrayList split(String s, char divider) {
		ArrayList retList = new ArrayList();
		int last = 0;
		int sLength = s.length();
		int i = 0;
		for (; i < sLength; ++i) {
			if (s.charAt(i) == divider) {
				retList.add(s.substring(last,i));
				last = i+1;
			}
		}
		retList.add(s.substring(last,i));
		return retList;
	}



	/** Delete all files and directories within a directory -
	 * causes the specified directory to be empty.
	 * @param dir A File object for the directory to be emptied.
	 * @param recursive Recursively delete subdirectories?
	 * @return false if any files or directories could not be deleted.
	 * @throws IOException
	 */
	public static boolean wipeDirectory(File dir, boolean recursive) throws IOException {
		boolean ret = true;
		File [] files = dir.listFiles();
		for (int i=0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (recursive) {
					if (! wipeDirectory(files[i], recursive)) ret = false;
					if (! files[i].delete()) ret = false;
				}
			} else {
				if (! files[i].delete()) ret = false;
			}
		}
		return ret;
	}



} // class Util
