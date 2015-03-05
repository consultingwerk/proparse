/* TestParse.groovy
November 2008 by John Green

Copyright (C) 2008-2009 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package proparse

import java.io.File;
import com.joanju.scripting.Logger
import com.joanju.scripting.ScriptSupport
import org.apache.tools.ant.util.FileUtils;
import org.prorefactor.refactor.RefactorSession
import org.prorefactor.treeparser.ParseUnit


/** Runnable class for testing that the Proparse works OK with your project.
 * Requires 'testparse.properties' in your working directory.
 * Logs to 'testparse.log' in your working directory.
 */
class TestParse implements Runnable {

	boolean SHOW_EXCEPTION_STACK_TRACE = false

	Logger logger
	RefactorSession prsession = RefactorSession.instance
	ScriptSupport support
	String relativePath


	public static void main(String [] args) {
		new TestParse().run()
	}


	void run() {
		// Disable parser output.
		prsession.setProjectBinariesEnabledOff();

		support = new ScriptSupport('testparse.properties', 'testparse.log')
		support.hasPrepend = false
		support.hasOutput = false
		try {
			if (! support.initialize())
				return
			run2_fileList()
		} finally {
			support.close()
		}
	}


	void run2_fileList() {
		logger = support.logger
		// Loop all compile units from the selection.
		for (File file in support.fileScanner) {
			relativePath = support.relativePath(file)
			logger.log(relativePath + ' ')
			def parseUnit = new ParseUnit(file)
			try {
				parseUnit.loadOrBuildPUB()
			} catch (Exception e) {
				// Show and log parse errors.
				logger.logLine(e.message)
				if (SHOW_EXCEPTION_STACK_TRACE)
					e.printStackTrace()
				continue
			}
			logger.newline()
		}
	}

}
