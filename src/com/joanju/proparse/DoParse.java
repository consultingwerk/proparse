/*
DoParse.java

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import org.prorefactor.core.TokenTypes;
import org.prorefactor.core.JPNode;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import antlr.TokenStreamException;
import antlr.RecognitionException;

public class DoParse {

	public DoParse(String fileName) {
		this(fileName, null, null);
	}
	
	public DoParse(String fileName, String inputContent) {
		this(fileName, inputContent, null);
	}

	DoParse(String fileName, DoParse primary) {
		this(fileName, null, primary);
	}
	
	DoParse(String fileName, String inputContent, DoParse primary) {
		this.fileName = fileName;
		this.inputContent = inputContent;
		this.primary = primary;
	}

	private DoParse primary = null;

	// instance members
	antlr.TokenStreamHiddenTokenFilter filter;

	boolean justLex = false;
	boolean proEval = false;
	boolean preProcessCondition = false;
	boolean preProcessConditionResult = false;
	int nextNodeNum;
	BufferedReader inStream;
	private Environment env = Environment.instance();
	ProParser parser;
	String fileName;
	String inputContent;
	TokenVectorIterator tvi;

	IntegerIndex<String> filenameList = new IntegerIndex<String>();


	class TokenVectorIterator implements antlr.TokenStream {
		TokenVectorIterator(ArrayList<ProToken> tokenVector, DoParse doParse) {
			this.tokenVector = tokenVector;
			this.doParse = doParse;
		}
		DoParse doParse;
		ArrayList<ProToken> tokenVector = new ArrayList<ProToken>();
		int it = 0;
		public antlr.Token nextToken() {
			if (it >= tokenVector.size())
				return new ProToken(filenameList, TokenTypes.EOF, "");
			return tokenVector.get(it++);
		}
	}




	int addFilename(String filename) {return filenameList.add(filename);}


	/** Set parent and prevSibling links, as well as nodeNum.
	 * Caller is responsible for setting nodeNum of input node,
	 * as well as nextNodeNum value.
	 */
	protected void backLinkAndNodeNum(JPNode r) {
		JPNode currNode = r.firstChild();
		while (currNode!=null) {
			currNode.setNodeNum(nextNodeNum++);
			currNode.setParent(r);
			backLinkAndNodeNum(currNode);
			JPNode nextNode = currNode.nextSibling();
			if (nextNode!=null)
				nextNode.setPrevSibling(currNode);
			currNode = nextNode;
		}
	}


	void doParse(ArrayList<ProToken> tokenVector)
			throws IOException, TokenStreamException, RecognitionException {
		tvi = new TokenVectorIterator(tokenVector, this);
		doParse();
	}

	public void doParse()
			throws IOException, TokenStreamException, RecognitionException {

		Reader inputReader = null;
		
		if (inputContent != null)
			inputReader = new StringReader(inputContent);
		else if (fileName != null) {
			try {
				/* SCL-3087: replaced FileReader with InputStreamReader to use the current codepage */
				inputReader = new InputStreamReader(new FileInputStream(fileName), Charset.forName(System.getProperty("file.encoding")));
			} catch (FileNotFoundException fe) {
				inputReader = new StringReader(fileName);
				fileName = "dummy.p";
			}
		}
		
		if (inputReader != null)
			inStream = new BufferedReader(inputReader);
		
		Preprocessor prepro = new Preprocessor(fileName, inStream, this);

		try {
			if (fileName!=null)
				addFilename(fileName);

			// Parsing either a token vector or else regular input stream
			if (tvi!=null) {
				filter = new antlr.TokenStreamHiddenTokenFilter(tvi);
			} else {
				if (primary==null && !justLex)
					prepro.initListing();
				Lexer lexer = new Lexer(prepro);
				Postlexer postlexer = new Postlexer(prepro, lexer, this);
				TokenList tokenlist = new TokenList(postlexer);
				tokenlist.build();
				filter = new antlr.TokenStreamHiddenTokenFilter(tokenlist);
			}

			// If we're just lexing, let's see the "hidden" tokens too.
			// Otherwise, filter.
			if (!justLex) {
				filter.hide(NodeTypes.WS);
				filter.hide(NodeTypes.COMMENT);
				filter.hide(NodeTypes.AMPMESSAGE);
				filter.hide(NodeTypes.INCLUDEFILEREFERENCE);
				filter.hide(NodeTypes.MAKROREFERENCE);
				filter.hide(NodeTypes.CONDITIONALCOMPILATION);
				filter.hide(NodeTypes.AMPANALYZESUSPEND);
				filter.hide(NodeTypes.AMPANALYZERESUME);
				filter.hide(NodeTypes.AMPGLOBALDEFINE);
				filter.hide(NodeTypes.AMPSCOPEDDEFINE);
				filter.hide(NodeTypes.AMPUNDEFINE);
			}
			
			// Create the parser, with the filter as the input.
			parser = new ProParser(filter);
			parser.init(this);

			if (justLex) {
				// Print a nice lexeme list.
				ProToken t;
				for (;;) {
					t = (ProToken) filter.nextToken();
					StringBuilder bldr = new StringBuilder();
					bldr
						.append(t.getLine())
						.append(" ")
						.append(t.getText())
						.append(" ")
						.append(NodeTypes.getTypeName(t.getType()))
						;
					System.out.println(bldr);
					if (t.getType()==antlr.Token.EOF_TYPE)
						break;
				}
				System.out.println("Done lexing.");
				return;
			}

			// Now parse the token stream
			// We might be:
			// - doing an eval of a Progress code chunk
			// - evaluating an &IF preprocessor condition
			// - just doing a regular parse
			if (proEval) {
				parser.program();
				ProEval proEval = new ProEval();
				proEval.program(parser.getAST());
			} else if (preProcessCondition) {
				parser.expression();
				ProEval proEval = new ProEval();
				preProcessConditionResult = proEval.preproIfEval(parser.getAST());
			} else {
				parser.program();
				JPNode topNode = (JPNode) parser.getAST();
				nextNodeNum = 0;
				topNode.setNodeNum(nextNodeNum++);
				backLinkAndNodeNum(topNode);
				// Deal with trailing hidden tokens
				JPNode.finalizeTrailingHidden((JPNode) parser.getAST());
			}

		} finally {
			if (prepro !=null) {
				// If we are listing, then we want to list all file indexes.
				if (prepro.listing) {
					int i = 0;
					while (isValidIndex(i)) {
						StringBuilder bldr = new StringBuilder();
						bldr
							.append("0 0 0 fileindex ")
							.append(i)
							.append(" ")
							.append(getFilename(i));
						prepro.listingStream.write(bldr.toString());
						prepro.listingStream.newLine();
						++i;
					}
				}
				// Tell the preprocessor we're done. Releases file handles, etc.
				prepro.parseComplete();
			}
			if (primary == null) {
				if (! env.isMultiParse() )
					env.clearSuperCache();
			}
			
			if (inStream != null)
				inStream.close();
		}
	}


	String getFilename(int fileIndex) {
		return filenameList.getValue(fileIndex);
	}


	/** A reference to the collection of filenames from the parse. */
	public IntegerIndex<String> getFilenameList() {return filenameList;}


	ParserSupport getParserSupport() {
		if (parser==null)
			return null;
		return parser.support;
	}


	public org.prorefactor.core.JPNode getTopNode() {
		return (JPNode) parser.getAST();
	}


	boolean isValidIndex(int index) {
		return filenameList.hasIndex(index);
	}


	public void setJustLex(boolean justLex) {
		this.justLex = justLex;
	}


}
