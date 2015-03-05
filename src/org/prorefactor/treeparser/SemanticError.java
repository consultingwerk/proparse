/* SemanticError.java - 
 */

package org.prorefactor.treeparser;

import org.prorefactor.core.JPNode;

/**
 * Represents a semantic error found in 4GL code while
 * analysing it.
 * @author pcd
 *
 */
public class SemanticError extends Error {
	
	JPNode errorLocation = null;	

	/**
	 * Create a record of an error detected in 4GL
	 * source code under analysis.
	 * @param message
	 */
	public SemanticError(String message, JPNode node) {
		super(message);
		errorLocation = node;
	}
	
	/**
	 * The column number on the source line, where
	 * the error was found.
	 * @return
	 */
	public int getColumn() {
		return errorLocation.getColumn();
	}

	/**
	 * The file name for the node where the error
	 * was found.
	 * @return
	 */
	public String getFilename() {
		return errorLocation.getFilename();
	}

	/**
	 * The line number for the node where the error
	 * was found.
	 * @return
	 */
	public int getLine() {
		return errorLocation.getLine();
	}

}
