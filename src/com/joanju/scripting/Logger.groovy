/* Logger.groovy

Write output both to a file and to stdout.

Copyright (C) 2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.scripting;

class Logger {

	Logger(File logFile) {
		this.logFile = logFile
	}

	boolean atNewline = true
	File logFile
	final String LINESEP = System.getProperty('line.separator')


	def log(Object o) {
		print o
		logFile << o
		atNewline = false
	}

	def logLine(Object o) {
		log(o)
		log(LINESEP)
		atNewline = true
	}

	def linebreak() {
		if (! atNewline) {
			newline()
			atNewline = true
		}
	}

	def newline() {
		log(LINESEP)
		atNewline = true
	}

}
