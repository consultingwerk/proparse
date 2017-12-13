/* NodeTypes.java

For node type names and numbers, this class provides:
   - get the string type name of an integer token type
   - get the integer token type of a string type name
Couldn't just use the literals table, because it does not contain
the names for synthetic node types.

Copyright (C) 2001-2011 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import org.prorefactor.core.TokenTypesI;
import org.prorefactor.core.TokenTypes;

import java.util.HashMap;
import java.io.IOException;

import com.joanju.Xferable;

import com.joanju.DataXferStream;

public class NodeTypes implements ProParserTokenTypes, TokenTypesI, Xferable {

	static HashMap<String, Integer> literalsMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> userliterals = new HashMap<String, Integer>();
	static HashMap<String, Integer> nameNums = new HashMap<String, Integer>();
	int namesEnd = 0;

	static TI[] typeInfoArray = new TI[Last_Token_Number + 1];

	// Type Info
	private static class TI {
		int bitset;
		byte minAbbrev;
		String fullText;

		static final int NO_FLAGS = 0;
		// See Note[1] at end of file.
		static final int EXTRA_LITERAL = 0;
		static final int KEYWORD = 1;
		static final int RESERVED = 2;
		// Some functions fit more than one of these categories:
		// - some function names are reserved, especially the old oddball
		//   ones like ACCUMULATE as well as some old record handling
		//   functions like AVAILABLE.
		// - some functions may look like a regular function, where a left
		//   paren follows the function name.
		// - some functions may be just the function keyword by themselves
		//   without any arguments
		static final int MAY_BE_REGULAR_FUNC = 4;
		static final int MAY_BE_NO_ARG_FUNC = 8;
		// Is the keyword a system handle name?
		static final int SYSHDL = 16;

	}

	/// See end of class for the (huge) static class initializer.




	static void add(int type, int minAbbrev, String fullText, int flags) {
		/*
		 For some token types, there are "synonym" literals. We add those
		 literals to the literals table for that type, and the last literal
		 added wins - it becomes the default text for getFullText().
		 For those synonym literals, there's no point in listing the flags
		 twice, so we just use a null flag of EXTRA_LITERAL.
		 The algorithm makes sure that flags are only added once, i.e. if
		 bitset is greater than zero then no more flags are added.
		*/
		int size = fullText.length();
		for (int i = minAbbrev; i <= size; ++i) {
			literalsMap.put(fullText.substring(0,i), type);
		}
		TI ti = typeInfoArray[type];
		if (ti==null) {
			ti = new TI();
			typeInfoArray[type] = ti;
			ti.minAbbrev = (byte)minAbbrev;
		}
		ti.fullText = fullText;
		if (ti.bitset==0)
			ti.bitset = flags;
		nameNums.put(ProParser._tokenNames[type], type);
	}


	/** Returns uppercase of the type info record's full text.
	 * Returns null if there's no type info for the type number.
	 * Returns empty string if there's no text for the type.
	 */
	public static String getFullText(int type) {
		if (! isValidType(type))
			return null;
		TI ti = typeInfoArray[type];
		if (ti==null)
			return null;
		String s = ti.fullText;
		if (s==null)
			return "";
		return s.toUpperCase();
	}


	public static String getFullText(String text) {
		Integer type = literalsMap.get(text);
		if (type==null)
			return "";
		return typeInfoArray[type].fullText.toUpperCase();
	}


	/** Implements TokenTypesI, calls getTypeName(). */
	public String getName(int tokenType) {return getTypeName(tokenType);}


	/** Get the type name (different than the keyword literal text) for a type number.
	 * Any "_KW" suffix is stripped.
 	 * @param n type number
	 * @return null if out of range
	 */
	public static String getTypeName(int n) {
		if (n > Last_Token_Number || n < 1)
			return null;
		String ret = ProParser._tokenNames[n];
		if (ret.endsWith("_KW"))
			ret = ret.substring(0, ret.length()-3);
		return ret;
	}


	/** Get the type number for a type name.
	 * For those type names that have it, the "_KW" suffix is optional.
	 * @param s type name
	 * @return -1 if invalid type name is entered.
	 */
	public static int getTypeNum(String s) {
		Integer ret = nameNums.get(s);
		if (ret==null) {
			// It's possible that we've been passed a token type name which needs
			// to have the _KW suffix added to it.
			ret = nameNums.get(s + "_KW");
		}
		if (ret==null)
			return -1;
		return ret;
	}


	public static boolean isAbbreviated(String text) {
		String lowText = text.toLowerCase();
		Integer nodeType = literalsMap.get(lowText);
		return	nodeType!=null
			&&	typeInfoArray[nodeType].fullText.length() > lowText.length();
	}


	public static boolean isKeywordType(int nodeType) {
		return (	isValidType(nodeType)
				&&	((typeInfoArray[nodeType].bitset & TI.KEYWORD) > 0)
				);
	}


	static boolean isMethodModifier(int type) {
		switch(type) {
			case PUBLIC:
			case PROTECTED:
			case PRIVATE:
			case OVERRIDE:
			case FINAL:
				return true;
			default:
				return false;
		}
	}


	static boolean isReserved(int nodeType) {
		return (	isValidType(nodeType)
				&&	((typeInfoArray[nodeType].bitset & TI.RESERVED) > 0)
				);
	}


	static boolean isSystemHandleName(int nodeType) {
		return (	isValidType(nodeType)
				&&	((typeInfoArray[nodeType].bitset & TI.SYSHDL) > 0)
				);
	}


	static boolean isUnreservedKeywordType(int nodeType) {
		return (	isValidType(nodeType)
				&&	((typeInfoArray[nodeType].bitset & TI.KEYWORD) > 0)
				&&	((typeInfoArray[nodeType].bitset & TI.RESERVED) == 0)
				);
	}


	static boolean isValidType(int type) {
		return (	type > 0
				&&	type <= Last_Token_Number
				&&	typeInfoArray[type] != null
				);
	}


	static boolean mayBeNoArgFunc(int nodeType) {
		return (	isValidType(nodeType)
				&&	((typeInfoArray[nodeType].bitset & TI.MAY_BE_NO_ARG_FUNC) > 0)
				);
	}


	static boolean mayBeRegularFunc(int nodeType) {
		return (	isValidType(nodeType)
				&&	((typeInfoArray[nodeType].bitset & TI.MAY_BE_REGULAR_FUNC) > 0)
				);
	}


	static int minAbbrev(int nodeType) {
		if (!isValidType(nodeType))
			return -1;
		return typeInfoArray[nodeType].minAbbrev;
	}


	/** Test if a literal string maps to a keyword literal or a user defined literal.
	 * @param text Literal string to match.
	 * @param ttype Default return, if no match is found.
	 * @return Token type if matched, input ttype if no match.
	 */
	static int testLiteralsTable(String text, int ttype) {
		String lowText = text.toLowerCase();
		Integer ret = literalsMap.get(lowText);
		if (ret!=null)
			return ret;
		ret = userliterals.get(lowText);
		if (ret!=null)
			return ret;
		return ttype;
	}


	static void userLiteralAdd(String text, String ttype) throws Exception {
		String lowType = ttype.toLowerCase();
		Integer typeNum = getTypeNum(lowType);
		// The documentation states that the second parameter must be a valid Proparse
		// token type, but we'll also check the literals table.
		if (typeNum==-1) {
			typeNum = literalsMap.get(lowType);
			if (typeNum==null)
				throw new Exception("dictAdd(): Invalid token type");
		}
		userliterals.put(text.toLowerCase(), typeNum);
	}


	static void userLiteralDelete(String text) {
		userliterals.remove(text);
	}


	public static boolean userLiteralTest(String text, int ttype) {
		Integer t = userliterals.get(text.toLowerCase());
		return (t!=null && t==ttype);
	}


	/** Implement Xferable.
	 * Since this class's data is all static, Xferable for NodeTypes
	 * is somewhat different than Xferable for most other classes.
	 * It doesn't even behave like a proper collection - the Last_Token_Number
	 * is very unlikely to be the number of node types written to the list.
	 * No schema is written for this class.
	 */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeInt(Last_Token_Number);
		for (int num = 1; num <= Last_Token_Number; ++num) {
			String name = getTypeName(num);
			if (name==null || name.length()==0)
				continue;
			// Get the *default* text for the token, especially needed
			// for symbols like COMMA which aren't given text in our set here.
			String defaultText = TokenTypes.getDefaultText(num);
			if (defaultText==null || defaultText.length()==0)
				defaultText = getFullText(num);
			if (defaultText == null)
				defaultText = "";
			out.writeInt(num);
			out.writeRef(name);
			out.writeRef(defaultText);
			out.writeBool(isKeywordType(num));
			out.writeBool(isReserved(num));
			out.writeInt(minAbbrev(num));
		}
	}
	/** No schema is written for this class. */
	public void writeXferSchema(DataXferStream out) throws IOException {}


	// Last, because it's huge.
	static {
		// For synonyms, the *last* added literal becomes the default literal.
		add(AACBIT, 5, "_cbit", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(AACONTROL, 8, "_control", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(AALIST, 5, "_list", TI.KEYWORD);
		add(AAMEMORY, 7, "_memory", TI.KEYWORD | TI.SYSHDL);
		add(AAMSG, 4, "_msg", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(AAPCONTROL, 9, "_pcontrol", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(AASERIAL, 7, "_serial-num", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(AATRACE, 6, "_trace", TI.KEYWORD);
		add(ABSOLUTE, 3, "absolute", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ABSTRACT, 8, "abstract", TI.KEYWORD);
		add(ACCELERATOR, 11, "accelerator", TI.KEYWORD);
		add(ACCUMULATE, 5, "accumulate", TI.KEYWORD | TI.RESERVED);
		add(ACTIVEFORM, 11, "active-form", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(ACTIVEWINDOW, 13, "active-window", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(ADD, 3, "add", TI.KEYWORD | TI.RESERVED);
		add(ADDINTERVAL, 12, "add-interval", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ADVISE, 6, "advise", TI.KEYWORD);
		add(ALERTBOX, 9, "alert-box", TI.KEYWORD);
		add(ALIAS, 5, "alias", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(ALL, 3, "all", TI.KEYWORD | TI.RESERVED);
		add(ALLOWREPLICATION, 17, "allow-replication", TI.KEYWORD);
		add(ALTER, 5, "alter", TI.KEYWORD | TI.RESERVED);
		add(ALTERNATEKEY, 13, "alternate-key", TI.KEYWORD);
		add(AMBIGUOUS, 5, "ambiguous", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(AMPANALYZERESUME, 0, "", TI.NO_FLAGS);
		add(AMPANALYZESUSPEND, 0, "", TI.NO_FLAGS);
		add(AMPELSE, 0, "", TI.NO_FLAGS);
		add(AMPELSEIF, 0, "", TI.NO_FLAGS);
		add(AMPENDIF, 0, "", TI.NO_FLAGS);
		add(AMPGLOBALDEFINE, 0, "", TI.NO_FLAGS);
		add(AMPIF, 0, "", TI.NO_FLAGS);
		add(AMPMESSAGE, 0, "", TI.NO_FLAGS);
		add(AMPSCOPEDDEFINE, 0, "", TI.NO_FLAGS);
		add(AMPTHEN, 0, "", TI.NO_FLAGS);
		add(AMPUNDEFINE, 0, "", TI.NO_FLAGS);
		add(ANALYZE, 6, "analyze", TI.KEYWORD);
		add(AND, 3, "and", TI.KEYWORD | TI.RESERVED);
		add(ANNOTATION, 0, "", TI.NO_FLAGS);
		add(ANSIONLY, 9, "ansi-only", TI.KEYWORD);
		add(ANY, 3, "any", TI.KEYWORD | TI.RESERVED);
		add(ANYWHERE, 8, "anywhere", TI.KEYWORD);
		add(APPEND, 6, "append", TI.KEYWORD);
		add(APPLICATION, 11, "application", TI.KEYWORD);
		add(APPLY, 5, "apply", TI.KEYWORD | TI.RESERVED);
		add(ARRAYMESSAGE, 7, "array-message", TI.KEYWORD);
		add(AS, 2, "as", TI.KEYWORD);
		add(ASC, 3, "asc", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ASCENDING, 4, "ascending", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(ASKOVERWRITE, 13, "ask-overwrite", TI.KEYWORD);
		add(ASSEMBLY, 8, "assembly", TI.KEYWORD);
		add(ASSIGN, 6, "assign", TI.KEYWORD | TI.RESERVED);
		add(Assign_dynamic_new, 0, "", TI.NO_FLAGS);
		add(ASYNCHRONOUS, 12, "asynchronous", TI.KEYWORD);
		add(AT, 2, "at", TI.KEYWORD | TI.RESERVED);
		add(ATTACHMENT, 6, "attachment", TI.KEYWORD);
		add(ATTRSPACE, 4, "attr-space", TI.KEYWORD | TI.RESERVED);
		add(AUDITCONTROL, 13, "audit-control", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(AUDITENABLED, 13, "audit-enabled", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(AUDITPOLICY, 12, "audit-policy", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(AUTHORIZATION, 13, "authorization", TI.KEYWORD | TI.RESERVED);
		add(AUTOCOMPLETION, 9, "auto-completion", TI.KEYWORD);
		add(AUTOENDKEY, 12, "auto-end-key", TI.KEYWORD);
		add(AUTOENDKEY, 11, "auto-endkey", TI.EXTRA_LITERAL);
		add(AUTOGO, 7, "auto-go", TI.KEYWORD);
		add(AUTOMATIC, 9, "automatic", TI.KEYWORD);
		add(AUTORETURN, 8, "auto-return", TI.KEYWORD | TI.RESERVED);
		add(AVAILABLE, 5, "available", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(AVERAGE, 3, "average", TI.KEYWORD);
		add(AVG, 3, "avg", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(Aggregate_phrase, 0, "", TI.NO_FLAGS);
		add(Array_subscript, 0, "", TI.NO_FLAGS);
		add(Assign_from_buffer, 0, "", TI.NO_FLAGS);
		add(Automationobject, 0, "", TI.NO_FLAGS);
		add(BACKGROUND, 4, "background", TI.KEYWORD | TI.RESERVED);
		add(BACKSLASH, 0, "", TI.NO_FLAGS);
		add(BACKTICK, 0, "", TI.NO_FLAGS);
		add(BACKWARDS, 8, "backwards", TI.KEYWORD);
		add(BASE64, 6, "base64", TI.KEYWORD);
		add(BASE64DECODE, 13, "base64-decode", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BASE64ENCODE, 13, "base64-encode", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BASEKEY, 8, "base-key", TI.KEYWORD);
		add(BATCHSIZE, 10, "batch-size", TI.KEYWORD);
		add(BEFOREHIDE, 8, "before-hide", TI.KEYWORD | TI.RESERVED);
		add(BEFORETABLE, 12, "before-table", TI.KEYWORD);
		add(BEGINS, 6, "begins", TI.KEYWORD | TI.RESERVED);
		add(BELL, 4, "bell", TI.KEYWORD | TI.RESERVED);
		add(BETWEEN, 7, "between", TI.KEYWORD | TI.RESERVED);
		add(BGCOLOR, 3, "bgcolor", TI.KEYWORD);
		add(BIGENDIAN, 10, "big-endian", TI.KEYWORD | TI.RESERVED);
		add(BIGINT, 6, "bigint", TI.KEYWORD);
		add(BINARY, 6, "binary", TI.KEYWORD);
		add(BIND, 4, "bind", TI.KEYWORD);
		add(BINDWHERE, 10, "bind-where", TI.KEYWORD);
		add(BLANK, 5, "blank", TI.KEYWORD | TI.RESERVED);
		add(BLOB, 4, "blob", TI.KEYWORD);
		add(BLOCK_LABEL, 0, "", TI.NO_FLAGS);
		add(BLOCKLEVEL, 11, "block-level", TI.KEYWORD);
		add(BOTH, 4, "both", TI.KEYWORD);
		add(BOTTOM, 6, "bottom", TI.KEYWORD);
		add(BOX, 3, "box", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BREAK, 5, "break", TI.KEYWORD | TI.RESERVED);
		add(BROWSE, 6, "browse", TI.KEYWORD);
		add(BTOS, 4, "btos", TI.KEYWORD);
		add(BUFFER, 6, "buffer", TI.KEYWORD);
		add(BUFFERCHARS, 12, "buffer-chars", TI.KEYWORD);
		add(BUFFERCOMPARE, 14, "buffer-compare", TI.KEYWORD | TI.RESERVED);
		add(BUFFERCOPY, 11, "buffer-copy", TI.KEYWORD | TI.RESERVED);
		add(BUFFERLINES, 12, "buffer-lines", TI.KEYWORD);
		add(BUFFERNAME, 8, "buffer-name", TI.KEYWORD);
		add(BUFFER_GROUP_ID, 15, "buffer-group-id", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BUFFER_GROUP_NAME, 17, "buffer-group-name", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BUFFER_PARTITION_ID, 19, "buffer-partition-id", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BUFFER_TENANT_ID, 16, "buffer-tenant-id", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BUFFER_TENANT_NAME, 18, "buffer-tenant-name", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(BUTTON, 6, "button", TI.KEYWORD);
		add(BUTTONS, 7, "buttons", TI.KEYWORD);
		add(BY, 2, "by", TI.KEYWORD | TI.RESERVED);
		add(BYPOINTER, 10, "by-pointer", TI.KEYWORD | TI.RESERVED);
		add(BYREFERENCE, 12, "by-reference", TI.KEYWORD);
		add(BYTE, 4, "byte", TI.KEYWORD);
		add(BYVALUE, 8, "by-value", TI.KEYWORD);
		add(BYVARIANTPOINTER, 16, "by-variant-pointer", TI.KEYWORD | TI.RESERVED);
		add(Block_iterator, 0, "", TI.NO_FLAGS);
		add(CACHE, 5, "cache", TI.KEYWORD);
		add(CACHESIZE, 10, "cache-size", TI.KEYWORD);
		add(CALL, 4, "call", TI.KEYWORD | TI.RESERVED);
		add(CANCELBUTTON, 13, "cancel-button", TI.KEYWORD);
		add(CANDO, 6, "can-do", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CANFIND, 8, "can-find", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CANQUERY, 9, "can-query", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(CANSET, 7, "can-set", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(CAPS, 4, "caps", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(CAPS, 5, "upper", TI.EXTRA_LITERAL);
		add(CARET, 0, "", TI.NO_FLAGS);
		add(CASE, 4, "case", TI.KEYWORD | TI.RESERVED);
		add(CASESENSITIVE, 8, "case-sensitive", TI.KEYWORD | TI.RESERVED);
		add(CAST, 4, "cast", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CATCH, 5, "catch", TI.KEYWORD);
		add(CDECL_KW, 5, "cdecl", TI.KEYWORD);
		add(CENTERED, 6, "centered", TI.KEYWORD | TI.RESERVED);
		add(CHAINED, 7, "chained", TI.KEYWORD);
		add(CHARACTER, 4, "character", TI.KEYWORD);
		add(CHARACTERLENGTH, 15, "characterlength", TI.KEYWORD);
		add(CHECK, 5, "check", TI.KEYWORD | TI.RESERVED);
		add(CHOOSE, 6, "choose", TI.KEYWORD);
		add(CHR, 3, "chr", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CLASS, 5, "class", TI.KEYWORD);
		add(CLEAR, 5, "clear", TI.KEYWORD | TI.RESERVED);
		add(CLIENTPRINCIPAL, 16, "client-principal", TI.KEYWORD);
		add(CLIPBOARD, 9, "clipboard", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(CLOB, 4, "clob", TI.KEYWORD);
		add(CLOSE, 5, "close", TI.KEYWORD);
		add(CODEBASELOCATOR, 16, "codebase-locator", TI.KEYWORD | TI.SYSHDL);
		add(CODEPAGE, 8, "codepage", TI.KEYWORD);
		add(CODEPAGECONVERT, 16, "codepage-convert", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(COLLATE, 7, "collate", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(COLOF, 6, "col-of", TI.KEYWORD);
		add(COLON, 5, "colon", TI.KEYWORD | TI.RESERVED);
		add(COLONALIGNED, 11, "colon-aligned", TI.KEYWORD);
		add(COLOR, 5, "color", TI.KEYWORD | TI.RESERVED);
		add(COLORTABLE, 11, "color-table", TI.KEYWORD | TI.SYSHDL);
		add(COLUMN, 3, "column", TI.KEYWORD);
		add(COLUMNBGCOLOR, 10, "column-bgcolor", TI.KEYWORD);
		add(COLUMNCODEPAGE, 15, "column-codepage", TI.KEYWORD);
		add(COLUMNDCOLOR, 13, "column-dcolor", TI.KEYWORD);
		add(COLUMNFGCOLOR, 10, "column-fgcolor", TI.KEYWORD);
		add(COLUMNFONT, 11, "column-font", TI.KEYWORD);
		add(COLUMNLABEL, 10, "column-label", TI.KEYWORD | TI.RESERVED);
		add(COLUMNOF, 9, "column-of", TI.KEYWORD);
		add(COLUMNPFCOLOR, 10, "column-pfcolor", TI.KEYWORD);
		add(COLUMNS, 7, "columns", TI.KEYWORD);
		add(COMBOBOX, 9, "combo-box", TI.KEYWORD);
		add(COMHANDLE, 10, "com-handle", TI.KEYWORD);
		add(COMHANDLE, 16, "component-handle", TI.EXTRA_LITERAL);
		add(COMMA, 0, "", TI.NO_FLAGS);
		add(COMMAND, 7, "command", TI.KEYWORD);
		add(COMMENT, 0, "", TI.NO_FLAGS);
		add(COMMENTEND, 0, "", TI.NO_FLAGS);
		add(COMMENTSTART, 0, "", TI.NO_FLAGS);
		add(COMPARE, 7, "compare", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(COMPARES, 8, "compares", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(COMPILE, 7, "compile", TI.KEYWORD);
		add(COMPILER, 8, "compiler", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(COMPLETE, 8, "complete", TI.KEYWORD);
		add(COMSELF, 8, "com-self", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(CONFIGNAME, 11, "config-name", TI.KEYWORD);
		add(CONNECT, 7, "connect", TI.KEYWORD);
		add(CONNECTED, 9, "connected", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CONSTRUCTOR, 11, "constructor", TI.KEYWORD);
		add(CONTAINS, 8, "contains", TI.KEYWORD);
		add(CONTENTS, 8, "contents", TI.KEYWORD);
		add(CONTEXT, 7, "context", TI.KEYWORD);
		add(CONTEXTHELP, 12, "context-help", TI.KEYWORD);
		add(CONTEXTHELPFILE, 17, "context-help-file", TI.KEYWORD);
		add(CONTEXTHELPID, 15, "context-help-id", TI.KEYWORD);
		add(CONTEXTPOPUP, 11, "context-popup", TI.KEYWORD);
		add(CONTROL, 7, "control", TI.KEYWORD | TI.RESERVED);
		add(CONTROLFRAME, 13, "control-frame", TI.KEYWORD);
		add(CONVERT, 7, "convert", TI.KEYWORD);
		add(CONVERT3DCOLORS, 10, "convert-3d-colors", TI.KEYWORD);
		add(COPYDATASET, 12, "copy-dataset", TI.KEYWORD);
		add(COPYLOB, 8, "copy-lob", TI.KEYWORD | TI.RESERVED);
		add(COPYTEMPTABLE, 15, "copy-temp-table", TI.KEYWORD);
		add(COUNT, 5, "count", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(COUNTOF, 8, "count-of", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CREATE, 6, "create", TI.KEYWORD | TI.RESERVED);
		add(CREATELIKESEQUENTIAL, 22, "create-like-sequential", TI.KEYWORD);
		add(CREATETESTFILE, 16, "create-test-file", TI.KEYWORD);
		add(CURLYAMP, 0, "", TI.NO_FLAGS);
		add(CURLYNUMBER, 0, "", TI.NO_FLAGS);
		add(CURLYSTAR, 0, "", TI.NO_FLAGS);
		add(CURRENCY, 8, "currency", TI.KEYWORD);
		add(CURRENT, 7, "current", TI.KEYWORD | TI.RESERVED);
		add(CURRENTCHANGED, 15, "current-changed", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(CURRENTENVIRONMENT, 11, "current-environment", TI.KEYWORD);
		add(CURRENTLANGUAGE, 12, "current-language", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(CURRENTQUERY, 13, "current-query", TI.KEYWORD);
		add(CURRENTRESULTROW, 18, "current-result-row", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(CURRENTVALUE, 13, "current-value", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(CURRENTWINDOW, 14, "current-window", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(CURSOR, 4, "cursor", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(Code_block, 0, "", TI.NO_FLAGS);
		add(DATABASE, 8, "database", TI.KEYWORD | TI.RESERVED);
		add(DATABIND, 6, "data-bind", TI.KEYWORD);
		add(DATARELATION, 13, "data-relation", TI.KEYWORD | TI.RESERVED);
		add(DATASERVERS, 11, "dataservers", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(DATASERVERS, 7, "gateways", TI.EXTRA_LITERAL);
		add(DATASET, 7, "dataset", TI.KEYWORD | TI.RESERVED);
		add(DATASETHANDLE, 14, "dataset-handle", TI.KEYWORD | TI.RESERVED);
		add(DATASOURCE, 11, "data-source", TI.KEYWORD);
		add(DATASOURCEMODIFIED, 20, "data-source-modified", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DATASOURCEROWID, 17, "data-source-rowid", TI.KEYWORD);
		add(DATE, 4, "date", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DATETIME, 8, "datetime", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DATETIMETZ, 11, "datetime-tz", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DAY, 3, "day", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DBCODEPAGE, 10, "dbcodepage", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DBCOLLATION, 11, "dbcollation", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DBIMS, 5, "dbims", TI.KEYWORD);
		add(DBNAME, 6, "dbname", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(DBPARAM, 7, "dbparam", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DBREMOTEHOST, 14, "db-remote-host", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DBRESTRICTIONS, 6, "dbrestrictions", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DBTASKID, 8, "dbtaskid", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DBTYPE, 6, "dbtype", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DBVERSION, 6, "dbversion", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DCOLOR, 6, "dcolor", TI.KEYWORD);
		add(DDE, 3, "dde", TI.KEYWORD | TI.RESERVED);
		add(DEBLANK, 7, "deblank", TI.KEYWORD | TI.RESERVED);
		add(DEBUG, 4, "debug", TI.KEYWORD);
		add(DEBUGGER, 8, "debugger", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(DEBUGLIST, 10, "debug-list", TI.KEYWORD | TI.RESERVED);
		add(DECIMAL, 3, "decimal", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DECIMALS, 8, "decimals", TI.KEYWORD | TI.RESERVED);
		add(DECLARE, 7, "declare", TI.KEYWORD | TI.RESERVED);
		add(DECRYPT, 7, "decrypt", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DEFAULT, 7, "default", TI.KEYWORD | TI.RESERVED);
		add(DEFAULTBUTTON, 8, "default-button", TI.KEYWORD);
		add(DEFAULTEXTENSION, 10, "default-extension", TI.KEYWORD);
		add(DEFAULTNOXLATE, 12, "default-noxlate", TI.KEYWORD);
		add(DEFAULTVALUE, 13, "default-value", TI.KEYWORD);
		add(DEFAULTWINDOW, 14, "default-window", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(DEFERLOBFETCH, 15, "defer-lob-fetch", TI.KEYWORD);
		add(DEFINE, 3, "define", TI.KEYWORD | TI.RESERVED);
		add(DEFINED, 7, "defined", TI.KEYWORD);
		add(DEFINETEXT, 0, "", TI.NO_FLAGS);
		add(DELEGATE, 8, "delegate", TI.KEYWORD);
		add(DELETECHARACTER, 11, "delete-character", TI.KEYWORD);
		add(DELETERESULTLISTENTRY, 24, "delete-result-list-entry", TI.KEYWORD);
		add(DELETE_KW, 6, "delete", TI.KEYWORD | TI.RESERVED);
		add(DELIMITER, 9, "delimiter", TI.KEYWORD | TI.RESERVED);
		add(DESCENDING, 4, "descending", TI.KEYWORD | TI.RESERVED);
		add(DESELECTION, 11, "deselection", TI.KEYWORD);
		add(DESTRUCTOR, 10, "destructor", TI.KEYWORD);
		add(DIALOGBOX, 10, "dialog-box", TI.KEYWORD);
		add(DIALOGHELP, 11, "dialog-help", TI.KEYWORD);
		add(DICTIONARY, 4, "dictionary", TI.KEYWORD | TI.RESERVED);
		add(DIGITS, 0, "", TI.NO_FLAGS);
		add(DIGITSTART, 0, "", TI.NO_FLAGS);
		add(DIR, 3, "dir", TI.KEYWORD);
		add(DISABLE, 7, "disable", TI.KEYWORD | TI.RESERVED);
		add(DISABLEAUTOZAP, 16, "disable-auto-zap", TI.KEYWORD | TI.RESERVED);
		add(DISABLED, 8, "disabled", TI.KEYWORD);
		add(DISCONNECT, 6, "disconnect", TI.KEYWORD | TI.RESERVED);
		add(DISPLAY, 4, "display", TI.KEYWORD | TI.RESERVED);
		add(DISTINCT, 8, "distinct", TI.KEYWORD | TI.RESERVED);
		add(DIVIDE, 0, "", TI.NO_FLAGS);
		add(DO, 2, "do", TI.KEYWORD | TI.RESERVED);
		add(DOS, 3, "dos", TI.KEYWORD | TI.RESERVED);
		add(DOT_COMMENT, 0, "", TI.NO_FLAGS);
		add(DOUBLE, 6, "double", TI.KEYWORD);
		add(DOUBLECOLON, 0, "", TI.NO_FLAGS);
		add(DOUBLEQUOTE, 0, "", TI.NO_FLAGS);
		add(DOWN, 4, "down", TI.KEYWORD | TI.RESERVED);
		add(DQSTRING, 0, "", TI.NO_FLAGS);
		add(DROP, 4, "drop", TI.KEYWORD | TI.RESERVED);
		add(DROPDOWN, 9, "drop-down", TI.KEYWORD);
		add(DROPDOWNLIST, 14, "drop-down-list", TI.KEYWORD);
		add(DROPFILENOTIFY, 16, "drop-file-notify", TI.KEYWORD);
		add(DROPTARGET, 11, "drop-target", TI.KEYWORD);
		add(DUMP, 4, "dump", TI.KEYWORD);
		add(DYNAMIC, 7, "dynamic", TI.KEYWORD);
		add(DYNAMICCAST, 12, "dynamic-cast", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DYNAMICCURRENTVALUE, 21, "dynamic-current-value", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(DYNAMICFUNCTION, 12, "dynamic-function", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DYNAMICINVOKE, 14, "dynamic-invoke", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(DYNAMICNEW, 11, "dynamic-new", TI.KEYWORD);
		add(DYNAMICNEXTVALUE, 18, "dynamic-next-value", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(EACH, 4, "each", TI.KEYWORD | TI.RESERVED);
		add(ECHO, 4, "echo", TI.KEYWORD);
		add(EDGECHARS, 4, "edge-chars", TI.KEYWORD);
		add(EDGEPIXELS, 6, "edge-pixels", TI.KEYWORD);
		add(EDITING, 7, "editing", TI.KEYWORD | TI.RESERVED);
		add(EDITOR, 6, "editor", TI.KEYWORD);
		add(EDITUNDO, 9, "edit-undo", TI.KEYWORD);
		add(ELSE, 4, "else", TI.KEYWORD | TI.RESERVED);
		add(EMPTY, 5, "empty", TI.KEYWORD);
		add(ENABLE, 6, "enable", TI.KEYWORD | TI.RESERVED);
		add(ENABLEDFIELDS, 14, "enabled-fields", TI.KEYWORD);
		add(ENCODE, 6, "encode", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(ENCRYPT, 7, "encrypt", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ENCRYPTIONSALT, 15, "encryption-salt", TI.KEYWORD);
		add(END, 3, "end", TI.KEYWORD | TI.RESERVED);
		add(ENDKEY, 7, "end-key", TI.KEYWORD);
		add(ENDKEY, 6, "endkey", TI.EXTRA_LITERAL);
		add(ENDMOVE, 8, "end-move", TI.KEYWORD);
		add(ENDRESIZE, 10, "end-resize", TI.KEYWORD);
		add(ENDROWRESIZE, 14, "end-row-resize", TI.KEYWORD);
		add(ENTERED, 7, "entered", TI.KEYWORD);
		add(ENTRY, 5, "entry", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
        add(ENUM, 4, "enum", TI.KEYWORD);
		add(EQ, 2, "eq", TI.KEYWORD);
		add(EQUAL, 0, "", TI.NO_FLAGS);
		add(ERROR, 5, "error", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ERRORCODE, 10, "error-code", TI.KEYWORD);
		add(ERRORSTACKTRACE, 17, "error-stack-trace", TI.KEYWORD);
		add(ERRORSTATUS, 10, "error-status", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(ESCAPE, 6, "escape", TI.KEYWORD | TI.RESERVED);
		add(ESCAPED_QUOTE, 0, "", TI.NO_FLAGS);
		add(ETIME_KW, 5, "etime", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(EVENT, 5, "event", TI.KEYWORD);
		add(EVENTPROCEDURE, 15, "event-procedure", TI.KEYWORD);
		add(EVENTS, 6, "events", TI.KEYWORD);
		add(EXCEPT, 6, "except", TI.KEYWORD | TI.RESERVED);
		add(EXCLAMATION, 0, "", TI.NO_FLAGS);
		add(EXCLUSIVEID, 12, "exclusive-id", TI.KEYWORD);
		add(EXCLUSIVELOCK, 9, "exclusive-lock", TI.KEYWORD | TI.RESERVED);
		add(EXCLUSIVEWEBUSER, 13, "exclusive-web-user", TI.KEYWORD);
		add(EXECUTE, 7, "execute", TI.KEYWORD);
		add(EXISTS, 6, "exists", TI.KEYWORD | TI.RESERVED);
		add(EXP, 3, "exp", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(EXPAND, 6, "expand", TI.KEYWORD);
		add(EXPANDABLE, 10, "expandable", TI.KEYWORD);
		add(EXPLICIT, 8, "explicit", TI.KEYWORD);
		add(EXPORT, 6, "export", TI.KEYWORD | TI.RESERVED);
		add(EXTENDED, 8, "extended", TI.KEYWORD);
		add(EXTENT, 6, "extent", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(EXTERNAL, 8, "external", TI.KEYWORD);
		add(Editing_phrase, 0, "", TI.NO_FLAGS);
		add(Entered_func, 0, "", TI.NO_FLAGS);
		add(Event_list, 0, "", TI.NO_FLAGS);
		add(Expr_statement, 0, "", TI.NO_FLAGS);
		add(FALSELEAKS, 11, "false-leaks", TI.KEYWORD | TI.RESERVED);
		add(FALSE_KW, 5, "false", TI.KEYWORD | TI.RESERVED);
		add(FETCH, 5, "fetch", TI.KEYWORD | TI.RESERVED);
		add(FGCOLOR, 3, "fgcolor", TI.KEYWORD);
		add(FIELD, 5, "field", TI.KEYWORD | TI.RESERVED);
		add(FIELDS, 6, "fields", TI.KEYWORD | TI.RESERVED);
		add(FILE, 4, "file", TI.KEYWORD);
		add(FILE, 9, "file-name", TI.EXTRA_LITERAL);
		add(FILE, 8, "filename", TI.EXTRA_LITERAL);
		add(FILEINFORMATION, 9, "file-information", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(FILENAME, 0, "", TI.NO_FLAGS);
		add(FILL, 4, "fill", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(FILLIN, 7, "fill-in", TI.KEYWORD);
		add(FILTERS, 7, "filters", TI.KEYWORD);
		add(FINAL, 5, "final", TI.KEYWORD);
		add(FINALLY, 7, "finally", TI.KEYWORD);
		add(FIND, 4, "find", TI.KEYWORD | TI.RESERVED);
		add(FINDCASESENSITIVE, 19, "find-case-sensitive", TI.KEYWORD | TI.RESERVED);
		add(FINDER, 6, "finder", TI.KEYWORD);
		add(FINDGLOBAL, 11, "find-global", TI.KEYWORD | TI.RESERVED);
		add(FINDNEXTOCCURRENCE, 20, "find-next-occurrence", TI.KEYWORD | TI.RESERVED);
		add(FINDPREVOCCURRENCE, 20, "find-prev-occurrence", TI.KEYWORD | TI.RESERVED);
		add(FINDSELECT, 11, "find-select", TI.KEYWORD | TI.RESERVED);
		add(FINDWRAPAROUND, 16, "find-wrap-around", TI.KEYWORD | TI.RESERVED);
		add(FIRST, 5, "first", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(FIRSTFORM, 10, "first-form", TI.KEYWORD);
		add(FIRSTOF, 8, "first-of", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(FITLASTCOLUMN, 15, "fit-last-column", TI.KEYWORD);
		add(FIXCHAR, 7, "fixchar", TI.KEYWORD);
		add(FIXCODEPAGE, 12, "fix-codepage", TI.KEYWORD);
		add(FIXEDONLY, 10, "fixed-only", TI.KEYWORD);
        add(FLAGS, 5, "flags", TI.KEYWORD);
		add(FLATBUTTON, 11, "flat-button", TI.KEYWORD);
		add(FLOAT, 5, "float", TI.KEYWORD);
		add(FOCUS, 5, "focus", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(FONT, 4, "font", TI.KEYWORD | TI.RESERVED);
		add(FONTBASEDLAYOUT, 17, "font-based-layout", TI.KEYWORD);
		add(FONTTABLE, 10, "font-table", TI.KEYWORD | TI.SYSHDL);
		add(FOR, 3, "for", TI.KEYWORD | TI.RESERVED);
		add(FORCEFILE, 10, "force-file", TI.KEYWORD);
		add(FOREIGNKEYHIDDEN, 18, "foreign-key-hidden", TI.KEYWORD);
		add(FORMAT, 4, "format", TI.KEYWORD | TI.RESERVED);
		add(FORMINPUT, 9, "forminput", TI.KEYWORD);
		add(FORMLONGINPUT, 15, "form-long-input", TI.KEYWORD);
		add(FORWARDS, 7, "forwards", TI.KEYWORD);
		add(FRAME, 4, "frame", TI.KEYWORD | TI.RESERVED);
		add(FRAMECOL, 9, "frame-col", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(FRAMEDB, 8, "frame-db", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(FRAMEDOWN, 10, "frame-down", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(FRAMEFIELD, 11, "frame-field", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(FRAMEFILE, 10, "frame-file", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(FRAMEINDEX, 10, "frame-index", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(FRAMELINE, 10, "frame-line", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(FRAMENAME, 10, "frame-name", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(FRAMEROW, 9, "frame-row", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(FRAMEVALUE, 9, "frame-value", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(FREECHAR, 0, "", TI.NO_FLAGS);
		add(FREQUENCY, 9, "frequency", TI.KEYWORD);
		add(FROM, 4, "from", TI.KEYWORD | TI.RESERVED);
		add(FROMCURRENT, 8, "from-current", TI.KEYWORD);
		add(FUNCTION, 8, "function", TI.KEYWORD);
		add(FUNCTIONCALLTYPE, 18, "function-call-type", TI.KEYWORD | TI.RESERVED);
		add(Field_list, 0, "", TI.NO_FLAGS);
		add(Field_ref, 0, "", TI.NO_FLAGS);
		add(Form_item, 0, "", TI.NO_FLAGS);
		add(Format_phrase, 0, "", TI.NO_FLAGS);
		add(GATEWAYS, 0, "", TI.MAY_BE_NO_ARG_FUNC);
		add(GE, 2, "ge", TI.KEYWORD);
		add(GENERATEMD5, 12, "generate-md5", TI.KEYWORD);
		add(GENERATEPBEKEY, 16, "generate-pbe-key", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GENERATEPBESALT, 17, "generate-pbe-salt", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(GENERATERANDOMKEY, 19, "generate-random-key", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(GENERATEUUID, 13, "generate-uuid", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(GET, 3, "get", TI.KEYWORD);
		add(GETATTRCALLTYPE, 18, "get-attr-call-type", TI.KEYWORD | TI.RESERVED);
		add(GETBITS, 8, "get-bits", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETBUFFERHANDLE, 17, "get-buffer-handle", TI.KEYWORD | TI.RESERVED);
		add(GETBYTE, 8, "get-byte", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETBYTE, 7, "getbyte", TI.EXTRA_LITERAL);
		add(GETBYTEORDER, 14, "get-byte-order", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETBYTES, 9, "get-bytes", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETCGILIST, 12, "get-cgi-list", TI.KEYWORD);
		add(GETCGILONGVALUE, 18, "get-cgi-long-value", TI.KEYWORD);
		add(GETCGIVALUE, 13, "get-cgi-value", TI.KEYWORD);
		add(GETCLASS, 9, "get-class", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
        add(GETCODEPAGE, 12, "get-codepage", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
        add(GETCODEPAGES, 13, "get-codepages", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(GETCOLLATIONS, 8, "get-collations", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(GETCONFIGVALUE, 16, "get-config-value", TI.KEYWORD);
		add(GETDIR, 7, "get-dir", TI.KEYWORD);
		add(GETDOUBLE, 10, "get-double", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GET_EFFECTIVE_TENANT_ID, 23, "get-effective-tenant-id", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GET_EFFECTIVE_TENANT_NAME, 25, "get-effective-tenant-name", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETFILE, 8, "get-file", TI.KEYWORD);
		add(GETFLOAT, 9, "get-float", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETINT64, 9, "get-int64", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETKEYVALUE, 11, "get-key-value", TI.KEYWORD | TI.RESERVED);
		add(GETLICENSE, 11, "get-license", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETLONG, 8, "get-long", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETPOINTERVALUE, 17, "get-pointer-value", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETSHORT, 9, "get-short", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETSIZE, 8, "get-size", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETSTRING, 10, "get-string", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETUNSIGNEDLONG, 17, "get-unsigned-long", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GETUNSIGNEDSHORT, 18, "get-unsigned-short", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(GLOBAL, 6, "global", TI.KEYWORD | TI.RESERVED);
		add(GLOBALDEFINE, 0, "", TI.NO_FLAGS);
		add(GOON, 5, "go-on", TI.KEYWORD | TI.RESERVED);
		add(GOPENDING, 7, "go-pending", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(GRANT, 5, "grant", TI.KEYWORD | TI.RESERVED);
		add(GRAPHICEDGE, 9, "graphic-edge", TI.KEYWORD | TI.RESERVED);
		add(GROUP, 5, "group", TI.KEYWORD | TI.RESERVED);
		add(GROUPBOX, 9, "group-box", TI.KEYWORD);
		add(GTHAN, 2, "gt", TI.KEYWORD);
		add(GTOREQUAL, 0, "", TI.NO_FLAGS);
		add(GTORLT, 0, "", TI.NO_FLAGS);
		add(GUID, 4, "guid", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(HANDLE, 6, "handle", TI.KEYWORD);
		add(HAVING, 6, "having", TI.KEYWORD | TI.RESERVED);
		add(HEADER, 6, "header", TI.KEYWORD | TI.RESERVED);
		add(HEIGHT, 6, "height", TI.KEYWORD);
        add(HEIGHTCHARS, 8, "height-chars", TI.KEYWORD);
        add(HEIGHTPIXELS, 8, "height-pixels", TI.KEYWORD);
		add(HELP, 4, "help", TI.KEYWORD | TI.RESERVED);
		add(HELPTOPIC, 10, "help-topic", TI.KEYWORD);
		add(HEXDECODE, 10, "hex-decode", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(HEXENCODE, 10, "hex-encode", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(HIDE, 4, "hide", TI.KEYWORD | TI.RESERVED);
		add(HINT, 4, "hint", TI.KEYWORD);
		add(HORIZONTAL, 4, "horizontal", TI.KEYWORD);
		add(HOSTBYTEORDER, 15, "host-byte-order", TI.KEYWORD | TI.RESERVED);
		add(HTMLENDOFLINE, 16, "html-end-of-line", TI.KEYWORD);
		add(HTMLFRAMEBEGIN, 16, "html-frame-begin", TI.KEYWORD);
		add(HTMLFRAMEEND, 14, "html-frame-end", TI.KEYWORD);
		add(HTMLHEADERBEGIN, 17, "html-header-begin", TI.KEYWORD);
		add(HTMLHEADEREND, 15, "html-header-end", TI.KEYWORD);
		add(HTMLTITLEBEGIN, 16, "html-title-begin", TI.KEYWORD);
		add(HTMLTITLEEND, 14, "html-title-end", TI.KEYWORD);
		add(ID, 0, "", TI.NO_FLAGS);
		add(ID_THREE, 0, "", TI.NO_FLAGS);
		add(ID_TWO, 0, "", TI.NO_FLAGS);
		add(IF, 2, "if", TI.KEYWORD | TI.RESERVED);
		add(IFCOND, 0, "", TI.NO_FLAGS);
		add(IMAGE, 5, "image", TI.KEYWORD);
		add(IMAGEDOWN, 10, "image-down", TI.KEYWORD);
		add(IMAGEINSENSITIVE, 17, "image-insensitive", TI.KEYWORD);
		add(IMAGESIZE, 10, "image-size", TI.KEYWORD);
		add(IMAGESIZECHARS, 12, "image-size-chars", TI.KEYWORD);
		add(IMAGESIZEPIXELS, 12, "image-size-pixels", TI.KEYWORD);
		add(IMAGEUP, 8, "image-up", TI.KEYWORD);
		add(IMPLEMENTS, 10, "implements", TI.KEYWORD);
		add(IMPORT, 6, "import", TI.KEYWORD | TI.RESERVED);
		add(IMPOSSIBLE_TOKEN, 0, "", TI.NO_FLAGS);
		add(INCLUDEREFARG, 0, "", TI.NO_FLAGS);
		add(INCREMENTEXCLUSIVEID, 22, "increment-exclusive-id", TI.KEYWORD);
		add(INDEX, 5, "index", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(INDEXEDREPOSITION, 18, "indexed-reposition", TI.KEYWORD);
		add(INDEXHINT, 10, "index-hint", TI.KEYWORD);
		add(INDICATOR, 9, "indicator", TI.KEYWORD | TI.RESERVED);
		add(INFORMATION, 4, "information", TI.KEYWORD);
		add(INHERITBGCOLOR, 11, "inherit-bgcolor", TI.KEYWORD);
		add(INHERITFGCOLOR, 11, "inherit-fgcolor", TI.KEYWORD);
		add(INHERITS, 8, "inherits", TI.KEYWORD);
		add(INITIAL, 4, "initial", TI.KEYWORD);
		add(INITIALDIR, 11, "initial-dir", TI.KEYWORD);
		add(INITIALFILTER, 14, "initial-filter", TI.KEYWORD);
		add(INITIATE, 8, "initiate", TI.KEYWORD);
		add(INNER, 5, "inner", TI.KEYWORD);
		add(INNERCHARS, 11, "inner-chars", TI.KEYWORD);
		add(INNERLINES, 11, "inner-lines", TI.KEYWORD);
		add(INPUT, 5, "input", TI.KEYWORD | TI.RESERVED);
		add(INPUTOUTPUT, 7, "input-output", TI.KEYWORD | TI.RESERVED);
		add(INSERT, 6, "insert", TI.KEYWORD | TI.RESERVED);
		add(INT64, 5, "int64", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(INTEGER, 3, "integer", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(INTERFACE, 9, "interface", TI.KEYWORD);
		add(INTERVAL, 8, "interval", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(INTO, 4, "into", TI.KEYWORD | TI.RESERVED);
		add(IN_KW, 2, "in", TI.KEYWORD | TI.RESERVED);
		add(IS, 2, "is", TI.KEYWORD | TI.RESERVED);
		add(ISATTRSPACE, 7, "is-attr-space", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(ISCODEPAGEFIXED, 17, "is-codepage-fixed", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ISCOLUMNCODEPAGE, 18, "is-column-codepage", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(IS_DB_MULTI_TENANT, 18, "is-db-multi-tenant", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ISLEADBYTE, 7, "is-lead-byte", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(ISODATE, 8, "iso-date", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ITEM, 4, "item", TI.KEYWORD);
		add(IUNKNOWN, 8, "iunknown", TI.KEYWORD);
		add(Inline_definition, 0, "", TI.NO_FLAGS);
		add(JOIN, 4, "join", TI.KEYWORD | TI.RESERVED);
		add(JOINBYSQLDB, 13, "join-by-sqldb", TI.KEYWORD);
		add(KBLABEL, 7, "kblabel", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(KEEPMESSAGES, 13, "keep-messages", TI.KEYWORD);
		add(KEEPTABORDER, 14, "keep-tab-order", TI.KEYWORD);
		add(KEY, 3, "key", TI.KEYWORD);
		add(KEYCODE, 8, "key-code", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(KEYCODE, 7, "keycode", TI.EXTRA_LITERAL);
		add(KEYFUNCTION, 8, "key-function", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(KEYFUNCTION, 7, "keyfunction", TI.EXTRA_LITERAL);
		add(KEYLABEL, 9, "key-label", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(KEYLABEL, 8, "keylabel", TI.EXTRA_LITERAL);
		add(KEYS, 4, "keys", TI.KEYWORD | TI.RESERVED);
		add(KEYWORD, 7, "keyword", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(KEYWORDALL, 11, "keyword-all", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LABEL, 5, "label", TI.KEYWORD | TI.RESERVED);
		add(LABELBGCOLOR, 9, "label-bgcolor", TI.KEYWORD);
		add(LABELDCOLOR, 8, "label-dcolor", TI.KEYWORD);
		add(LABELFGCOLOR, 9, "label-fgcolor", TI.KEYWORD);
		add(LABELFONT, 10, "label-font", TI.KEYWORD);
		add(LANDSCAPE, 9, "landscape", TI.KEYWORD);
		add(LANGUAGES, 8, "languages", TI.KEYWORD);
		add(LARGE, 5, "large", TI.KEYWORD);
		add(LARGETOSMALL, 14, "large-to-small", TI.KEYWORD);
		add(LAST, 4, "last", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(LASTBATCH, 10, "last-batch", TI.KEYWORD);
		add(LASTEVENT, 9, "last-event", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(LASTFORM, 9, "last-form", TI.KEYWORD);
		add(LASTKEY, 8, "last-key", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(LASTKEY, 7, "lastkey", TI.EXTRA_LITERAL);
		add(LASTOF, 7, "last-of", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(LC, 2, "lc", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LC, 5, "lower", TI.EXTRA_LITERAL);
		add(LDBNAME, 7, "ldbname", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(LE, 2, "le", TI.KEYWORD);
		add(LEAKDETECTION, 14, "leak-detection", TI.KEYWORD | TI.RESERVED);
		add(LEAVE, 5, "leave", TI.KEYWORD | TI.RESERVED);
		add(LEFT, 4, "left", TI.KEYWORD);
		add(LEFTALIGNED, 10, "left-aligned", TI.KEYWORD);
		add(LEFTANGLE, 0, "", TI.NO_FLAGS);
		add(LEFTBRACE, 0, "", TI.NO_FLAGS);
		add(LEFTCURLY, 0, "", TI.NO_FLAGS);
		add(LEFTPAREN, 0, "", TI.NO_FLAGS);
		add(LEFTTRIM, 9, "left-trim", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LENGTH, 6, "length", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LEXAT, 0, "", TI.NO_FLAGS);
		add(LEXCOLON, 0, "", TI.NO_FLAGS);
		add(LEXDATE, 0, "", TI.NO_FLAGS);
		add(LEXOTHER, 0, "", TI.NO_FLAGS);
		add(LIBRARY, 7, "library", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(LIKE, 4, "like", TI.KEYWORD | TI.RESERVED);
		add(LIKESEQUENTIAL, 15, "like-sequential", TI.KEYWORD | TI.RESERVED);
		add(LINECOUNTER, 10, "line-counter", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(LISTEVENTS, 11, "list-events", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LISTING, 5, "listing", TI.KEYWORD | TI.RESERVED);
		add(LISTITEMPAIRS, 15, "list-item-pairs", TI.KEYWORD);
		add(LISTITEMS, 10, "list-items", TI.KEYWORD);
		add(LISTQUERYATTRS, 16, "list-query-attrs", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LISTSETATTRS, 14, "list-set-attrs", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LISTWIDGETS, 12, "list-widgets", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LITTLEENDIAN, 13, "little-endian", TI.KEYWORD | TI.RESERVED);
		add(LOAD, 4, "load", TI.KEYWORD);
		add(LOADPICTURE, 12, "load-picture", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LOBDIR, 7, "lob-dir", TI.KEYWORD);
		add(LOCAL_METHOD_REF, 0, "", TI.NO_FLAGS);
		add(LOCKED, 6, "locked", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(LOG, 3, "log", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LOGICAL, 7, "logical", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(LOGMANAGER, 11, "log-manager", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(LONG, 4, "long", TI.KEYWORD);
		add(LONGCHAR, 8, "longchar", TI.KEYWORD);
		add(LOOKAHEAD, 9, "lookahead", TI.KEYWORD);
		add(LOOKUP, 6, "lookup", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(LTHAN, 2, "lt", TI.KEYWORD);
		add(LTOREQUAL, 0, "", TI.NO_FLAGS);
		add(Last_Token_Number, 0, "", TI.NO_FLAGS);
		add(Loose_End_Keeper, 0, "", TI.NO_FLAGS);
		add(MACHINECLASS, 13, "machine-class", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(MAP, 3, "map", TI.KEYWORD | TI.RESERVED);
		add(MARGINEXTRA, 12, "margin-extra", TI.KEYWORD);
		add(MARKNEW, 8, "mark-new", TI.KEYWORD);
		add(MARKROWSTATE, 14, "mark-row-state", TI.KEYWORD);
		add(MATCHES, 7, "matches", TI.KEYWORD);
		add(MAXCHARS, 9, "max-chars", TI.KEYWORD);
		add(MAXIMIZE, 8, "maximize", TI.KEYWORD);
		add(MAXIMUM, 3, "max", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(MAXIMUM, 7, "maximum", TI.EXTRA_LITERAL);
		add(MAXIMUMLEVEL, 13, "maximum-level", TI.KEYWORD);
		add(MAXROWS, 8, "max-rows", TI.KEYWORD);
		add(MAXSIZE, 8, "max-size", TI.KEYWORD);
		add(MAXVALUE, 7, "max-value", TI.KEYWORD);
		add(MD5DIGEST, 10, "md5-digest", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(MEMBER, 6, "member", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(MEMPTR, 6, "memptr", TI.KEYWORD);
		add(MENU, 4, "menu", TI.KEYWORD);
		add(MENUBAR, 8, "menu-bar", TI.KEYWORD);
		add(MENUBAR, 7, "menubar", TI.EXTRA_LITERAL);
		add(MENUITEM, 9, "menu-item", TI.KEYWORD);
		add(MERGEBYFIELD, 14, "merge-by-field", TI.KEYWORD);
		add(MESSAGE, 7, "message", TI.KEYWORD | TI.RESERVED);
		add(MESSAGEDIGEST, 14, "message-digest", TI.KEYWORD);
		add(MESSAGELINE, 12, "message-line", TI.KEYWORD);
		add(MESSAGELINES, 13, "message-lines", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(METHOD, 6, "method", TI.KEYWORD);
		add(MINIMUM, 3, "minimum", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(MINSIZE, 8, "min-size", TI.KEYWORD);
		add(MINUS, 0, "", TI.NO_FLAGS);
		add(MINVALUE, 7, "min-value", TI.KEYWORD);
		add(MODULO, 3, "modulo", TI.KEYWORD);
		add(MONTH, 5, "month", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(MOUSE, 5, "mouse", TI.KEYWORD | TI.SYSHDL);
		add(MOUSEPOINTER, 7, "mouse-pointer", TI.KEYWORD);
		add(MPE, 3, "mpe", TI.KEYWORD);
		add(MTIME, 5, "mtime", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC | TI.MAY_BE_NO_ARG_FUNC);
		add(MULTIPLE, 8, "multiple", TI.KEYWORD);
		add(MULTIPLEKEY, 12, "multiple-key", TI.KEYWORD);
		add(MULTIPLY, 0, "", TI.NO_FLAGS);
		add(MUSTEXIST, 10, "must-exist", TI.KEYWORD);
		add(Method_param_list, 0, "", TI.NO_FLAGS);
		add(Method_parameter, 0, "", TI.NO_FLAGS);
		add(NAMEDOT, 0, "", TI.NO_FLAGS);
		add(NAMESPACEPREFIX, 16, "namespace-prefix", TI.KEYWORD);
		add(NAMESPACEURI, 13, "namespace-uri", TI.KEYWORD);
		add(NATIVE, 6, "native", TI.KEYWORD);
		add(NE, 2, "ne", TI.KEYWORD);
		add(NESTED, 6, "nested", TI.KEYWORD);
		add(NEW, 3, "new", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(NEWINSTANCE, 12, "new-instance", TI.KEYWORD);
		add(NEWLINE, 0, "", TI.NO_FLAGS);
		add(NEXT, 4, "next", TI.KEYWORD | TI.RESERVED);
		add(NEXTPROMPT, 11, "next-prompt", TI.KEYWORD | TI.RESERVED);
		add(NEXTVALUE, 10, "next-value", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(NO, 2, "no", TI.KEYWORD | TI.RESERVED);
		add(NOAPPLY, 8, "no-apply", TI.KEYWORD);
		add(NOARRAYMESSAGE, 10, "no-array-message", TI.KEYWORD);
		add(NOASSIGN, 9, "no-assign", TI.KEYWORD);
		add(NOATTRLIST, 9, "no-attr-list", TI.KEYWORD | TI.RESERVED);
		add(NOATTRSPACE, 7, "no-attr-space", TI.KEYWORD | TI.RESERVED);
		add(NOAUTOVALIDATE, 16, "no-auto-validate", TI.KEYWORD);
		add(NOBINDWHERE, 13, "no-bind-where", TI.KEYWORD);
		add(NOBOX, 6, "no-box", TI.KEYWORD);
		add(NOCOLUMNSCROLLING, 12, "no-column-scrolling", TI.KEYWORD);
		add(NOCONSOLE, 10, "no-console", TI.KEYWORD);
		add(NOCONVERT, 10, "no-convert", TI.KEYWORD);
		add(NOCONVERT3DCOLORS, 13, "no-convert-3d-colors", TI.KEYWORD);
		add(NOCURRENTVALUE, 16, "no-current-value", TI.KEYWORD);
		add(NODEBUG, 8, "no-debug", TI.KEYWORD);
		add(NODRAG, 7, "no-drag", TI.KEYWORD);
		add(NOECHO, 7, "no-echo", TI.KEYWORD);
		add(NOEMPTYSPACE, 14, "no-empty-space", TI.KEYWORD);
		add(NOERROR_KW, 8, "no-error", TI.KEYWORD | TI.RESERVED);
		add(NOFILL, 4, "no-fill", TI.KEYWORD | TI.RESERVED);
		add(NOFOCUS, 8, "no-focus", TI.KEYWORD | TI.RESERVED);
		add(NOHELP, 7, "no-help", TI.KEYWORD | TI.RESERVED);
		add(NOHIDE, 7, "no-hide", TI.KEYWORD | TI.RESERVED);
		add(NOINDEXHINT, 13, "no-index-hint", TI.KEYWORD);
		add(NOINHERITBGCOLOR, 14, "no-inherit-bgcolor", TI.KEYWORD);
		add(NOINHERITFGCOLOR, 14, "no-inherit-fgcolor", TI.KEYWORD);
		add(NOJOINBYSQLDB, 16, "no-join-by-sqldb", TI.KEYWORD);
		add(NOLABELS, 8, "no-labels", TI.KEYWORD | TI.RESERVED);
		add(NOLOBS, 7, "no-lobs", TI.KEYWORD | TI.RESERVED);
		add(NOLOCK, 7, "no-lock", TI.KEYWORD | TI.RESERVED);
		add(NOLOOKAHEAD, 12, "no-lookahead", TI.KEYWORD);
		add(NOMAP, 6, "no-map", TI.KEYWORD | TI.RESERVED);
		add(NOMESSAGE, 6, "no-message", TI.KEYWORD | TI.RESERVED);
		add(NONE, 4, "none", TI.KEYWORD);
		add(NON_SERIALIZABLE, 16, "non-serializable", TI.KEYWORD);
		add(NOPAUSE, 8, "no-pause", TI.KEYWORD | TI.RESERVED);
		add(NOPREFETCH, 8, "no-prefetch", TI.KEYWORD | TI.RESERVED);
		add(NORETURNVALUE, 13, "no-return-value", TI.KEYWORD | TI.RESERVED);
		add(NORMAL, 6, "normal", TI.KEYWORD);
		add(NORMALIZE, 9, "normalize", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(NOROWMARKERS, 14, "no-row-markers", TI.KEYWORD);
		add(NOSCROLLBARVERTICAL, 14, "no-scrollbar-vertical", TI.KEYWORD);
		add(NOSEPARATECONNECTION, 22, "no-separate-connection", TI.KEYWORD);
		add(NOSEPARATORS, 13, "no-separators", TI.KEYWORD);
		add(NOT, 3, "not", TI.KEYWORD | TI.RESERVED);
		add(NOTACTIVE, 10, "not-active", TI.KEYWORD);
		add(NOTABSTOP, 6, "no-tab-stop", TI.KEYWORD);
		add(NOUNDERLINE, 6, "no-underline", TI.KEYWORD);
		add(NOUNDO, 7, "no-undo", TI.KEYWORD | TI.RESERVED);
		add(NOVALIDATE, 6, "no-validate", TI.KEYWORD | TI.RESERVED);
		add(NOW, 3, "now", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(NOWAIT, 7, "no-wait", TI.KEYWORD | TI.RESERVED);
		add(NOWORDWRAP, 12, "no-word-wrap", TI.KEYWORD);
		add(NULL_KW, 4, "null", TI.KEYWORD | TI.RESERVED);
		add(NUMALIASES, 7, "num-aliases", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(NUMBER, 0, "", TI.NO_FLAGS);
		add(NUMCOPIES, 10, "num-copies", TI.KEYWORD);
		add(NUMDBS, 7, "num-dbs", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(NUMENTRIES, 11, "num-entries", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(NUMERIC, 7, "numeric", TI.KEYWORD);
		add(NUMRESULTS, 11, "num-results", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(Not_casesens, 0, "", TI.NO_FLAGS);
		add(Not_null, 0, "", TI.NO_FLAGS);
		add(OBJCOLON, 0, "", TI.NO_FLAGS);
		add(OBJECT, 6, "object", TI.KEYWORD);
		add(OCTETLENGTH, 12, "octet-length", TI.KEYWORD);
		add(OF, 2, "of", TI.KEYWORD | TI.RESERVED);
		add(OFF, 3, "off", TI.KEYWORD | TI.RESERVED);
		add(OK, 2, "ok", TI.KEYWORD);
		add(OKCANCEL, 9, "ok-cancel", TI.KEYWORD);
		add(OLD, 3, "old", TI.KEYWORD | TI.RESERVED);
		add(ON, 2, "on", TI.KEYWORD | TI.RESERVED);
		add(ONLY, 4, "only", TI.KEYWORD);
		add(OPEN, 4, "open", TI.KEYWORD | TI.RESERVED);
		add(OPSYS, 5, "opsys", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(OPTION, 6, "option", TI.KEYWORD | TI.RESERVED);
		add(OR, 2, "or", TI.KEYWORD | TI.RESERVED);
		add(ORDER, 5, "order", TI.KEYWORD);
		add(ORDEREDJOIN, 12, "ordered-join", TI.KEYWORD);
		add(ORDINAL, 7, "ordinal", TI.KEYWORD);
		add(OS2, 3, "os2", TI.KEYWORD);
		add(OS400, 5, "os400", TI.KEYWORD);
		add(OSAPPEND, 9, "os-append", TI.KEYWORD | TI.RESERVED);
		add(OSCOMMAND, 10, "os-command", TI.KEYWORD | TI.RESERVED);
		add(OSCOPY, 7, "os-copy", TI.KEYWORD | TI.RESERVED);
		add(OSCREATEDIR, 13, "os-create-dir", TI.KEYWORD | TI.RESERVED);
		add(OSDELETE, 9, "os-delete", TI.KEYWORD | TI.RESERVED);
		add(OSDIR, 6, "os-dir", TI.KEYWORD | TI.RESERVED);
		add(OSDRIVES, 8, "os-drives", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(OSERROR, 8, "os-error", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(OSGETENV, 9, "os-getenv", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(OSRENAME, 9, "os-rename", TI.KEYWORD | TI.RESERVED);
		add(OTHERWISE, 9, "otherwise", TI.KEYWORD | TI.RESERVED);
		add(OUTER, 5, "outer", TI.KEYWORD);
		add(OUTERJOIN, 10, "outer-join", TI.KEYWORD);
		add(OUTPUT, 6, "output", TI.KEYWORD | TI.RESERVED);
		add(OVERLAY, 7, "overlay", TI.KEYWORD | TI.RESERVED);
		add(OVERRIDE, 8, "override", TI.KEYWORD);
		add(PAGE, 4, "page", TI.KEYWORD | TI.RESERVED);
		add(PAGEBOTTOM, 8, "page-bottom", TI.KEYWORD | TI.RESERVED);
		add(PAGED, 5, "paged", TI.KEYWORD);
		add(PAGENUMBER, 8, "page-number", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(PAGESIZE_KW, 9, "page-size", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(PAGETOP, 8, "page-top", TI.KEYWORD | TI.RESERVED);
		add(PAGEWIDTH, 8, "page-width", TI.KEYWORD);
		add(PARAMETER, 5, "parameter", TI.KEYWORD | TI.RESERVED);
		add(PARENT, 6, "parent", TI.KEYWORD);
		add(PARENTFIELDSAFTER, 19, "parent-fields-after", TI.KEYWORD);
		add(PARENTFIELDSBEFORE, 20, "parent-fields-before", TI.KEYWORD);
		add(PARENTIDFIELD, 15, "parent-id-field", TI.KEYWORD);
		add(PARENTIDRELATION, 18, "parent-id-relation", TI.KEYWORD);
		add(PARTIALKEY, 11, "partial-key", TI.KEYWORD);
		add(PASCAL_KW, 6, "pascal", TI.KEYWORD);
		add(PASSWORDFIELD, 14, "password-field", TI.KEYWORD | TI.RESERVED);
		add(PAUSE, 5, "pause", TI.KEYWORD | TI.RESERVED);
		add(PBEHASHALGORITHM, 12, "pbe-hash-algorithm", TI.KEYWORD);
		add(PBEKEYROUNDS, 14, "pbe-key-rounds", TI.KEYWORD);
		add(PDBNAME, 7, "pdbname", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(PERFORMANCE, 4, "performance", TI.KEYWORD);
		add(PERIOD, 0, "", TI.NO_FLAGS);
		add(PERIODSTART, 0, "", TI.NO_FLAGS);
		add(PERSISTENT, 7, "persistent", TI.KEYWORD | TI.RESERVED);
		add(PFCOLOR, 3, "pfcolor", TI.KEYWORD);
		add(PINNABLE, 8, "pinnable", TI.KEYWORD);
		add(PIPE, 0, "", TI.NO_FLAGS);
		add(PLUS, 0, "", TI.NO_FLAGS);
		add(PLUSMINUSSTART, 0, "", TI.NO_FLAGS);
		add(PORTRAIT, 8, "portrait", TI.KEYWORD);
		add(POSITION, 8, "position", TI.KEYWORD);
		add(PRECISION, 9, "precision", TI.KEYWORD);
		add(PREFERDATASET, 14, "prefer-dataset", TI.KEYWORD);
		add(PREPROCESS, 7, "preprocess", TI.KEYWORD | TI.RESERVED);
		add(PREPROCESSDIRECTIVE, 0, "", TI.NO_FLAGS);
		add(PREPROCESSELSE, 0, "", TI.NO_FLAGS);
		add(PREPROCESSELSEIF, 0, "", TI.NO_FLAGS);
		add(PREPROCESSENDIF, 0, "", TI.NO_FLAGS);
		add(PREPROCESSIF, 0, "", TI.NO_FLAGS);
		add(PREPROCESSJMESSAGE, 0, "", TI.NO_FLAGS);
		add(PREPROCESSMESSAGE, 0, "", TI.NO_FLAGS);
		add(PREPROCESSTOKEN, 0, "", TI.NO_FLAGS);
		add(PREPROCESSUNDEFINE, 0, "", TI.NO_FLAGS);
		add(PRESELECT, 6, "preselect", TI.KEYWORD);
		add(PREV, 4, "prev", TI.KEYWORD);
		add(PRIMARY, 7, "primary", TI.KEYWORD);
		add(PRINTER, 7, "printer", TI.KEYWORD);
		add(PRINTERSETUP, 13, "printer-setup", TI.KEYWORD);
		add(PRIVATE, 7, "private", TI.KEYWORD);
		add(PRIVILEGES, 10, "privileges", TI.KEYWORD | TI.RESERVED);
		add(PROCEDURE, 5, "procedure", TI.KEYWORD);
		add(PROCEDURECALLTYPE, 19, "procedure-call-type", TI.KEYWORD | TI.RESERVED);
		add(PROCESS, 7, "process", TI.KEYWORD | TI.RESERVED);
		add(PROCHANDLE, 7, "proc-handle", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(PROCSTATUS, 7, "proc-status", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(PROCTEXT, 9, "proc-text", TI.KEYWORD);
		add(PROCTEXTBUFFER, 16, "proc-text-buffer", TI.KEYWORD);
		add(PROFILER, 8, "profiler", TI.KEYWORD | TI.SYSHDL);
		add(PROGRAMNAME, 12, "program-name", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(PROGRESS, 8, "progress", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(PROMPT, 6, "prompt", TI.KEYWORD);
		add(PROMPTFOR, 8, "prompt-for", TI.KEYWORD | TI.RESERVED);
		add(PROMSGS, 7, "promsgs", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(PROPARSEDIRECTIVE, 0, "", TI.NO_FLAGS);
		add(PROPATH, 7, "propath", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(PROPERTY, 8, "property", TI.KEYWORD);
		add(Property_getter, 0, "", TI.NO_FLAGS);
		add(Property_setter, 0, "", TI.NO_FLAGS);
		add(PROTECTED, 9, "protected", TI.KEYWORD);
		add(PROVERSION, 7, "proversion", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(PUBLIC, 6, "public", TI.KEYWORD);
		add(PUBLISH, 7, "publish", TI.KEYWORD);
		add(PUT, 3, "put", TI.KEYWORD | TI.RESERVED);
		add(PUTBITS, 8, "put-bits", TI.KEYWORD);
		add(PUTBYTE, 8, "put-byte", TI.KEYWORD | TI.RESERVED);
		add(PUTBYTE, 7, "putbyte", TI.EXTRA_LITERAL);
		add(PUTBYTES, 9, "put-bytes", TI.KEYWORD);
		add(PUTDOUBLE, 10, "put-double", TI.KEYWORD);
		add(PUTFLOAT, 9, "put-float", TI.KEYWORD);
		add(PUTINT64, 9, "put-int64", TI.KEYWORD);
		add(PUTKEYVALUE, 11, "put-key-value", TI.KEYWORD | TI.RESERVED);
		add(PUTLONG, 8, "put-long", TI.KEYWORD);
		add(PUTSHORT, 9, "put-short", TI.KEYWORD);
		add(PUTSTRING, 10, "put-string", TI.KEYWORD);
		add(PUTUNSIGNEDLONG, 17, "put-unsigned-long", TI.KEYWORD);
		add(PUTUNSIGNEDSHORT, 18, "put-unsigned-short", TI.KEYWORD);
		add(Parameter_list, 0, "", TI.NO_FLAGS);
		add(Program_root, 0, "", TI.NO_FLAGS);
		add(Program_tail, 0, "", TI.NO_FLAGS);
		add(QSTRING, 0, "", TI.NO_FLAGS);
		add(QUERY, 5, "query", TI.KEYWORD | TI.RESERVED);
		add(QUERYCLOSE, 11, "query-close", TI.KEYWORD | TI.RESERVED);
		add(QUERYOFFEND, 13, "query-off-end", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(QUERYTUNING, 12, "query-tuning", TI.KEYWORD | TI.RESERVED);
		add(QUESTION, 8, "question", TI.KEYWORD);
		add(QUIT, 4, "quit", TI.KEYWORD | TI.RESERVED);
		add(QUOTER, 6, "quoter", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RADIOBUTTONS, 13, "radio-buttons", TI.KEYWORD);
		add(RADIOSET, 9, "radio-set", TI.KEYWORD);
		add(RANDOM, 6, "random", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RAW, 3, "raw", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RAWTRANSFER, 12, "raw-transfer", TI.KEYWORD);
		add(RCODEINFORMATION, 10, "rcode-information", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(READ, 4, "read", TI.KEYWORD);
		add(READAVAILABLE, 14, "read-available", TI.KEYWORD | TI.RESERVED);
		add(READEXACTNUM, 14, "read-exact-num", TI.KEYWORD | TI.RESERVED);
		add(READKEY, 7, "readkey", TI.KEYWORD | TI.RESERVED);
		add(READONLY, 9, "read-only", TI.KEYWORD);
		add(REAL, 4, "real", TI.KEYWORD);
		add(RECID, 5, "recid", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(RECORDLENGTH, 10, "record-length", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RECORD_NAME, 0, "", TI.NO_FLAGS);
		add(RECTANGLE, 4, "rectangle", TI.KEYWORD | TI.RESERVED);
		add(RECURSIVE, 9, "recursive", TI.KEYWORD);
		add(REFERENCEONLY, 14, "reference-only", TI.KEYWORD);
		add(REJECTED, 8, "rejected", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RELATIONFIELDS, 15, "relation-fields", TI.KEYWORD);
		add(RELEASE, 7, "release", TI.KEYWORD | TI.RESERVED);
		add(REPEAT, 6, "repeat", TI.KEYWORD | TI.RESERVED);
		add(REPLACE, 7, "replace", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(REPLICATIONCREATE, 18, "replication-create", TI.KEYWORD);
		add(REPLICATIONDELETE, 18, "replication-delete", TI.KEYWORD);
		add(REPLICATIONWRITE, 17, "replication-write", TI.KEYWORD);
		add(REPOSITION, 10, "reposition", TI.KEYWORD | TI.RESERVED);
		add(REPOSITIONBACKWARD, 19, "reposition-backward", TI.KEYWORD | TI.RESERVED);
		add(REPOSITIONFORWARD, 18, "reposition-forward", TI.KEYWORD);
		add(REPOSITIONMODE, 15, "reposition-mode", TI.KEYWORD);
		add(REPOSITIONTOROW, 17, "reposition-to-row", TI.KEYWORD | TI.RESERVED);
		add(REPOSITIONTOROWID, 19, "reposition-to-rowid", TI.KEYWORD | TI.RESERVED);
		add(REQUEST, 7, "request", TI.KEYWORD);
		add(RESTARTROW, 11, "restart-row", TI.KEYWORD);
		add(RESULT, 6, "result", TI.KEYWORD);
		add(RETAIN, 6, "retain", TI.KEYWORD | TI.RESERVED);
		add(RETAINSHAPE, 8, "retain-shape", TI.KEYWORD);
		add(RETRY, 5, "retry", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(RETRYCANCEL, 12, "retry-cancel", TI.KEYWORD);
		add(RETURN, 6, "return", TI.KEYWORD | TI.RESERVED);
		add(RETURNS, 7, "returns", TI.KEYWORD);
		add(RETURNTOSTARTDIR, 18, "return-to-start-dir", TI.KEYWORD);
		add(RETURNVALUE, 10, "return-value", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(REVERSEFROM, 12, "reverse-from", TI.KEYWORD);
		add(REVERT, 6, "revert", TI.KEYWORD | TI.RESERVED);
		add(REVOKE, 6, "revoke", TI.KEYWORD | TI.RESERVED);
		add(RGBVALUE, 5, "rgb-value", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RIGHT, 5, "right", TI.KEYWORD);
		add(RIGHTALIGNED, 11, "right-aligned", TI.KEYWORD);
		add(RIGHTANGLE, 0, "", TI.NO_FLAGS);
		add(RIGHTBRACE, 0, "", TI.NO_FLAGS);
		add(RIGHTCURLY, 0, "", TI.NO_FLAGS);
		add(RIGHTPAREN, 0, "", TI.NO_FLAGS);
		add(RIGHTTRIM, 10, "right-trim", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(RINDEX, 7, "r-index", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(ROUND, 5, "round", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ROUNDED, 7, "rounded", TI.KEYWORD);
		add(ROUTINELEVEL, 13, "routine-level", TI.KEYWORD);
		add(ROW, 3, "row", TI.KEYWORD);
		add(ROWCREATED, 11, "row-created", TI.KEYWORD | TI.RESERVED);
		add(ROWDELETED, 11, "row-deleted", TI.KEYWORD | TI.RESERVED);
		add(ROWHEIGHTCHARS, 10, "row-height", TI.KEYWORD);
		add(ROWHEIGHTCHARS, 12, "row-height-chars", TI.EXTRA_LITERAL);
		add(ROWHEIGHTPIXELS, 12, "row-height-pixels", TI.KEYWORD);
		add(ROWID, 5, "rowid", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ROWMODIFIED, 12, "row-modified", TI.KEYWORD | TI.RESERVED);
		add(ROWOF, 6, "row-of", TI.KEYWORD);
		add(ROWSTATE, 9, "row-state", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(ROWUNMODIFIED, 14, "row-unmodified", TI.KEYWORD | TI.RESERVED);
		add(RULE, 4, "rule", TI.KEYWORD);
		add(RUN, 3, "run", TI.KEYWORD | TI.RESERVED);
		add(RUNPROCEDURE, 8, "run-procedure", TI.KEYWORD);
		add(SAVE, 4, "save", TI.KEYWORD | TI.RESERVED);
		add(SAVEAS, 7, "save-as", TI.KEYWORD);
		add(SAVECACHE, 9, "savecache", TI.KEYWORD);
		add(SAXATTRIBUTES, 14, "sax-attributes", TI.KEYWORD);
		add(SAXCOMPLETE, 10, "sax-complete", TI.KEYWORD | TI.RESERVED);
		add(SAXPARSERERROR, 16, "sax-parser-error", TI.KEYWORD | TI.RESERVED);
		add(SAXREADER, 10, "sax-reader", TI.KEYWORD);
		add(SAXRUNNING, 11, "sax-running", TI.KEYWORD | TI.RESERVED);
		add(SAXUNINITIALIZED, 17, "sax-uninitialized", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITER, 10, "sax-writer", TI.KEYWORD);
		add(SAXWRITEBEGIN, 15, "sax-write-begin", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITECOMPLETE, 18, "sax-write-complete", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITECONTENT, 17, "sax-write-content", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITEELEMENT, 17, "sax-write-element", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITEERROR, 15, "sax-write-error", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITEIDLE, 14, "sax-write-idle", TI.KEYWORD | TI.RESERVED);
		add(SAXWRITETAG, 13, "sax-write-tag", TI.KEYWORD | TI.RESERVED);
		add(SCHEMA, 6, "schema", TI.KEYWORD | TI.RESERVED);
		add(SCOPEDDEFINE, 0, "", TI.NO_FLAGS);
		add(SCREEN, 6, "screen", TI.KEYWORD | TI.RESERVED);
		add(SCREENIO, 9, "screen-io", TI.KEYWORD | TI.RESERVED);
		add(SCREENLINES, 12, "screen-lines", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(SCROLL, 6, "scroll", TI.KEYWORD | TI.RESERVED);
		add(SCROLLABLE, 10, "scrollable", TI.KEYWORD);
		add(SCROLLBARHORIZONTAL, 11, "scrollbar-horizontal", TI.KEYWORD);
		add(SCROLLBARVERTICAL, 11, "scrollbar-vertical", TI.KEYWORD);
		add(SCROLLING, 9, "scrolling", TI.KEYWORD);
		add(SDBNAME, 7, "sdbname", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(SEARCH, 6, "search", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(SEARCHSELF, 11, "search-self", TI.KEYWORD | TI.RESERVED);
		add(SEARCHTARGET, 13, "search-target", TI.KEYWORD | TI.RESERVED);
		add(SECTION, 7, "section", TI.KEYWORD);
		add(SECURITYPOLICY, 15, "security-policy", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(SEEK, 4, "seek", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(SELECT, 6, "select", TI.KEYWORD | TI.RESERVED);
		add(SELECTION, 9, "selection", TI.KEYWORD);
		add(SELECTIONLIST, 14, "selection-list", TI.KEYWORD);
		add(SELF, 4, "self", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(SEMI, 0, "", TI.NO_FLAGS);
		add(SEND, 4, "send", TI.KEYWORD);
		add(SENDSQLSTATEMENT, 8, "send-sql-statement", TI.KEYWORD);
		add(SEPARATECONNECTION, 19, "separate-connection", TI.KEYWORD);
		add(SEPARATORS, 10, "separators", TI.KEYWORD);
		add(SERIALIZABLE, 12, "serializable", TI.KEYWORD);
		add(SERIALIZEHIDDEN, 16, "serialize-hidden", TI.KEYWORD);
		add(SERIALIZENAME, 14, "serialize-name", TI.KEYWORD);
		add(SERVER, 6, "server", TI.KEYWORD);
		add(SERVERSOCKET, 13, "server-socket", TI.KEYWORD);
		add(SESSION, 7, "session", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(SET, 3, "set", TI.KEYWORD | TI.RESERVED);
		add(SETATTRCALLTYPE, 18, "set-attr-call-type", TI.KEYWORD | TI.RESERVED);
		add(SETBYTEORDER, 14, "set-byte-order", TI.KEYWORD);
		add(SETCONTENTS, 12, "set-contents", TI.KEYWORD);
		add(SETCURRENTVALUE, 17, "set-current-value", TI.KEYWORD);
		add(SETDBCLIENT, 13, "set-db-client", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SET_EFFECTIVE_TENANT, 20, "set-effective-tenant", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SETPOINTERVALUE, 15, "set-pointer-value", TI.KEYWORD);
		add(SETSIZE, 8, "set-size", TI.KEYWORD);
		add(SETUSERID, 7, "setuserid", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(SHA1DIGEST, 11, "sha1-digest", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SHARED, 6, "shared", TI.KEYWORD | TI.RESERVED);
		add(SHARELOCK, 5, "share-lock", TI.KEYWORD | TI.RESERVED);
		add(SHORT, 5, "short", TI.KEYWORD);
		add(SHOWSTATS, 9, "show-stats", TI.KEYWORD | TI.RESERVED);
		add(SIDELABELS, 8, "side-labels", TI.KEYWORD);
		add(SIGNATURE, 9, "signature", TI.KEYWORD);
		add(SILENT, 6, "silent", TI.KEYWORD);
		add(SIMPLE, 6, "simple", TI.KEYWORD);
		add(SINGLE, 6, "single", TI.KEYWORD);
		add(SINGLEQUOTE, 0, "", TI.NO_FLAGS);
		add(SIZE, 4, "size", TI.KEYWORD);
		add(SIZECHARS, 6, "size-chars", TI.KEYWORD);
		add(SIZEPIXELS, 6, "size-pixels", TI.KEYWORD);
		add(SKIP, 4, "skip", TI.KEYWORD | TI.RESERVED);
		add(SKIPDELETEDRECORD, 19, "skip-deleted-record", TI.KEYWORD | TI.RESERVED);
		add(SKIP_GROUP_DUPLICATES, 21, "skip-group-duplicates", TI.KEYWORD);
		add(SLASH, 0, "", TI.NO_FLAGS);
		add(SLIDER, 6, "slider", TI.KEYWORD);
		add(SMALLINT, 8, "smallint", TI.KEYWORD);
		add(SOAPHEADER, 11, "soap-header", TI.KEYWORD);
		add(SOAPHEADERENTRYREF, 20, "soap-header-entryref", TI.KEYWORD);
		add(SOCKET, 6, "socket", TI.KEYWORD);
		add(SOME, 4, "some", TI.KEYWORD | TI.RESERVED);
		add(SORT, 4, "sort", TI.KEYWORD);
		add(SOURCE, 6, "source", TI.KEYWORD);
		add(SOURCEPROCEDURE, 16, "source-procedure", TI.KEYWORD | TI.SYSHDL);
		add(SPACE, 5, "space", TI.KEYWORD | TI.RESERVED);
		add(SQL, 3, "sql", TI.KEYWORD);
		add(SQRT, 4, "sqrt", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SQSTRING, 0, "", TI.NO_FLAGS);
		add(SSLSERVERNAME, 15, "ssl-server-name", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(STAR, 0, "", TI.NO_FLAGS);
		add(START, 5, "start", TI.KEYWORD);
		add(STARTING, 8, "starting", TI.KEYWORD);
		add(STARTMOVE, 10, "start-move", TI.KEYWORD);
		add(STARTRESIZE, 12, "start-resize", TI.KEYWORD);
		add(STARTROWRESIZE, 16, "start-row-resize", TI.KEYWORD);
		add(STATIC, 6, "static", TI.KEYWORD);
		add(STATUS, 6, "status", TI.KEYWORD | TI.RESERVED);
		add(STATUSBAR, 10, "status-bar", TI.KEYWORD);
		add(STDCALL_KW, 7, "stdcall", TI.KEYWORD);
		add(STOMPDETECTION, 15, "stomp-detection", TI.KEYWORD | TI.RESERVED);
		add(STOMPFREQUENCY, 15, "stomp-frequency", TI.KEYWORD | TI.RESERVED);
		add(STOP, 4, "stop", TI.KEYWORD);
		add(STOPAFTER, 10, "stop-after", TI.KEYWORD);
		add(STOREDPROCEDURE, 11, "stored-procedure", TI.KEYWORD);
		add(STREAM, 6, "stream", TI.KEYWORD | TI.RESERVED);
		add(STREAMHANDLE, 13, "stream-handle", TI.KEYWORD | TI.RESERVED);
		add(STREAMIO, 9, "stream-io", TI.KEYWORD | TI.RESERVED);
		add(STRETCHTOFIT, 14, "stretch-to-fit", TI.KEYWORD);
		add(STRING, 6, "string", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(STRINGXREF, 11, "string-xref", TI.KEYWORD);
		add(SUBAVERAGE, 7, "sub-average", TI.KEYWORD);
		add(SUBCOUNT, 9, "sub-count", TI.KEYWORD);
		add(SUBMAXIMUM, 7, "sub-maximum", TI.KEYWORD);
		add(SUBMENU, 4, "sub-menu", TI.KEYWORD);
		add(SUBMENUHELP, 13, "sub-menu-help", TI.KEYWORD);
		add(SUBMINIMUM, 7, "sub-minimum", TI.KEYWORD);
		add(SUBSCRIBE, 9, "subscribe", TI.KEYWORD);
		add(SUBSTITUTE, 5, "substitute", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SUBSTRING, 6, "substring", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SUBTOTAL, 9, "sub-total", TI.KEYWORD);
		add(SUM, 3, "sum", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(SUMMARY, 7, "summary", TI.KEYWORD);
		add(SUPER, 5, "super", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC | TI.SYSHDL);
		add(SYMMETRICENCRYPTIONALGORITHM, 30, "symmetric-encryption-algorithm", TI.KEYWORD);
		add(SYMMETRICENCRYPTIONIV, 23, "symmetric-encryption-iv", TI.KEYWORD);
		add(SYMMETRICENCRYPTIONKEY, 24, "symmetric-encryption-key", TI.KEYWORD);
		add(SYMMETRICSUPPORT, 17, "symmetric-support", TI.KEYWORD);
		add(SYSTEMDIALOG, 13, "system-dialog", TI.KEYWORD | TI.RESERVED);
		add(SYSTEMHELP, 11, "system-help", TI.KEYWORD);
		add(Scanner_head, 0, "", TI.NO_FLAGS);
		add(Scanner_tail, 0, "", TI.NO_FLAGS);
		add(Sql_begins, 0, "", TI.NO_FLAGS);
		add(Sql_between, 0, "", TI.NO_FLAGS);
		add(Sql_comp_query, 0, "", TI.NO_FLAGS);
		add(Sql_in, 0, "", TI.NO_FLAGS);
		add(Sql_like, 0, "", TI.NO_FLAGS);
		add(Sql_null_test, 0, "", TI.NO_FLAGS);
		add(Sql_select_what, 0, "", TI.NO_FLAGS);
		add(TABLE, 5, "table", TI.KEYWORD | TI.RESERVED);
		add(TABLEHANDLE, 12, "table-handle", TI.KEYWORD | TI.RESERVED);
		add(TABLENUMBER, 12, "table-number", TI.KEYWORD | TI.RESERVED);
		add(TABLESCAN, 10, "table-scan", TI.KEYWORD);
		add(TARGET, 6, "target", TI.KEYWORD);
		add(TARGETPROCEDURE, 16, "target-procedure", TI.KEYWORD | TI.SYSHDL);
		add(TEMPTABLE, 10, "temp-table", TI.KEYWORD);
		add(TENANT, 6, "tenant", TI.KEYWORD);
		add(TENANT_ID, 9, "tenant-id", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(TENANT_NAME, 11, "tenant-name", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(TENANT_NAME_TO_ID, 17, "tenant-name-to-id", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(TENANT_WHERE, 12, "tenant-where", TI.KEYWORD);
		add(TERMINAL, 4, "term", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(TERMINAL, 8, "terminal", TI.EXTRA_LITERAL);
		add(TERMINATE, 9, "terminate", TI.KEYWORD);
		add(TEXT, 4, "text", TI.KEYWORD | TI.RESERVED);
		add(TEXTCURSOR, 11, "text-cursor", TI.KEYWORD | TI.SYSHDL);
		add(TEXTSEGGROW, 8, "text-seg-growth", TI.KEYWORD);
		add(THEN, 4, "then", TI.KEYWORD | TI.RESERVED);
		add(THISOBJECT, 11, "this-object", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(THISPROCEDURE, 14, "this-procedure", TI.KEYWORD | TI.RESERVED | TI.SYSHDL);
		add(THREED, 7, "three-d", TI.KEYWORD);
		add(THROUGH, 7, "through", TI.KEYWORD);
		add(THROUGH, 4, "thru", TI.EXTRA_LITERAL);
		add(THROW, 5, "throw", TI.KEYWORD);
		add(TICMARKS, 9, "tic-marks", TI.KEYWORD);
		add(TILDE, 0, "", TI.NO_FLAGS);
		add(TIME, 4, "time", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(TIMESTAMP, 9, "timestamp", TI.KEYWORD);
		add(TIMEZONE, 8, "timezone", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC | TI.MAY_BE_NO_ARG_FUNC);
		add(TITLE, 5, "title", TI.KEYWORD | TI.RESERVED);
		add(TO, 2, "to", TI.KEYWORD | TI.RESERVED);
		add(TODAY, 5, "today", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC);
		add(TOGGLEBOX, 10, "toggle-box", TI.KEYWORD);
		add(TOOLBAR, 8, "tool-bar", TI.KEYWORD);
		add(TOOLTIP, 7, "tooltip", TI.KEYWORD);
		add(TOP, 3, "top", TI.KEYWORD);
		add(TOPIC, 5, "topic", TI.KEYWORD);
		add(TOPNAVQUERY, 13, "top-nav-query", TI.KEYWORD);
		add(TOPONLY, 8, "top-only", TI.KEYWORD | TI.RESERVED);
		add(TOROWID, 8, "to-rowid", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(TOTAL, 5, "total", TI.KEYWORD);
		add(TRAILING, 5, "trailing", TI.KEYWORD);
		add(TRANSACTION, 5, "trans", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC);
		add(TRANSACTION, 8, "transaction", TI.EXTRA_LITERAL);
		add(TRANSACTIONMODE, 16, "transaction-mode", TI.KEYWORD);
		add(TRANSINITPROCEDURE, 20, "trans-init-procedure", TI.KEYWORD);
		add(TRANSPARENT, 8, "transparent", TI.KEYWORD);
		add(TRIGGER, 7, "trigger", TI.KEYWORD | TI.RESERVED);
		add(TRIGGERS, 8, "triggers", TI.KEYWORD | TI.RESERVED);
		add(TRIM, 4, "trim", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_REGULAR_FUNC);
		add(TRUE_KW, 4, "true", TI.KEYWORD | TI.RESERVED);
		add(TRUNCATE, 5, "truncate", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(TTCODEPAGE, 10, "ttcodepage", TI.KEYWORD);
		add(TYPE_NAME, 0, "", TI.NO_FLAGS);
		add(TYPELESS_TOKEN, 0, "", TI.NO_FLAGS);
		add(TYPEOF, 7, "type-of", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(UNARY_MINUS, 0, "", TI.NO_FLAGS);
		add(UNARY_PLUS, 0, "", TI.NO_FLAGS);
		add(UNBOX, 5, "unbox", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(UNBUFFERED, 6, "unbuffered", TI.KEYWORD);
		add(UNDERLINE, 6, "underline", TI.KEYWORD | TI.RESERVED);
		add(UNDO, 4, "undo", TI.KEYWORD | TI.RESERVED);
		add(UNFORMATTED, 6, "unformatted", TI.KEYWORD | TI.RESERVED);
		add(UNION, 5, "union", TI.KEYWORD | TI.RESERVED);
		add(UNIQUE, 6, "unique", TI.KEYWORD | TI.RESERVED);
		add(UNIQUEMATCH, 12, "unique-match", TI.KEYWORD);
		add(UNIX, 4, "unix", TI.KEYWORD | TI.RESERVED);
		add(UNKNOWNVALUE, 0, "", TI.NO_FLAGS);
		add(UNLESSHIDDEN, 13, "unless-hidden", TI.KEYWORD | TI.RESERVED);
		add(UNLOAD, 6, "unload", TI.KEYWORD);
		add(UNQUOTEDSTRING, 0, "", TI.NO_FLAGS);
		add(UNSIGNEDBYTE, 13, "unsigned-byte", TI.KEYWORD);
		add(UNSIGNEDSHORT, 14, "unsigned-short", TI.KEYWORD);
		add(UNSUBSCRIBE, 11, "unsubscribe", TI.KEYWORD);
		add(UP, 2, "up", TI.KEYWORD | TI.RESERVED);
		add(UPDATE, 6, "update", TI.KEYWORD | TI.RESERVED);
		add(URLDECODE, 10, "url-decode", TI.KEYWORD);
		add(URLENCODE, 10, "url-encode", TI.KEYWORD);
		add(USE, 3, "use", TI.KEYWORD);
		add(USEDICTEXPS, 7, "use-dict-exps", TI.KEYWORD);
		add(USEFILENAME, 12, "use-filename", TI.KEYWORD);
		add(USEINDEX, 9, "use-index", TI.KEYWORD | TI.RESERVED);
		add(USER, 4, "user", TI.KEYWORD | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(USEREVVIDEO, 12, "use-revvideo", TI.KEYWORD);
		add(USERID, 6, "userid", TI.KEYWORD | TI.RESERVED | TI.MAY_BE_NO_ARG_FUNC | TI.MAY_BE_REGULAR_FUNC);
		add(USER_FUNC, 0, "", TI.NO_FLAGS);
		add(USETEXT, 8, "use-text", TI.KEYWORD);
		add(USEUNDERLINE, 13, "use-underline", TI.KEYWORD);
		add(USEWIDGETPOOL, 15, "use-widget-pool", TI.KEYWORD);
		add(USING, 5, "using", TI.KEYWORD | TI.RESERVED);
		add(V6FRAME, 7, "v6frame", TI.KEYWORD | TI.RESERVED);
		add(VALIDATE, 8, "validate", TI.KEYWORD);
		add(VALIDEVENT, 11, "valid-event", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(VALIDHANDLE, 12, "valid-handle", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(VALIDOBJECT, 12, "valid-object", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(VALUE, 5, "value", TI.KEYWORD | TI.RESERVED);
		add(VALUECHANGED, 13, "value-changed", TI.KEYWORD | TI.RESERVED);
		add(VALUES, 6, "values", TI.KEYWORD | TI.RESERVED);
		add(VARIABLE, 3, "variable", TI.KEYWORD);
		add(VERBOSE, 4, "verbose", TI.KEYWORD);
		add(VERTICAL, 4, "vertical", TI.KEYWORD);
		add(VIEW, 4, "view", TI.KEYWORD | TI.RESERVED);
		add(VIEWAS, 7, "view-as", TI.KEYWORD | TI.RESERVED);
		add(VMS, 3, "vms", TI.KEYWORD);
		add(VOID, 4, "void", TI.KEYWORD);
		add(WAIT, 4, "wait", TI.KEYWORD);
		add(WAITFOR, 8, "wait-for", TI.KEYWORD | TI.RESERVED);
		add(WARNING, 7, "warning", TI.KEYWORD);
		add(WEBCONTEXT, 7, "web-context", TI.KEYWORD | TI.SYSHDL);
		add(WEEKDAY, 7, "weekday", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(WHEN, 4, "when", TI.KEYWORD | TI.RESERVED);
		add(WHERE, 5, "where", TI.KEYWORD | TI.RESERVED);
		add(WHILE, 5, "while", TI.KEYWORD | TI.RESERVED);
		add(WIDGET, 6, "widget", TI.KEYWORD);
		add(WIDGETHANDLE, 8, "widget-handle", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(WIDGETID, 9, "widget-id", TI.KEYWORD);
		add(WIDGETPOOL, 11, "widget-pool", TI.KEYWORD);
		add(WIDTH, 5, "width", TI.KEYWORD);
		add(WIDTHCHARS, 7, "width-chars", TI.KEYWORD);
		add(WIDTHPIXELS, 7, "width-pixels", TI.KEYWORD);
		add(WINDOW, 6, "window", TI.KEYWORD | TI.RESERVED);
		add(WINDOWDELAYEDMINIMIZE, 18, "window-delayed-minimize", TI.KEYWORD | TI.RESERVED);
		add(WINDOWMAXIMIZED, 12, "window-maximized", TI.KEYWORD | TI.RESERVED);
		add(WINDOWMINIMIZED, 12, "window-minimized", TI.KEYWORD | TI.RESERVED);
		add(WINDOWNAME, 11, "window-name", TI.KEYWORD);
		add(WINDOWNORMAL, 13, "window-normal", TI.KEYWORD | TI.RESERVED);
		add(WITH, 4, "with", TI.KEYWORD | TI.RESERVED);
		add(WORDINDEX, 10, "word-index", TI.KEYWORD);
		add(WORKTABLE, 8, "work-table", TI.KEYWORD | TI.RESERVED);
		add(WORKTABLE, 8, "workfile", TI.EXTRA_LITERAL);
		add(WRITE, 5, "write", TI.KEYWORD | TI.RESERVED);
		add(WS, 0, "", TI.NO_FLAGS);
		add(Widget_ref, 0, "", TI.NO_FLAGS);
		add(With_columns, 0, "", TI.NO_FLAGS);
		add(With_down, 0, "", TI.NO_FLAGS);
		add(X, 1, "x", TI.KEYWORD);
		add(XCODE, 5, "xcode", TI.KEYWORD | TI.RESERVED);
		add(XDOCUMENT, 10, "x-document", TI.KEYWORD);
		add(XMLDATATYPE, 13, "xml-data-type", TI.KEYWORD);
		add(XMLNODENAME, 13, "xml-node-name", TI.KEYWORD);
		add(XMLNODETYPE, 13, "xml-node-type", TI.KEYWORD);
		add(XNODEREF, 9, "x-noderef", TI.KEYWORD);
		add(XOF, 4, "x-of", TI.KEYWORD);
		add(XREF, 4, "xref", TI.KEYWORD | TI.RESERVED);
		add(XREFXML, 8, "xref-xml", TI.KEYWORD);
		add(Y, 1, "y", TI.KEYWORD);
		add(YEAR, 4, "year", TI.KEYWORD | TI.MAY_BE_REGULAR_FUNC);
		add(YES, 3, "yes", TI.KEYWORD | TI.RESERVED);
		add(YESNO, 6, "yes-no", TI.KEYWORD);
		add(YESNOCANCEL, 13, "yes-no-cancel", TI.KEYWORD);
		add(YOF, 4, "y-of", TI.KEYWORD);
	}


}
