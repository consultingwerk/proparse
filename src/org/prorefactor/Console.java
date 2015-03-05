/** 18-Dec-06 by John Green
 * 
 * Copyright (C) 2006 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.prorefactor.io.LogWriter;
import org.prorefactor.refactor.PUB;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.reports.HowUsedConsole;
import org.prorefactor.reports.WhereUsedConsole;
import org.prorefactor.treeparser.ParseUnit;



/** Provides command-line access to ProRefactor configuration
 * and one or two features.
 */
public class Console {
	
	protected BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	protected RefactorSession session = RefactorSession.getInstance();

	
	public static void main(String[] args) {
		new Console().uiLoop();
	}
	
	
	public void uiLoop() {
		try {
			String choice = "";
			while (! choice.equalsIgnoreCase("q")) {
				out.println();
				showMenu();
				choice = menuSelect();
				if (choice==null) continue;
				processSelection(choice);
				out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	protected void processSelection(String choice) throws Exception {
		if (choice.equalsIgnoreCase("h")) howUsedReport();
		else if (choice.equalsIgnoreCase("l")) loadProjectSettings();
		else if (choice.equalsIgnoreCase("p")) parseDirectory();
		else if (choice.equalsIgnoreCase("w")) whereUsedReport();
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection buildCUListFromFile(String listFileName) throws IOException {
		out.println("Reading files list...");
		BufferedReader cuList = new BufferedReader(new FileReader(new File(listFileName)));
		ArrayList ret = new ArrayList();
		for (String cuName = ""; cuName!=null; cuName = cuList.readLine()) {
			cuName = cuName.trim();
			if (cuName.length()==0) continue;
			File cuFile = new File(cuName);
			if (cuFile.exists()==false) throw new IOException("File does not exist: " + cuName);
			ret.add(cuFile);
		}
		return ret;
	}


	protected void checkProjectIsLoaded() throws Exception {
		String projName = session.getProjectName();
		while (projName==null || projName.length()==0) {
			out.println("No project loaded.");
			loadProjectSettings();
			projName = session.getProjectName();
		}
	}
	
	
	public Collection findCompileUnits(File dir) {
		String [] extensions = {"p","w","cls"};
		out.println("Finding files...");
		Collection files = FileUtils.listFiles(dir, extensions, true);
		return files;
	}


	private void howUsedReport() throws Exception {
		checkProjectIsLoaded();
		new HowUsedConsole().go();
	}
	
	
	protected void loadProjectSettings() throws Exception {
		out.print("Project name to load settings for: ");
		String projectName = reader.readLine();
		if (projectName==null || projectName.length()==0) return;
		session.loadProject(projectName);
	}
	
	
	protected String menuSelect() throws IOException {
		out.print("Enter selection: ");
		String choice = reader.readLine();
		return choice;
	}
	
	
	private void parseDirectory() throws Exception {
		checkProjectIsLoaded();
		out.println("Enter the directory to parse.");
		File dir = promptDirectory();
		if (dir==null) return;
		LogWriter log = new LogWriter(out);
		try {
			Collection files = findCompileUnits(dir);
			int currFileNum = 0;
			String totalFiles = "/" + files.size() + " ";
			for (Iterator it = files.iterator(); it.hasNext(); ) {
				File cuFile = (File) it.next();
				currFileNum++;
				String logMsg = Integer.toString(currFileNum) + totalFiles + cuFile.toString();
				ParseUnit pu = new ParseUnit(cuFile);
				PUB pub = pu.getPUB();
				pub.loadTo(PUB.HEADER);
				if (pub.isCurrent()) {
					log.log(logMsg + " PUB is current");
					continue;
				}
				log.log(logMsg + " parse");
				try {
					pu.treeParser01();
				} catch (RefactorException e) {
					log.logException(cuFile.toString(), e);
				}
			}
		} finally {
			log.close();
		}
	}


	/** Prompt for a directory.
	 * @return null if no valid directory selected.
	 */
	protected File promptDirectory() throws IOException {
		out.print("Directory: ");
		String dirName = reader.readLine();
		if (dirName==null || dirName.length()==0) return null;
		File dir = new File(dirName);
		if (dir.isDirectory()==false) {
			out.println(dirName + " is not a directory.");
			return null;
		}
		return dir;
	}
	
	
	protected void showMenu() {
		out.println(
			  "h) How-used report \n"
			+ "l) Load Settings for a project \n"
			+ "p) Parse a directory \n"
			+ "q) Quit \n"
			+ "w) Where used report \n"
			);
	}
	
	
	private void whereUsedReport() throws Exception {
		checkProjectIsLoaded();
		new WhereUsedConsole().go();
	}
	
	
}
