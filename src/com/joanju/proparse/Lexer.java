/*
Lexer.java

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import java.io.IOException;
import java.util.Stack;

import org.prorefactor.core.TokenTypes;


public class Lexer implements ProParserTokenTypes {

	Lexer(Preprocessor prepro) throws IOException {
		this.prepro = prepro;
		this.filenameList = prepro.doParse.getFilenameList();
		getChar(); // We always assume "currChar" is available.
	}

	/** Lowercase value of current character */
	private int currChar;

	/** Current character, before being lowercased */
	private int currInt;

	private int currStringType;
	private StringBuilder currText = new StringBuilder();

	private IntegerIndex<String> filenameList;
	private Preprocessor prepro;

	private boolean gettingAmpIfDefArg = false;
	private boolean preserve = false;
	private int preserveFile;
	private int preserveLine;
	private int preserveCol;
	private int preserveSource;
	private int preserveChar;

	private int textStartFile;
	private int textStartLine;
	private int textStartCol;
	private int textStartSource;

	private static final int EOF_CHAR = Preprocessor.EOF_CHAR;

	private ProToken ampTextToken = null;
	private ProToken newToken = null;
	
	private Stack<ConditionalCompilationToken> condComp = new Stack<ConditionalCompilationToken>();
	private ConditionalCompilationToken condToken = null;
	
