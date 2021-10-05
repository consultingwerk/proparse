/*
Postlexer.java

This class deals with &IF conditions by acting
as a filter between the lexer and the parser.

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import antlr.TokenStreamException;
import antlr.RecognitionException;

import java.util.LinkedList;
import java.util.ArrayList;
import java.io.IOException;


public class Postlexer implements antlr.TokenStream, ProParserTokenTypes {

	Postlexer(Preprocessor prepro, Lexer lexer, DoParse doParse) {
		this.prepro = prepro;
		this.lexer = lexer;
		this.doParse = doParse;
		this.filenameList = doParse.getFilenameList();
	}
	
	private ProToken currToken;
	private DoParse doParse;
	private IntegerIndex<String> filenameList;
	private Lexer lexer;
	private Preprocessor prepro;
	private LinkedList<PreproIfState> preproIfVec = new LinkedList<PreproIfState>();

	class PreproIfState {
		boolean consuming = false;
		boolean done = false;
	}




	public ProToken nextToken() throws TokenStreamException {
		try {
			for (;;) {
	
				getNextToken();

				switch (currToken.getType()) {

				case AMPIF:
					preproIf();
					break; // loop again

				case AMPTHEN:
					// &then are consumed by preproIf()
					throwMessage("Unexpected &THEN");
					break;

				case AMPELSEIF:
					preproElseif();
					break; // loop again

				case AMPELSE:
					preproElse();
					break; // loop again

				case AMPENDIF:
					preproEndif();
					break; // loop again

				default:
					return currToken;

				}
			}
		} catch (Exception e) {
			// antlr.Token.nextToken() does not declare throw for anything
			// other than TokenStreamException.
			throw new RuntimeException(e);
		}
	}


	ProToken defined() throws IOException {
		// Progress DEFINED() returns a single digit: 0,1,2, or 3.
		// The text between the parens can be pretty arbitrary, and can
		// have embedded comments, so this calls a specific lexer function for it.
		// We may have macro usage inside the preprocessor string, which we need to skip
		// lexer.getAmpIfDefArg returns expansed name  
		getNextToken();
		if (currToken.getType() == WS)
			getNextToken();
		if (currToken.getType() != LEFTPAREN)
			throwMessage("Bad DEFINED function in &IF preprocessor condition");

		// in case the token starts with a preprocessor we skip it. need to get to preprocessed value, 
		for (;currToken.getType() == MAKROREFERENCE;)
			getNextToken();

		ProToken argToken = lexer.getAmpIfDefArg();

		// when preprocessor variable to check starts with a preprocessor include,
		// we take next one   
		for (;argToken.getType() == MAKROREFERENCE;)
			argToken = lexer.getAmpIfDefArg();

		// skip other preprocessors 
		for (;currToken.getType() == MAKROREFERENCE;)
			getNextToken();

		getNextToken();

		// consume last preprocessors
		for (;currToken.getType() == MAKROREFERENCE;){
			getNextToken();
		}		

		if (currToken.getType() != RIGHTPAREN)
			throwMessage("Bad DEFINED function in &IF preprocessor condition");
		return new ProToken(filenameList, NUMBER, prepro.defined(argToken.getText().trim().toLowerCase()));
	}


	void getNextToken() throws IOException {
		currToken = lexer.nextToken();
	}


	void listingLine(ProToken token, String text) throws IOException {
		if (! prepro.listing)
			return;
		StringBuilder bldr = listingToken(token);
		bldr.append(text);
		prepro.listingStream.write(bldr.toString());
		prepro.listingStream.newLine();
	}


	StringBuilder listingToken(ProToken token) {
		StringBuilder bldr = new StringBuilder();
		bldr.append(token.fileIndex)
			.append(" ")
			.append(token.getLine())
			.append(" ")
			.append(token.getColumn())
			.append(" ");
		return bldr;
	}


	// For consuming tokens that has been preprocessed out (&IF FALSE...)
	void preproconsume() throws IOException, TokenStreamException, RecognitionException {
		int thisIfLevel = preproIfVec.size();
		prepro.consuming++;
		while	(	thisIfLevel <= preproIfVec.size()
				&&	preproIfVec.get(thisIfLevel - 1).consuming
				) {
			getNextToken();
			switch (currToken.getType()) {
			case AMPIF:
				preproIf();
				break;
			case AMPELSEIF:
				preproElseif();
				break;
			case AMPELSE:
				preproElse();
				break;
			case AMPENDIF:
				preproEndif();
				break;
			case EOF:
				throwMessage("Unexpected end of input when consuming discarded &IF/&ELSEIF/&ELSE text");
				break;
			default:
				break;
			}
		}
		prepro.consuming--;
	}


	void preproIf()
			throws IOException, TokenStreamException, RecognitionException {
		// Preserve the currToken current position for listing, before evaluating the expression.
		// We can't just write to listing here, because the expression evaluation may
		// find macro references to list.
		StringBuilder bldr = listingToken(currToken);
		bldr.append("ampif ");
		PreproIfState preproIfState = new PreproIfState();
		preproIfVec.add(preproIfState);
		// Only evaluate if we aren't consuming from an outer &IF.
		boolean isTrue = preproIfCond(prepro.consuming==0);
		if (isTrue) {
			if (prepro.listing) {
				bldr.append("true");
				prepro.listingStream.write(bldr.toString());
				prepro.listingStream.newLine();
			}
			preproIfState.done = true;
		} else {
			if (prepro.listing) {
				bldr.append("false");
				prepro.listingStream.write(bldr.toString());
				prepro.listingStream.newLine();
			}
			preproIfState.consuming = true;
			preproconsume();
		}
	}



	void preproElse() throws IOException, TokenStreamException, RecognitionException {
		PreproIfState preproIfState = preproIfVec.getLast();
		if (! preproIfState.done) {
			preproIfState.consuming = false;
			listingLine(currToken, "ampelse ?");
		}
		else {
			if (! preproIfState.consuming) {
				listingLine(currToken, "ampelse true");
				preproIfState.consuming = true;
				preproconsume();
			}
			// else: already consuming. no change.
			listingLine(currToken, "ampelse ?");
		}
	}


	void preproElseif()
			throws IOException, TokenStreamException, RecognitionException {
		// Preserve the current position for listing, before evaluating the expression.
		// We can't just write to listing here, because the expression evaluation may
		// find macro references to list.
		StringBuilder bldr = listingToken(currToken);
		bldr.append("ampelseif ");
		boolean evaluate = true;
		// Don't evaluate if we're consuming from an outer &IF
		if (prepro.consuming - 1 > 0)
			evaluate = false;
		// Don't evaluate if we're already done with this &IF
		if (preproIfVec.getLast().done)
			evaluate = false;
		boolean isTrue = preproIfCond(evaluate);
		if (prepro.listing) {
			if (!evaluate) {
					bldr.append("?");
			} else {
				if (isTrue)
					bldr.append("true");
				else
					bldr.append("false");
			}
			prepro.listingStream.write(bldr.toString());
			prepro.listingStream.newLine();
		}
		PreproIfState preproIfState = preproIfVec.getLast();
		if (isTrue && (! preproIfState.done)) {
			preproIfState.done = true;
			preproIfState.consuming = false;
		} else {
			if (! preproIfState.consuming) {
				preproIfState.consuming = true;
				preproconsume();
			}
			// else: already consuming. no change.
		}
	}


	void preproEndif() throws IOException {
		listingLine(currToken, "ampendif");
		preproIfVec.removeLast();
	}


	boolean preproIfCond(boolean evaluate)
			throws IOException, TokenStreamException, RecognitionException {
		// Notes
		// An &IF here in this &IF condition is not legal. Progress would barf on it.
		// That allows us to simply use a global flag to watch for &THEN.

		ArrayList<ProToken> tokenVector = new ArrayList<ProToken>();
		boolean done = false;
		while (!done) {
			getNextToken();
			switch (currToken.getType()) {
			case EOF:
				throwMessage("Unexpected end of input after &IF or &ELSEIF");
				break;
			case AMPTHEN:
				done = true;
				break;
			case DEFINED:
				if (evaluate)
					// If not evaluating, just discard
					tokenVector.add(defined());
				break;
			case COMMENT:
			case WS:
			case PREPROCESSTOKEN:
				break;
			default:
				if (evaluate)
					// If not evaluating, just discard
					tokenVector.add(currToken);
			}
		}

		// If it's blank or the the evaluate argument is false, we don't evaluate
		if ((tokenVector.size()==0) || (!evaluate))
			return false;
		else {
			DoParse evalDoParse = new DoParse(null, doParse);
			evalDoParse.preProcessCondition = true;
			for (int i = 0; i < 4; i++) {
				tokenVector.add(new ProToken(filenameList, EOF, ""));
			}
			try {
				evalDoParse.doParse(tokenVector);
			} catch(ProEvalException e) {
				e.appendMessage(" Unable to evaluate &IF condition:");
				for (ProToken tok : tokenVector) {
					e.appendMessage(" " + tok.getText());
				}
				int theIndex = currToken.fileIndex;
				if (doParse.isValidIndex(theIndex))
					e.filename = doParse.getFilename(theIndex);
				e.line = currToken.getLine();
				e.column = currToken.getColumn();
				throw e;
			}
			return evalDoParse.preProcessConditionResult;
		}
	}


	void throwMessage(String theMessage) {
		int theIndex = currToken.fileIndex;
		if (doParse.isValidIndex(theIndex))
			throw new IllegalArgumentException(
				doParse.getFilename(theIndex)
				+ ":"
				+ currToken.getLine()
				+ " "
				+ theMessage
				);
		else
			throw new IllegalArgumentException(theMessage);
	}


}
