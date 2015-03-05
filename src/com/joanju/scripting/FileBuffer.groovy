/* FileBuffer.groovy

Copyright (C) 2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.scripting;


import org.prorefactor.core.JPNode


/** Work with a file's lines of text.
 * <p>
 * This class expects count-from-one line and column numbers,
 * like those that are used in Proparse.
 * </p><p>
 * The idea here is that the lines are loaded into an ArrayList,
 * and any changes should *not* affect the mapping of a line's
 * original line number to the row in the ArrayList.
 * </p><p>
 * When the file is written out, each entry in the ArrayList is
 * written, plus the OS-specific line seperator. If the entry
 * from the ArrayList is null, then no line seperator is written
 * out (i.e. that's how to delete a line).
 * </p><p>
 * The trick for inserting new lines without affecting the line
 * number mapping is to insert new text into an existing ArrayList
 * entry ... with your own line separators.
 * </p>
 */
class FileBuffer {

	// NOTES: If adding methods to this, remember to convert Proparse's
	// lines and columns, which start at 1, to Java array index from zero.

	FileBuffer(File file) {
		this.file = file
		lines = (ArrayList<String>) file.readLines()
	}

	ArrayList<String> lines
	File file

	final String LINESEP = System.getProperty('line.separator')



	/** Append text to a line.
	 * Use Proparse style count-from-one line number.
	 */
	void append(int line1, String s) {
		int line = line1 - 1
		def text = lines[line]
		if (text)
			lines[line] = text + s
		else
			lines[line] = s
	}


	/** Fetch a line, as counted from one (ex: node.line). */
	String getLine(int line) {return lines[line-1];}


	/** Test that a node's text can be matched to the source file text.
	 * Returns true if the input node is null.
	 */
	boolean match(JPNode node) {
		if (!node)
			return true
		// Proparse counts from 1, but we need line and column indexes counting from zero.
		def line = lines[node.line - 1]
		def column = node.column - 1
		def text = node.text
		return text.equals(line.substring(column, column + text.length()))
	}


	/** Call match(node) for all nodes in a list. */
	boolean match(List<JPNode> nodes) {
		for (JPNode node in nodes) {
			if (! match(node))
				return false
		}
		return true
	}


	/** Remove a text segment from the text.
	 * Input [startline, startcolumn, endline, endcolumn],
	 * count lines and columns from one like Proparse.
	 * Columns are inclusive; chars at startcol and endcol are removed.
	 */
	void remove(Segment seg1) {
		// Work with a Segment indexed from zero.
		def seg = zeroIndexedSegment(seg1)
		if (seg.endline == seg.startline) {
			String text = lines[seg.startline]
			lines[seg.startline] = (
				text.substring(0, seg.startcol)
				+ text.substring(seg.endcol + 1)
				)
			return
		}
		if (lines[seg.startline] != null) {
			lines[seg.startline] =
				lines[seg.startline].substring(0, seg.startcol)
		}
		if ((seg.endline - seg.startline) > 1) {
			for (line in seg.startline + 1 .. seg.endline - 1) {
				lines[line] = null
			}
		}
		if (lines[seg.endline] != null) {
			lines[seg.endline] =
				lines[seg.endline].substring(seg.endcol + 1)
		}
	}


	/** Replace a node's text with new text.
	 * Note that column positions any further along the line of
	 * text will no longer be valid after this operation.
	 * You should do a match() before doing a replace().
	 */
	void replace(JPNode node, String text) {
		def line = node.line - 1
		def column = node.column - 1
		def oldText = lines[line]
		def newText = (
			oldText.substring(0, column)
			+ text
			+ oldText.substring(column + node.text.length())
			)
		lines[line] = newText
	}


	/** Set a line's text, with line number as counted from one (ex: node.line). */
	void setLine(int line, String text) {lines[line-1] = text;}


	/** Get a text segment.
	 * Input [startline, startcolumn, endline, endcolumn]
	 * where lines and columns are indexed from 1 (like Proparse's columns).
	 * Return string includes the characters at startcol and endcol.
	 */
	String textSegment(Segment seg1) {
		// Work with a Segment indexed from zero.
		def seg = zeroIndexedSegment(seg1)
		if (seg.endline == seg.startline)
			return lines[seg.startline].substring(seg.startcol, seg.endcol + 1)
		def sb = new StringBuilder()
		if (lines[seg.startline] != null) {
			sb << lines[seg.startline].substring(seg.startcol)
			sb << LINESEP
		}
		if ((seg.endline - seg.startline) > 1) {
			for (line in seg.startline + 1 .. seg.endline - 1) {
				if (lines[line] != null) {
					sb << lines[line]
					sb << LINESEP
				}
			}
		}
		if (lines[seg.endline] != null) {
			sb << lines[seg.endline].substring(0, seg.endcol + 1)
			sb << LINESEP
		}
		return sb.toString()
	}


	/** Write the buffer to the specified File.
	 * Creates directories if needed, for creating the file.
	 * Overwrites any existing file.
	 */
	void write(File outFile) {
		outFile.parentFile.mkdirs()
		// Empty the file, in case we're overwriting a previous run.
		outFile.write('')
		for (line in lines) {
			if (line!=null) {
				outFile << line
				outFile << LINESEP
			}
		}
	}


	/** Zero a node's text from the source file text.
	 * Replaces existing characters with null characters.
	 * It is expected that the null characters will be removed
	 * once positional (string index / column) work is complete.
	 * Does nothing if the input node is null.
	 */
	void zero(JPNode node) {
		if (!node)
			return
		int start = node.column - 1
		int len = node.text.length()
		int end = start + len - 1
		def fill = ZEROSTRING * len
		def sb = new StringBuilder(lines.get(node.line - 1))
		sb[start .. end] = fill
		lines[node.line - 1] = sb.toString()
	}


	/** Call zero(node) for all nodes in a list. */
	void zero(List<JPNode> nodes) {
		for (JPNode node in nodes) {
			zero(node)
		}
	}


	/** Get a zero-indexed segment, from a count-from-one line/column segment. */
	Segment zeroIndexedSegment(Segment seg1) {
		return new Segment(
				seg1.startline-1, seg1.startcol-1,
				seg1.endline-1, seg1.endcol-1
				)
	}


}