//////////////// Lexical productions listed first, support functions follow.


	ProToken nextToken() throws IOException {

		String makroText;
		String incRefText;
		ConditionalCompilationToken token;
		
		for (;;) {

			if (preserve) {
				// The preserved character is the character prior to currChar.
				textStartFile = preserveFile;
				textStartLine = preserveLine;
				textStartCol = preserveCol;
				textStartSource = preserveSource;
				currText.setLength(1);
				currText.setCharAt(0, (char)preserveChar);
				preserveDrop(); // we are done with the preservation
				switch (preserveChar) {
				case '.':
					return periodStart();
				case ':':
					return colon();
				} // switch
			}

			if(this.condToken != null)
			{
				token = this.condToken;
				this.condToken = null;
				return token;
			}
			
			if(!prepro.incRef.isEmpty())
			{
				textStartFile = prepro.textStartFile;
				textStartLine = prepro.textStartLine;
				textStartCol = prepro.textStartCol;
				textStartSource = prepro.textStartSourceNum;
				
				incRefText = prepro.incRef.get(0);
				prepro.incRef.remove(0);
				
				return makeToken(INCLUDEFILEREFERENCE, incRefText);
			}
			
			if(!prepro.makroRef.isEmpty())
			{
				textStartFile = prepro.textStartFile;
				textStartLine = prepro.textStartLine;
				textStartCol = prepro.textStartCol;
				textStartSource = prepro.textStartSourceNum;
				
				makroText = prepro.makroRef.get(0);
				prepro.makroRef.remove(0);
				
				return makeToken(MAKROREFERENCE, makroText);
			}
			
			// Proparse Directive
			// Check this before setting currText...
			// we don't want BEGIN_PROPARSE_DIRECTIVE in the text
			if (currInt == Preprocessor.PROPARSE_DIRECTIVE) {
				textStartFile = prepro.textStartFile;
				textStartLine = prepro.textStartLine;
				textStartCol = prepro.textStartCol;
				textStartSource = prepro.textStartSourceNum;
				getChar();
				return makeToken(PROPARSEDIRECTIVE, prepro.proparseDirectiveText);
			}

			if(ampTextToken != null)
			{
				newToken = ampTextToken;
				ampTextToken = null;
				return newToken;
			}
			
			textStartFile = prepro.getFileIndex();
			textStartLine = prepro.getLine();
			textStartCol = prepro.getColumn();
			textStartSource = prepro.getSourceNum();
			currText.setLength(1);
			currText.setCharAt(0, (char)currInt);
			
			if (gettingAmpIfDefArg) {
				getChar();
				gettingAmpIfDefArg = false;
				return ampIfDefArg();
			}

			switch (currChar) {

			case '\t':
			case '\n':
			case '\f':
			case '\r':
			case ' ':
				getChar();
				return whitespace();

			case '"':
			case '\'':
				if (prepro.escapeCurrent) {
					getChar();
					// Escaped quote does not start a string
					return id(FILENAME);
				} else {
					currStringType = currInt;
					getChar();
					return quotedString();
				}

			case '/':
				getChar();
				if (currChar=='*') {
					return comment();
				} else if (currChar == '/') {
					return singleComment();
				} else if (currChar=='(' || currIsSpace()) {
					// slash (division) can only be followed by whitespace or '('
					// ...that's what I found empirically, anyway. (jag 2003/05/09)
					return makeToken(SLASH);
				} else if (currChar=='=') {
					append();
					getChar();
					return makeToken(DIVIDE_EQUAL);
				} else {
					append();
					getChar();
					return id(FILENAME);
				}

			case ':':
				getChar();
				return colon();

			case '&':
				getChar();
				ampTextToken = ampText();
				switch(ampTextToken.getType())
				{
					case ProParserTokenTypes.AMPIF:
						token = new ConditionalCompilationToken(filenameList, textStartFile, textStartLine, textStartCol, textStartSource);
						token.setAmpIf(ampTextToken);
						this.condComp.push(token);
						newToken = token;
						return newToken;
						
					case ProParserTokenTypes.AMPENDIF:
						token = this.condComp.pop();
						token.setEndIf(ampTextToken);
						newToken = ampTextToken;
						ampTextToken = null;
						this.condToken = new ConditionalCompilationToken(token);						
						return newToken;
						
					default:
						newToken = ampTextToken;
						ampTextToken = null;
						return newToken;
				}
			case '@':
				getChar();
				if (currIsSpace())
					return makeToken(LEXAT);
				else
					append();
				getChar();
				return id(ANNOTATION);
			case '[':
				getChar();
				return makeToken(LEFTBRACE);
			case ']':
				getChar();
				return makeToken(RIGHTBRACE);
			case '^':
				getChar();
				return makeToken(CARET);
			case ',':
				getChar();
				return makeToken(COMMA);
			case '!':
				getChar();
				return makeToken(EXCLAMATION);
			case '=':
				getChar();
				return makeToken(EQUAL);
			case '(':
				getChar();
				return makeToken(LEFTPAREN);
			case ')':
				getChar();
				return makeToken(RIGHTPAREN);
			case ';':
				getChar();
				return makeToken(SEMI);
			case '*':
				getChar();
				if(currChar == '=') {
					append();
					getChar();
					return makeToken(MULTIPLY_EQUAL);
				}
				else {
					return makeToken(STAR);
				}
			case '?':
				getChar();
				return makeToken(UNKNOWNVALUE);
			case '`':
				getChar();
				return makeToken(BACKTICK);

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				getChar();
				return digitStart();

			case '.':
				getChar();
				return periodStart();

			case '>':
				getChar();
				if (currChar=='=') {
					append();
					getChar();
					return makeToken(GTOREQUAL);
				} else {
					return makeToken(RIGHTANGLE);
				}

			case '<':
				getChar();
				if (currChar=='>') {
					append();
					getChar();
					return makeToken(GTORLT);
				} else if (currChar=='=') {
					append();
					getChar();
					return makeToken(LTOREQUAL);
				} else {
					return makeToken(LEFTANGLE);
				}

			case '+':
				getChar();
				if(currChar == '=') {
					append();
					getChar();
					return makeToken(PLUS_EQUAL);
				}
				else {
					return plusMinusStart(PLUS);
				}
			case '-':
				getChar();
				if(currChar == '=') {
					append();
					getChar();
					return makeToken(MINUS_EQUAL);
				}
				else {
					return plusMinusStart(MINUS);
				}

			case '#':
			case '|':
			case '%':
				getChar();
				return id(FILENAME);

			default:
				if (currInt==EOF_CHAR) {
					getChar(); // preprocessor will catch any infinite loop on this.
					return makeToken(ProParserTokenTypes.EOF,"");
				} else {
					getChar();
					return id(ID);
				}

			}
		}
	}
	
	
	/** Get argument for &IF DEFINED(...).
	 * The nextToken function is necessarily the main entry point. This is just
	 * a wrapper around that.
	 */
	ProToken getAmpIfDefArg() throws IOException {
		gettingAmpIfDefArg = true;
		return nextToken();
	}
	
	
	/** Get the text between the parens for &IF DEFINED(...).
	 * The compiler seems to allow any number of tokens between the parens,
	 * and like with an &Name reference, it allows embedded comments.
	 * Here, I'm allowing for the embedded comments and just gathering all the text
	 * up to the closing paren. Hopefully that will do it.
	 * 
	 * The compiler doesn't seem to ignore extra tokens. For example, &if defined(ab cd)
	 * does not match a macro named "ab". It doesn't match "abcd" either, so all I can guess
	 * is that they are combining the text of all the tokens between the parens.
	 * I haven't found any macro name that matches &if defined(ab"cd").
	 * 
	 * The compiler works different here than it does for a typical ID token.
	 * An ID token (like a procedure name) may contain arbitrary quotation marks.
	 * Within an &if defined() function, the quotation marks must match.
	 * I don't know if that really makes a difference, because the quoted string
	 * can't contain a paren ')' anyway, so as far as I can tell we can ignore quotation
	 * marks and just watch for the closing paren.
	 * A macro name can't contain any quotation marks anyway, so for all I know
	 * the compiler's handling of quotes within defined() may just be an artifact of its lexer.
	 * I don't think there's any way to get whitespace into a macro name either.
	 */
	private ProToken ampIfDefArg() throws IOException {
		loop:
		for (;;) {
			if (currChar == ')') {
				break loop;
			}
			// Watch for comments.
			if (currChar=='/') {
				getChar();
				if (currChar != '*') {
					append('/');
					continue loop;
				}
				else {
					String s = currText.toString();
					comment();
					currText.replace(0, currText.length(), s);
					continue loop;
				}
			}
			append();
			getChar();
		}
		return makeToken(ID);
	}


	ProToken colon() throws IOException {
		if (currChar==':') {
			append();
			getChar();
			return makeToken(DOUBLECOLON);
		}
		if (currIsSpace()) return makeToken(LEXCOLON);
		return makeToken(OBJCOLON);
	}


	ProToken whitespace() throws IOException {
		int fileIdx;
		loop:
		for (;;) {
			switch (currChar) {
			case ' ':
			case '\t':
			case '\f':
			case '\n':
			case '\r':
				append();
				fileIdx = prepro.currFile;
				getChar();
				if((!prepro.incRef.isEmpty()) || fileIdx != prepro.currFile)
					break loop;
				break;
			default:
				break loop;
			}
		}
		return makeToken(WS);
	}


	ProToken comment() throws IOException {
		// Escapes in comments are processed because you can end a comment
		// with something dumb like: ~*~/
		// We preserve that text.
		// Note that macros are *not* expanded inside comments.
		// (See the preprocessor source)
		prepro.doingComment = true;
		append(); // currChar=='*'
		int commentLevel = 1;
		while (commentLevel > 0) {
			getChar();
			unEscapedAppend();
			if (currChar=='/') {
				getChar();
				unEscapedAppend();
				if (currChar=='*')
					commentLevel++;
			}
			else if (currChar=='*') {
				while (currChar=='*') {
					getChar();
					unEscapedAppend();
					if (currChar=='/')
						commentLevel--;
				}
			}
			else if (currInt==EOF_CHAR) {
				prepro.lexicalThrow("Missing end of comment");
			}
		}
		prepro.doingComment = false;
		getChar();
		return makeToken(COMMENT);
	}

	ProToken singleComment() throws IOException {
		// Single line comments are treated just like regular comments,
		// everything till end of line is considered comment - no escape
		// character to look after

		append(); // currChar=='/'
		prepro.doingComment = true;
		prepro.doingSingleLineComment = true; 
		
		while (true) {
			getChar();
			unEscapedAppend();
			
			// check if the preprocessor escaped the 4gl new line (~n, ~r)
			if ((prepro.escapeCurrent == false && (currChar == '\r' || currChar == '\n')) || currInt == EOF_CHAR) {
				prepro.doingComment = false;
				prepro.doingSingleLineComment = false; 
				return makeToken(COMMENT);
			}
		}
	}

	ProToken quotedString() throws IOException {
		// Inside quoted strings (string constants) we preserve
		// the source code's original text - we don't discard
		// escape characters.
		// The preprocessor *does* expand macros inside strings.
		for (;;) {
			if (currInt==EOF_CHAR)
				prepro.lexicalThrow("Unmatched quote");
			unEscapedAppend();
			if (currInt == currStringType && !prepro.escapeCurrent) {
				getChar();
				if (currInt == currStringType) { // quoted quote
					unEscapedAppend();
				} else {
					break; // close quote
				}
			}
			getChar();
		}

		if (currChar == ':') {
			boolean isStringAttributes = false;
			// Preserve the colon before calling getChar,
			// in case it belongs in the next token.
			preserveCurrent();
			String theText = ":";
			for_loop:
			for (;;) {
				getChar();
				switch (currChar) {
				case 'r':
				case 'l':
				case 'c':
				case 't':
				case 'u':
				case 'x':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					theText += (char)currInt;
					isStringAttributes = true;
					break;
				default:
					break for_loop;
				}
			}
			// either string attributes, or the preserved colon
			// goes into the next token.
			if (isStringAttributes) {
				append(theText);
				preserveDrop();
			}
		} // currChar==':'

		return makeToken(QSTRING);
	}


	ProToken digitStart() throws IOException {
		int ttype=NUMBER;
		for_loop:
		for (;;) {
			switch (currChar) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				append();
				getChar();
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			case '#':
			case '$':
			case '%':
			case '&':
			case '_':
				append();
				getChar();
				if(ttype!=FILENAME) ttype=ID;
				break;
			// We don't know here if the plus or minus is in the middle or at the end.
			// Don't change ttype.
			case '+':
			case '-':
				append();
				getChar();
				break;
			case '/':
				append();
				getChar();
				if(ttype==NUMBER)
					ttype=LEXDATE;
				break;
			case '\\':
				append();
				getChar();
				ttype=FILENAME;
				break;
			case '.':
				if (prepro.nameDot) {
					append();
					getChar();
					break;
				} else
					break for_loop;
			default:
				break for_loop;
			}
		}
		return makeToken(ttype);
	}


	ProToken plusMinusStart(int inputType) throws IOException {
		int ttype = NUMBER;
		for_loop:
		for (;;) {
			switch (currChar) {
			// We don't know here if the plus or minus is in the middle or at the end.
			// Don't change ttype.
			case '+':
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				append();
				getChar();
				break;
			// Leave comma out of this. -1, might be part of an expression list.
			case '#':
			case '$':
			case '%':
			case '&':
			case '/':
			case '\\':
			case '_':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
				append();
				getChar();
				ttype=FILENAME;
				break;
			case '.':
				if (prepro.nameDot) {
					append();
					getChar();
					break;
				} else
					break for_loop;
			default:
				break for_loop;
			}
		}
		if (currText.length() == 1)
			return makeToken(inputType);
		else
			return makeToken(ttype);
	}


	ProToken periodStart() throws IOException {
		if (! Character.isDigit(currChar)) {
			if (prepro.nameDot)
				return makeToken(NAMEDOT);
			else
				return makeToken(PERIOD);
		}
		int ttype = NUMBER;
		for_loop:
		for (;;) {
			switch (currChar) {
			// We don't know here if the plus or minus is in the middle or at the end.
			// Don't change _ttype.
			case '+':
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				append();
				getChar();
				break;
			case '#':
			case '$':
			case '%':
			case '&':
			case '/':
			case '\\':
			case '_':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
				append();
				getChar();
				ttype = FILENAME;
				break;
			default:
				break for_loop;
			}
		}
		return makeToken(ttype);
	}


	ProToken id(int inputTokenType) throws IOException {
		// Tokens that start with a-z or underscore
		// - ID
		// - FILENAME
		// - keyword (testLiterals = true)
		// Also inputTokenType can be ANNOTATION for a token that starts with '@'.
		// Based on the PROGRESS online help, the following are the valid name characters.
		// Undocumented: you can use a slash in an index name! Arg!
		// Undocumented: the compiler allows you to start a block label with $
		// If we find a back slash, we know we're into a filename.
		// Extended characters (octal 200-377) can be used in identifiers, even at the beginning.
		int ttype = inputTokenType;
		for_loop:
		for (;;) {
			switch (currChar) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			case '_':
			case '-':
			case '$':
			case '#':
			case '%':
			case '&':
			case '/':
			// For tokens like ALT-* and CTRL-`  :
			// Emperically, I found that the following are the only other
			// characters that can be put into a key label. Things
			// like ALT-, must be put into quotes "ALT-,", because
			// the comma has special meaning in 4gl code.
			// Note also that extended characters can come after CTRL- or ALT-.
			// ('!'|'@'|'^'|'*'|'+'|';'|'"'|'`')
			case '!':
			case '"':
			case '*':
			case '+':
			case ';':
			case '@':
			case '^':
			case '`':
				append();
				getChar();
				break;
			case '\\':
			case '\'':
				append();
				getChar();
				if (ttype==ID) ttype = FILENAME;
				break;
			default:
				if (   currChar == '.'
					&& ! Character.isDigit(currChar) 
					&& prepro.nameDot) {
					append();
					getChar();
					break;
				}
				if (currInt >= 128 && currInt <= 255) {
					append();
					getChar();
					break;
				} else {
					break for_loop;
				}
			}
		}
		// See if it's a keyword
		if (ttype==ID)
			ttype = NodeTypes.testLiteralsTable(currText.toString(), ttype);
		return makeToken(ttype);
	}


	ProToken ampText() throws IOException {
		for (;;) {
			if (	Character.isLetterOrDigit(currInt)
				||	(currInt >= 128 && currInt <= 255)
				) {
				append();
				getChar();
				continue;
			}
			switch (currChar) {
				case '#':
				case '$':
				case '%':
				case '&':
				case '-':
				case '_':
					append();
					getChar();
					continue;
			}
			if (currChar=='/') {
				// You can embed comments in (or at the end of) an &token.
				// I've no idea why. See the regression test for bug#083.
				preserveCurrent();
				getChar();
				if (currChar=='*') {
					String s = currText.toString();
					comment();
					currText.replace(0, currText.length(), s);
					preserveDrop();
					continue;
				}
			}
			break;
		}
		ProToken t = directive();
		if (t!=null)
			return t;
		return makeToken(FILENAME);
	}



	ProToken directive() throws IOException {
		// Called by ampText, which has already gather the text for
		// the *potential* directive.

		String macroType = currText.toString().toLowerCase();

		if (	"&global-define".startsWith(macroType)
			&&	macroType.length() >= 4
			) {
			appendToEOL();
			// We have to do the define *before* getting next char.
			macroDefine(AMPGLOBALDEFINE);
			getChar();
			return makeToken(AMPGLOBALDEFINE);
		}
		if (	"&scoped-define".startsWith(macroType)
			&&	macroType.length() >= 4
			) {
			appendToEOL();
			// We have to do the define *before* getting next char.
			macroDefine(AMPSCOPEDDEFINE);
			getChar();
			return makeToken(AMPSCOPEDDEFINE);
		}

		if (	"&undefine".startsWith(macroType)
			&&	macroType.length() >= 5
			) {
			// Append whitespace between &UNDEFINE and the target token
			while (Character.isWhitespace(currChar)) {
				append();
				getChar();
			}
			// Append the target token
			while ( (! Character.isWhitespace(currChar)) && currInt!=EOF_CHAR) {
				append();
				getChar();
			}
			// &UNDEFINE consumes up to *and including* the first whitespace
			// after the token it undefines.
			// At least that seems to be what Progress is doing.
			if (currChar=='\r') {
				append();
				getChar();
				if (currChar=='\n') {
					append();
					getChar();
				}
			} else if (currInt!=EOF_CHAR) {
				append();
				getChar();
			}
			macroUndefine();
			return makeToken(AMPUNDEFINE);
		}

		if (macroType.equals("&analyze-suspend")) {
			appendToEOL();
			getChar();
			return makeToken(AMPANALYZESUSPEND);
		}
		if (macroType.equals("&analyze-resume")) {
			appendToEOL();
			getChar();
			return makeToken(AMPANALYZERESUME);
		}
		if (macroType.equals("&message")) {
			appendToEOL();
			getChar();
			return makeToken(AMPMESSAGE);
		}

		if (macroType.equals("&if")) {
			return makeToken(AMPIF);
		}
		if (macroType.equals("&then")) {
			return makeToken(AMPTHEN);
		}
		if (macroType.equals("&elseif")) {
			return makeToken(AMPELSEIF);
		}
		if (macroType.equals("&else")) {
			return makeToken(AMPELSE);
		}
		if (macroType.equals("&endif")) {
			return makeToken(AMPENDIF);
		}

		// If we got here, it wasn't a preprocessor directive,
		// and the caller is responsible for building the token.
		return null;

	}



