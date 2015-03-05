/* ScriptSupport.groovy

Copyright (C) 2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.scripting;

import com.joanju.proparse.Environment
import org.codehaus.groovy.ant.FileScanner
import org.prorefactor.core.JPNode
import org.prorefactor.refactor.RefactorSession

/* ScriptSupport.groovy
 * Prepare configuration and files list for running a script.
 */
class ScriptSupport {

	public static final String LINESEP = System.getProperty("line.separator")

	ScriptSupport(String propertiesFileName, String logFileName) {
		this.logFileName = logFileName
		this.propertiesFileName = propertiesFileName
	}

	// ScriptSupport options
	def hasOutput = true
	def hasPrepend = true

	def userconfig = new Properties()
	
	File inputDir
	File logFile
	File outputDir
	FileScanner fileScanner
	Logger logger
	String canonicalPropath = ''
	String convertCanonicalDir = ''
	String logFileName
	String originalPropath = ''
	String outputDirName
	String propertiesFileName

	def prsession = RefactorSession.instance

	final String LINESEP = System.getProperty('line.separator')



	boolean initialize() {
		if (! loadScriptConfig())
			return false
		buildFileScanner()

		logFile = new File(logFileName)
		logFile << LINESEP + LINESEP
		logFile << new Date()
		logFile << LINESEP
		logger = new Logger(logFile)

		return true
	}


	void buildFileScanner() {
		// See http://ant.apache.org/manual/CoreTypes/fileset.html
		// See http://groovy.codehaus.org/Using+Ant+from+Groovy
		fileScanner = new AntBuilder().fileScanner {
			def extns = userconfig.getProperty('extensions')
			if (!extns)
				extns = 'p w cls'
			fileset(dir:convertCanonicalDir) {
				for (extn in extns.split()) {
					// '**/*.p' etc.
					include(name:'**/*.' + extn)
				}
			}
		}
	}
	

	boolean close() {
		if (originalPropath)
			setPropath(originalPropath)
	}


	boolean checkInputDir(String dirName) {
		if (!dirName) {
			println 'Input directory has not been configured in ${propertiesFileName}'
			return false
		}
		inputDir = new File(dirName)
		if (! inputDir.exists()) {
			println 'Input directory does not exist'
			return false
		}
		if (! inputDir.isDirectory()) {
			println 'Input directory must be a directory, not a file'
			return false
		}
		convertCanonicalDir = inputDir.canonicalPath
		if (! convertCanonicalDir.endsWith(File.separator))
			convertCanonicalDir += File.separator
		return true
	}


	boolean checkOutputDir(String dirName) {
		if (!dirName) {
			println 'Output directory has not been configured in ${propertiesFileName}'
			return false
		}
		outputDirName = dirName
		if (! dirName.endsWith(File.separator))
			outputDirName += File.separator
		outputDir = new File(outputDirName)
		if (outputDir.exists() && !outputDir.isDirectory()) {
			println 'Could not create output directory - found file with that name.'
			return false
		}
		if (! outputDir.exists()) {
			if (! outputDir.mkdirs()) {
				println 'Failed to create output directory'
				return false
			}
		}
		return true
	}


	String getUserConfig(String propertyName) {
		return userconfig.get(propertyName)
	}


	/** Log that a node was not converted, with a given message/reason. */
	void logNoConvert(JPNode node, String message) {
		logger.linebreak()
		logger.logLine(node.filename + ' line ' + node.line)
		logger.logLine('    ' + message)
	}


	def loadProject(String projectName) {
		try {
			println "Loading $projectName settings"
			prsession.loadProject(projectName)
		} catch(Exception e) {
			println 'Failed to load project.'
			println e
			return false
		}
		return true
	}


	boolean loadScriptConfig() {
		def propsFile = new File(propertiesFileName)
		if (! propsFile.exists()) {
			println "Could not find $propertiesFileName file."
			return false
		}
		userconfig.load(propsFile.newInputStream())
		if (!loadProject(userconfig.getProperty('loadProject')))
			return false
		if (!checkInputDir(userconfig.getProperty('inputDir')))
			return false
		if (hasOutput) {
			if (!checkOutputDir(userconfig.getProperty('outputDir')))
				return false
			if (hasPrepend) {
				def prepend = userconfig.getProperty('propathPrepend')
				if (! prepend) {
					println 'Propath prepend has not been configured in ${propertiesFileName}.'
					return false
				}
				originalPropath = prsession.progressSettings.propath
				setPropath(prepend + ',' + originalPropath)
			}
		}
		return true
	}


	/** Return a double-quoted string, any existing double-quotes are escaped. */
	String quoter(String s) {
		return '"' + s.replace('"', '~"') + '"'
	}


	String relativePath(File file) {
		return file.path.substring(convertCanonicalDir.length())
	}


	void setPropath(String newPropath) {
		// Still some duplication in environment settings due to old/new proparse.
		prsession.progressSettings.propath = newPropath
		Environment.instance().configSet("propath", newPropath);
	}


}
