/** 19-Dec-06 by John Green
 * 
 * Copyright (C) 2006 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.io;

import java.io.IOException;
import java.io.PrintStream;

import org.prorefactor.core.Util;


/** Provides log and status writing.
 * From LogFile, writes to statusLog, errorLog, and exceptionLog.
 * Optionally logs output and errors to a PrintStream.
 */
public class LogWriter {
	
	
	/** The printStream is optional (use null if you don't want one). */
	public LogWriter(PrintStream printStream) throws IOException {
		this.printStream = printStream;
	}
	
	LogFile errorLog = LogFile.createErrorLog();
	LogFile exceptionLog = LogFile.createExceptionLog();
	LogFile statusLog = LogFile.createOutputLog();
	PrintStream printStream = null;
	
	
	/** Closes the log files, but not the print stream. */
	public void close() throws IOException {
		errorLog.close();
		exceptionLog.close();
		statusLog.close();
	}
	
	
	/** Log the process status with newline. */
	public void log(String logText) throws IOException {
		if (printStream!=null) printStream.println(logText);
		statusLog.write(logText);
		statusLog.newLine();
	}
	
	
	/** Log an error.
	 * Writes the optional errorLogPrefix to a line in the error log
	 * prior to writing the error text to the printStream, the outputLog, and the errorLog.
	 */
	public void logError(String errorLogPrefix, String errorText) throws IOException {
		if (errorLogPrefix!=null) {
			errorLog.write(errorLogPrefix);
			errorLog.newLine();
		}
		log(errorText);
		errorLog.write(errorText);
		errorLog.newLine();
	}
	
	
	/** Log an exception.
	 * Writes the optional logPrefix to a line in the error log and exception log
	 * prior to writing the exception message to the printStream, the outputLog, and the errorLog,
	 * and the exception stack trace to the exceptionLog.
	 */
	public void logException(String logPrefix, Exception exception) throws IOException {
		if (logPrefix!=null) {
			exceptionLog.write(logPrefix);
			exceptionLog.newLine();
		}
		String message = exception.getMessage();
		if (message==null) message = exception.toString();
		logError(logPrefix, message);
		exceptionLog.write(Util.getExceptionText(exception));
		exceptionLog.newLine();
	}
	
	
}
