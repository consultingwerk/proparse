/** 19-Dec-06 by John Green
 * 
 * Copyright (C) 2006 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.prorefactor.refactor.RefactorSession;

/** Log Files and IO for parse error logs, exception logs. */
public class LogFile extends BufferedWriter {
	
	public LogFile(File outFile) throws IOException {
		super(new FileWriter(outFile));
		this.outFile = outFile;
	}
	
	File outFile = null;
	
	
	/** Use the error log for parse errors or similar. */
	public static LogFile createErrorLog() throws IOException {
		return new LogFile(new File(RefactorSession.getProrefactorDirName() + "error.log"));
	}


	/** Use the exception log for writing full exception stack traces. */
	public static LogFile createExceptionLog() throws IOException {
		return new LogFile(new File(RefactorSession.getProrefactorDirName() + "exception.log"));
	}


	/** Use the output log for writing normal processing messages. */
	public static LogFile createOutputLog() throws IOException {
		return new LogFile(new File(RefactorSession.getProrefactorDirName() + "output.log"));
	}


}