//////////////// End lexical productions, begin support functions


	void append() {
		currText.append((char)currInt);
	}


	void append(char c) {
		currText.append(c);
	}


	void append(String theText) {
		currText.append(theText);
	}


	void appendToEOL() throws IOException {
		// As with the other "append" functions,
		// the caller is responsible for calling getChar() after this.
		for (;;) {
			if (currChar=='/') {
				append();
				getChar();
				if (currChar=='*') {
					// comment() expects to start at '*',
					// finishes on char after closing slash
					comment();
					continue;
				}
				continue;
			}
			if (currInt==EOF_CHAR)
				break;
			append();
			if (currChar=='\n') {
				// We do not call getChar() here. That is because  we cannot
				// get the next character until after any &glob, &scoped, or &undefine
				// have been dealt with. The next character might be a '{' which in
				// turn leads to a reference to what is just now being defined or
				// undefined.
				break;
			}
			getChar();
		}
	}


	boolean currIsSpace() {
		return (	currInt==EOF_CHAR
				||	Character.isWhitespace(currChar)
				);
	}


	void getChar() throws IOException {
		currInt = prepro.getChar();
		currChar = Character.toLowerCase(currInt);
	}


	void macroDefine(int defType) throws IOException {
		if (prepro.consuming!=0)
			return;
		int it = 0;
		int end = currText.length();
		while (! Character.isWhitespace(currText.charAt(it)))
			++it;  // "&glob..." or "&scoped..."
		while (Character.isWhitespace(currText.charAt(it)))
			++it;  // whitespace
		int start = it;
		while (! Character.isWhitespace(currText.charAt(it)))
			++it;  // macro name
		String macroName = currText.substring(start, it);
		while (		it!=end
				&&	Character.isWhitespace(currText.charAt(it))
				)
			++it;  // whitespace
		String defText = StringFuncs.stripComments(currText.substring(it));
		defText = defText.trim();
		// Do listing before lowercasing the name
		// Escape line breaks. Somehow it is possible to get line breaks into globdef/scopdef.
		if (prepro.listing) {
			StringBuilder bldr = new StringBuilder();
			bldr
				.append(textStartFile)
				.append(" ")
				.append(textStartLine)
				.append(" ")
				.append(textStartCol)
				.append((defType==AMPGLOBALDEFINE ? " globdef " : " scopdef "))
				.append(macroName)
				.append(" ")
				.append(StringFuncs.escapeLineBreaks(defText));
			prepro.listingStream.write(bldr.toString());
			prepro.listingStream.newLine();
		}
		if (defType == AMPGLOBALDEFINE)
			prepro.defGlobal(macroName.toLowerCase(), defText);
		else
			prepro.defScoped(macroName.toLowerCase(), defText);
	}


	void macroUndefine() throws IOException {
		if (prepro.consuming != 0)
			return;
		int it = 0;
		int end = currText.length();
		while (! Character.isWhitespace(currText.charAt(it)))
			++it;  // "&undef..."
		while (Character.isWhitespace(currText.charAt(it)))
			++it;  // whitespace
		int start = it;
		while (it!=end && (! Character.isWhitespace(currText.charAt(it))))
			++it;  // macro name
		String macroName = currText.substring(start, it);
		// List the name as in the code - not lowercased
		if (prepro.listing) {
			StringBuilder bldr = new StringBuilder();
			bldr
				.append(textStartFile)
				.append(" ")
				.append(textStartLine)
				.append(" ")
				.append(textStartCol)
				.append(" undef ")
				.append(macroName);
			prepro.listingStream.write(bldr.toString());
			prepro.listingStream.newLine();
		}
		prepro.undef(macroName.toLowerCase());
	}


	ProToken makeToken(int ttype) {
		return new ProToken (filenameList, 
							 ttype, 
							 currText.toString(), 
							 textStartFile, 
							 textStartLine, 
							 textStartCol, 
							 textStartSource);
	}


	ProToken makeToken(int ttype, String text) {
		return new ProToken (filenameList, 
							 ttype, 
							 text, 
							 textStartFile, 
							 textStartLine, 
							 textStartCol, 
							 textStartSource);
	}


	ProToken makeToken(int ttype, char text) {
		return new ProToken (filenameList, 
							 ttype, 
							 Character.toString(text), 
							 textStartFile, 
							 textStartLine, 
							 textStartCol, 
							 textStartSource);
	}


	void preserveCurrent() {
		// Preserve the current character/file/line/col before looking
		// ahead to the next character. Need this because current char
		// might be appended to current token, or it might be the start
		// of the next token, depending on what character follows... but
		// as soon as we look ahead to the following character, we lose
		// our file/line/col, and that's why we need to preserve.
		preserve = true;
		preserveFile = prepro.getFileIndex();
		preserveLine = prepro.getLine();
		preserveCol = prepro.getColumn();
		preserveSource = prepro.getSourceNum();
		preserveChar = currChar;
	}


	void preserveDrop() {
		preserve = false;
	}


	void unEscapedAppend() {
		if (prepro.wasEscape) {
			append(prepro.escapeText);
			if (prepro.escapeAppend) append();
		} else
			append();
	}


}
