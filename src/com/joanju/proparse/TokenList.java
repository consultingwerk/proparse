/*
TokenList.java

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;

import antlr.TokenStreamException;

import java.util.ArrayList;
import java.io.StringWriter;


/** List of tokens for input to the parser.
 * This class is responsible for gathering a list of tokens from the postlexer,
 * and examining that list for fine-tuning before sending it on to the parser.
 * The complete tool chain is:<br/>
 * preprocessor-lexer-postlexer-tokenlist-filter-parser
 */
public class TokenList implements antlr.TokenStream, ProParserTokenTypes {


	TokenList(antlr.TokenStream input) {
		this.input = input;
	}

	int currentPosition = 0;
	int listSize;
	antlr.TokenStream input;
	ArrayList<ProToken> list = new ArrayList<ProToken>();



	public void build() throws TokenStreamException {
		for(;;) {
			ProToken nextToken = (ProToken) input.nextToken();
			list.add(nextToken);
			if (nextToken.getType()==OBJCOLON)
				reviewObjcolon();
			if (nextToken.getType()==EOF)
				break;
		}
		listSize = list.size();
	}


	public ProToken nextToken() throws TokenStreamException {
		if (currentPosition >= listSize)
			return new ProToken(null, EOF, "");
		return list.get(currentPosition++);
	}


	/** Review the token list at an OBJCOLON token.
	 * This is the reason this class was created in the first place.
	 * If we have an OBJCOLON, what comes before it has to be one of a few things:
	 *   a system handle,
	 *   a handle expression,
	 *   an Object reference expression, or
	 *   a type name (class name) for a static member reference.
	 * <p>
	 * A type name can be pretty much anything, even a reserved keyword.
	 * It can also be a qualified class name, such as com.joanju.Foo.
	 * <p>
	 * This method attempts to resolve the following problem:
	 * Because of static class member references, a class name can be the first
	 * token in an expression. Class names can be composed of reserved keywords.
	 * This means that a reserved keyword could be the first piece of an expression,
	 * and this completely breaks the lookahead in the ANTLR generated parser.
	 * So, here, we look for OBJCOLON, and make sure that what comes before it is
	 * a system handle, an ID, or a non-reserved keyword.
	 * <p>
	 * A NAMEDOT token is a '.' followed by anything other than whitespace.
	 * If the OBJCOLON is proceeded by a NAMEDOT pair (NAMEDOT followed by anything),
	 * then we convert all of the NAMEDOT pairs to NAMEDOT-ID pairs.
	 * Otherwise, if the OBJCOLON is proceeded by any reserved keyword other than a
	 * systemhandlename, then we change that token's type to ID.
	 */
	private void reviewObjcolon() {

		int colonIndex = list.size() - 1;
		int lastIndex = colonIndex - 1;

		// There may be whitespace in front of an OBJCOLON.
		int ttype = list.get(lastIndex).getType();
		while (ttype==WS || ttype==COMMENT) {
			ttype = list.get(--lastIndex).getType();
		}

		// Look for NAMEDOT pairs.
		// Actually, it's not that easy. Something like:
		//     newsyntax.101b.deep.FindMe
		// is perfectly valid, and because of the digit following the '.',
		// one part of that name gets picked up as a token with text ".101b".
		int index = lastIndex;
		boolean foundNamedot = false;
		for(;;) {
			if (index==0)
				break;
			int currType = list.get(index).getType();
			if (currType==WS || currType==COMMENT) {
				// There can be space in front of a NAMEDOT in a table or field name.
				// We don't want to fiddle with those here.
				return;
			}
			if (list.get(index-1).getType() == NAMEDOT) {
				index = index - 2;
			} else if(list.get(index).getText().length() > 0 && list.get(index).getText().charAt(0)=='.') {
				index = index - 1;
			} else {
				break;
			}
			foundNamedot = true;
		}
		if (foundNamedot) {
			// Now merge all the parts into one ID token.
			ProToken token = list.get(index);
			token.setType(ID);
			StringWriter text = new StringWriter();
			text.append(token.getText());
			int drop = index + 1;
			for (int i = 0; i < lastIndex - index; i++) {
				text.append(list.get(drop).getText());
				list.remove(drop);
			}
			token.setText(text.toString());
			return;
		}

		// Not namedotted, so if it's reserved and not a system handle, convert to ID.
		ttype = list.get(index).getType();
		if (NodeTypes.isReserved(ttype) && (! NodeTypes.isSystemHandleName(ttype)))
			list.get(index).setType(ID);
	}


}
