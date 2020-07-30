/* FileStuff.java
 * Created on Jul 30, 2003
 * John Green
 *
 * Copyright (C) 2003 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.prorefactor.refactor.messages.Message;



/**
 * Various file and directory related utilities for the refactoring toolkit.
 */
public class FileStuff {


	static RefactorSession prsession = RefactorSession.getInstance();

	/** OS specific text file line seperator - '\n' on unix, "\r\n" on Windows */
	public static final String LINESEP = System.getProperty("line.separator");



	/** Count lines in a file */
	public static int countLines(File file) throws IOException {
		/* SCL-3087 : Replaced FileReader with InputStreamReader to use the current codepage */
		BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(System.getProperty("file.encoding"))));
		int numLines = 0;
		while (buff.readLine() != null) numLines++;
		buff.close();
		return numLines;
	} // countLines



	/** Find a file (or directory) on the propath
	 * @return null if not found
	 */
	public static File findFile(String filename) {
		File inFile = new File(filename);
		// "absolute" on windows means drive letter (i.e. c:)
		// We don't search the path if it starts with '.', '/', or '\'
		char c = filename.charAt(0);
		if (inFile.isAbsolute() || c=='.' || c=='/' || c=='\\' ) {
			if (inFile.exists()) return inFile;
			return null;
		}
		String propath = prsession.getProgressSettings().propath;
		String [] parts = propath.split(",");
		for (String part : parts) {
			File retFile = new File(part + File.separator + filename);
			if (retFile.exists()) return retFile;
		}
		return null;
	} // findFile
	
	
	
	/** Find a class file on Proparse's propath, from the "package.classname".
	 * @return null if not found.
	 */
	public static File findFileForClassName(String className) {
		return findFile(className.replace('.', '/') + ".cls");
	}



	/** Return the full path name.
	 * Just takes care of try/catch around getCanonicalPath().
	 */
	public static String fullpath(File file) {
		String ret;
		try {
			ret = file.getCanonicalPath();
		} catch (IOException e) {
			ret = file.toString();
		}
		return ret;
	} // fullpath


// TODO replace
//	/** Given a File object, find a matching file index number from
//	 * the current Proparse compile unit.
//	 */
//	public static int getFileIndex(File file) throws RefactorException, IOException {
//		ProparseLdr parser = ProparseLdr.getInstance();
//		assert parser.errorGetStatus()==0 : parser.errorGetText();
//		String canonical = file.getCanonicalPath();
//		int ret = -1;
//		for (int i = 0; parser.errorGetStatus() >= 0 ; i++) {
//			String currName = parser.getIndexFilename(i);
//			// Sometimes there's an empty "" entry in Proparse's filename set.
//			if (currName.length()<1) continue;
//			if ((new File(currName)).getCanonicalPath().equals(canonical)) {
//				ret = i;
//				break;
//			}
//		}
//		// getIndexFilename raises a warning when we've gone past the last index
//		parser.errorClear();
//		if (ret==-1) throw new RefactorException("Failed to find file in parse tree: " + file.toString());
//		return ret;
//	} // getFileIndex



	/** Return a new file name, with extra text before the existing extension.
	 * Ex: FileStuff.insertBeforeExtension("include.i", ".orig") returns "include.orig.i"
	 * @param origName The original filename
	 * @param insert The text to insert
	 * @return The modified filename
	 */
	public static String insertBeforeExtension(String origName, String insert) {
		int dotpos = origName.lastIndexOf('.');
		return origName.substring(0, dotpos) + insert + origName.substring(dotpos);
	} // insertBeforeExtension



	/**
	 * Prepare a target path, based on a specified target path
	 * name plus an original sourcefile name.
	 * @param outputDir The name of the top of the output directory structure.
	 * @param sourceFilename The name of the original sourcefile, used as basis for output filename.
	 * @return The filename that you should use for writing target source to.
	 * We build a target directory structure which resembles the
	 * source code *absolute* path. Why? Because we may have any
	 * number of absolute paths in our PROPATH:
	 * "/u1/my/application,/u2/third/party/library,etc"
	 * It is easiest and most consistent to just use absolute path
	 * for all entries, rather than try to use relative path for some
	 * and be forced to use absolute path for others.
	 * We even include drive letters, because it is possible for developers
	 * to use a PROPATH for version/variant management:
	 * "x:/myapp,y:/myapp"
	 * To avoid really long target directory names, use an output top
	 * directory that is near root, ex: "/temp/out".
	 * We replace ':' (Windows drive letter) with '_', so that we are
	 * always working with a valid name.
	 */
	public static String prepareTarget(String outputDir, String sourceFilename) {
		File sourceFile = new File(sourceFilename);
		String sourcePath = sourceFile.getAbsolutePath();
		File theTarget = new File(outputDir + "/" + sourcePath.replace(':', '_'));
		String returnName = theTarget.getAbsolutePath();
		theTarget = theTarget.getParentFile();
		theTarget.mkdirs();
		return returnName;
	} // prepareTarget



	/** Search a file for a given string.
	 * Search is done line by line; searches across line breaks not supported.
	 * @param file The file to search
	 * @param searchString The string to search for
	 * @param messageString The text to be placed in each Message object created
	 * @return ArrayList of org.prorefactor.refactor.message.Message objects.
	 * @throws IOException
	 */
	public static ArrayList searchFile(
			File file, String searchString, String messageString )
			throws IOException {
		/* SCL-3087 : Replaced FileReader with InputStreamReader to use the current codepage */
		BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(System.getProperty("file.encoding"))));
		ArrayList results = new ArrayList();
		String currline;
		String lowerSearch = searchString.toLowerCase();
		int lineNum = 0;
		while ( (currline = buff.readLine()) != null) {
			lineNum++;
			int lastPos = 0;
			lastPos = currline.toLowerCase().indexOf(lowerSearch, lastPos);
			while (lastPos != -1) {
				Message mess  = new Message();
				mess.column = lastPos + 1; // have count from 0, want count from 1
				mess.line = lineNum;
				mess.file = file;
				mess.message = messageString;
				results.add(mess);
				lastPos = currline.indexOf(searchString, lastPos + 1);
			}
		}
		buff.close();
		return results;
	} // searchFile


// TODO replace
//	/** Search all files in currently parsed Compile Unit for a given string.
//	 * Search is done line by line; searches across line breaks not supported.
//	 * @param searchString The string to search for
//	 * @param messageString The text to be placed in each Message object created
//	 * @return ArrayList of org.prorefactor.refactor.message.Message objects.
//	 * @throws IOException
//	 */
//	public static ArrayList searchFilesInCompileUnit(
//			String searchString, String messageString )
//			throws IOException {
//		ArrayList resultList = new ArrayList();
//		ProparseLdr parser = ProparseLdr.getInstance();
//		int fileIndex = 0;
//		String filename = parser.getIndexFilename(fileIndex);
//		while (parser.errorGetStatus() > -1) {
//			// There's often a blank filename in the list
//			if (filename.length() > 0)
//				resultList.addAll(searchFile(new File(filename), searchString, messageString));
//			fileIndex++;
//			filename = parser.getIndexFilename(fileIndex);
//		}
//		// Clear the warning that we've gone past the end of the fileIndex list.
//		parser.errorClear();
//		return resultList;
//	} // searchFilesInCompileUnit



} // class FileStuff

