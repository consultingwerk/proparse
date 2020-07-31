/*
Iwdiff.java

Ignore whitespace diff
Used for unit and regression testing.
Ignores whitespace, and PROGRESS-style comments.
Only displays line numbers of first non-whitespace difference.

This works OK for the source that we use for unit and regression
testing, but it will not necessarily work for every Progress
application out there. There are some known oddities of COMPILE..PREPROCESS
that this just won't handle.

The first input file must be the COMPILE..PREPROCESS output file,
the second input file must be the Proparse output file.

The preprocessor makes changes to the source if there are any
escape sequences. Our parser output does not. We have to watch
for these differences.

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import java.util.LinkedList;
import java.io.*;
import java.nio.charset.Charset;


public class Iwdiff {

	// Unlike comments, doingString is true/false for both files at any given time.
	boolean doingString = false;


	class Source {

		boolean redo = false;
		boolean returnSpace = false;
		int pos = -1; // character position in the input
		char c = 'x'; // is always equal to la(1)
		int commentLevel = 0;
		int line = 1;
		LinkedList<Character> lookahead = new LinkedList<Character>();
		BufferedReader in;

		Source(BufferedReader in) {
			this.in = in;
		}

		void consume() {
			if (lookahead.size()!=0) {
				if (lookahead.getFirst() == '\n')
					++line;
				lookahead.removeFirst();
			}
		}

		char la(int i) throws IOException {
			while (i > lookahead.size()) {
				lookahead.addLast(get());
			}
			return lookahead.get(i - 1);
		}

		char get() throws IOException {
			int g = in.read();
			if (g==-1)
				return 0;
			return (char)g;
		}

		char getNext () throws IOException {
			if (redo) {
				redo = false;
				return c;
			}
			returnSpace = false;
			while (true) {
				consume();
				c = la(1);
				if (c==0)
					return c;
				if (doingString) {
					return c;
				}
				else if (c=='/' && la(2)=='*') {
					// We aren't in a string - watch for and consume comments.
					consume();
					c = la(1);
					++commentLevel;
					returnSpace = true;
				}
				else if (c=='*' && commentLevel!=0 && la(2)=='/') {
					consume();
					c = la(1);
					--commentLevel;
				}
				else if (commentLevel==0 && Character.isWhitespace(c)) {
					returnSpace = true;
				}
				else if (commentLevel==0)
					break;
			}
			if (returnSpace) {
				lookahead.addFirst(' ');
				c = ' ';
			}
			return c;
		}

	}



	/** Convenience method for comparing two files. */
	public String diff(File src1, File src2) throws IOException {
		/* SCL-3087: change FileReaders to InputStreamReaders to use the current codepage */
		return diff(new BufferedReader(new InputStreamReader(new FileInputStream(src1), Charset.forName(System.getProperty("file.encoding")))),
				    new BufferedReader(new InputStreamReader(new FileInputStream(src2), Charset.forName(System.getProperty("file.encoding")))));
	}

	/** Convenience method for comparing an 'expect' file's contents to a result string. */
	public String diff(File src1, String text) throws IOException {
		/* SCL-3087: change FileReader to InputStreamReader to use the current codepage */
		return diff(new BufferedReader(new InputStreamReader(new FileInputStream(src1), Charset.forName(System.getProperty("file.encoding")))),
				    new BufferedReader(new StringReader(text)) );
	}

	public String diff(BufferedReader src1, BufferedReader src2) throws IOException {

		char stringType = ' ';

		Source f1 = new Source(src1);
		Source f2 = new Source(src2);

		while(f1.c != 0) {

			f1.getNext();
			f2.getNext();


			if (f1.c == f2.c) {

				// Watch for tilde escape sequences
				if (f1.c=='~' && f2.c=='~') {
					// Get the next character of the escape sequence.
					f1.getNext();
					f2.getNext();
					if (	Character.isDigit(f2.c)
						&&	Character.isDigit(f2.la(2))
						&&	Character.isDigit(f2.la(3))) {
						// Three digits of an octal escape
						f2.getNext();
						f2.getNext();  // now at third digit
					}
					// Inside a string, Progress converts "~n" to "~\r\n".
					// Proparse leaves it as "~n".
					if (doingString && f2.c=='n') {
						if (f1.c=='\r')
							f1.getNext();	// f1 now at '\n', f2 now at 'n'.
					}
					// We don't bother comparing this character in the escape sequence.
					continue;
				}

				// Watch for "." handling within table/field names. See bug#016.
				if (f1.c=='.' && f2.c=='.') {
					f1.getNext();
					f2.getNext();
					if (f1.c==' ' && f2.c!=' ') {
						f2.redo = true;
						continue;
					} else {
						f1.redo = true;
						f2.redo = true;
						continue;
					}
				}

				// Start of new string
				if ( (f1.c=='"' || f1.c=='\'') && !doingString ) {
					doingString = true;
					stringType = f1.c;
					continue;
				}

				// End of string
				if (doingString && f1.c==stringType) {
					if (f2.la(2)==stringType) {
						// The preprocessor removes the escaping quote
						// in sequences like "aaa""aaa" to become "aaa"aaa".
						f2.getNext();
						continue;
					}
					doingString = false;
					continue;
				}

				// They match, and we don't have anything else that we need to
				// do when they match. We can continue on to the next character.
				continue;

			} // f1.c==f2.c


			// Ignore EOF whitespace
			if (f1.c==0 && f2.c==' ')
				f2.getNext();
			if (f1.c==' ' && f2.c==0)
				f1.getNext();

			// Test for different file lengths
			if (f1.c==0 && f2.c!=0) {
				return "src1 is shorter than src2";
			}
			if (f1.c!=0 && f2.c==0) {
				return "src1 is longer than src2";
			}

			// Progress's preprocessor turns tabs into correct number of spaces for 8-space columns.
			if (doingString && f1.c==' ' && f2.c=='\t') {
				// We just ignore the rest of the whitespace, rather than try to figure
				// out how to convert the tab to spaces.
				while (f1.c==' ')
					f1.getNext();
				f1.redo = true;  // Compare current character, next loop
				while (f2.c=='\t' || f2.c==' ')
					f2.getNext();
				f2.redo = true;  // Compare current character, next loop
				continue;
			}

			// Progress's preprocessor removes escaped newlines.
			// (Sort of... in a string it converts them to '\r' ??!!)
			// Proparse also removes them, but not inside string literals.
			if (doingString && f2.c=='~' && f1.c!='~') {
				boolean doit = false;
				if (f2.la(2)=='\n') {
					f2.getNext();
					doit = true;
				} else if (f2.la(2)=='\r' && f2.la(3)=='\n') {
					f2.getNext();
					f2.getNext();
					doit = true;
				}
				if (doit) {
					if (f1.c=='\r')
						f1.getNext(); // prepro converted escape newline to CR
					f1.redo = true; // compare this character again, next loop
					continue;
				}
			}

			// Progress's preprocessor converts LF to CR LF on Windows.
			// Proparse does not.
			if (f1.c=='\r' && f1.la(2)=='\n' && f2.c=='\n') {
				f2.redo = true;
				continue;
			}

			// Progress's preprocessor will convert "~~\r\n" to "~~\r". Go figure.
			if (doingString && f2.c=='\n' && f1.c!='\n') {
				f1.redo = true;
				continue;
			}

			// Progress's preprocessor leaves in superfluous tildes in identifiers;
			// Proparse removes them.
			if (f1.c=='~' && f2.c!='~' && f1.la(2)==f2.c) {
				f2.redo = true;
				continue;
			}

			// Given a superfluous " ~n." in the code, Progress's preprocessor turns
			// it into " ~\r\n.", and Proparse turns it into " \n.".
			// Since we skip extra whitespace, we end up comparing '~' to '.'.
			if (	!doingString
				&&	f1.c=='~' && f1.la(2)=='\r' && f1.la(3)=='\n' && f1.la(4)==f2.c
				) {
				f1.getNext(); // now f1.c==' ' (consumed both "\r\n")
				f1.getNext(); // matching char
				// Files are now (possibly) in sync. Continue to next char.
				continue;
			}

			// The preprocessor will discard a tilde in front of an unescaped tilde CR sequence.
			// I don't know why.
			if (f2.c== '~' && f1.c!='~' && f1.c==f2.la(2)) {
				f2.getNext();
				continue;
			}

			// Watch for "." handling within table/field names. See bug#016.
			if (f1.c==' ' && f2.c=='.') {
				f2.redo = true;
				continue;
			}

			// Watch for &string character sequences that Progress's preprocessor
			// might have stripped. See [Note 1] at bottom of this file.
			if (f2.c=='&' && f1.c!='&') {
				// Try to re-synch at next matching char.
				while (f2.c!=f1.c && f2.c!=0)
					f2.getNext();
				// Recheck current charpos, in case f2 is now at EOF.
				f1.redo = true;
				f2.redo = true;
				continue;
			}

			// Progress strips tabs and spaces after &THEN. Proparse does not.
			if (	!doingString
				&&	(f1.c!=' ' && f1.c!='\t')
				&&	(f2.c==' ' || f2.c=='\t')
				) {
				while (f2.c==' ' || f2.c=='\t') f2.getNext();
				f1.redo = true;
				f2.redo = true;
				continue;
			}

			// Finally, check for mismatched character
			if (f1.c != f2.c) {
				return "Difference at line " + f1.line + " " + f1.c + ", " + f2.line + " " + f2.c;
			}


		} // while

		return "";

	}



}


/* Notes

[Note 1]
This is particularly ugly. Given this code:
	def var theInt as int init 1.
	def frame a
	theInt view-as radio-set radio-buttons
	&One , 1 , &Two, 2, &Three,3
	.
	update theInt with frame a.
This is the output:
	def var theInt as int init 1.
	def frame a
	theInt view-as radio-set radio-buttons
	, 1 ,  2, 3
	.
	update theInt with frame a.
I have no idea why Progress's preprocessor would strip the &string
sequences. If we hit an unmatched &, then we consume until we match
another character, and then try to take it from there.

*/
