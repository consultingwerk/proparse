/**
 * Pstring.java
 * @author John Green
 * 24-Oct-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002 Joanju Limited.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.core;


/**
 * This class is for working with the text of Proparse's QSTRING nodes.
 * Proparse's QSTRING nodes contain the string literal, including the
 * delimiting quotation marks as well as any string attributes.
 * This class will allow us to easily fetch and work with things like
 * just the text portion, just the attributes portion, check if the
 * delimiting quotes are single-quotes or double-quotes, etc.
 */
public class Pstring {

	private char quote;
	private String text;
	private String attributes;



	/**
	 * Constructor - should generally only be constructed
	 * by passing in the results of parser.getNodeText()
	 */
	public Pstring(String quotedString) {
		quote = quotedString.charAt(0);
		int secondQuote = quotedString.lastIndexOf(quote);
		text = quotedString.substring(1, secondQuote);
		if (secondQuote < (quotedString.length() - 1))
			attributes = quotedString.substring(secondQuote + 1);
		else
			attributes = "";
	}
	
	
	/** Strip attributes and quotes, if quoted. */
	public static String dequote(String orig) {
		if (isQuoted(orig)) {
			Pstring pstring = new Pstring(orig);
			return pstring.justText().trim();
		} else {
			return orig;
		}
	}


	/** Get the string attributes, including the colon. */
	public String getAttributes() {return attributes;}

	/** Get the character quotation mark. */
	public char getQuote() {return quote;}

	/** Same as justText - get the text stripped of quotes and attributes. */
	public String getText() {return text;}
	
	
	/** Convenience method to check if the first character of a String is a quote character. */
	public static boolean isQuoted(String checkMe) {
		char c = checkMe.charAt(0);
		return c=='\'' || c=='"';
	}


	/** Is this string translatable?
	 * @return True if translatable
	 */
	public boolean isTrans() {
		return attributes.indexOf('U') < 0 && attributes.indexOf('u') < 0;
	}


	/** Just the text portion of the node's text - er - you know. */
	public String justText() {return text;}
	
	
	public void setAttributes(String attributes) {this.attributes = attributes;}

	public void setQuote(char quote) {this.quote = quote;}

	/** Set the text portion - the stuff in between the quotation marks. */
	public void setText(String text) {this.text = text;}


	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff
			.append(quote)
			.append(text)
			.append(quote)
			.append(attributes)
			;
		return buff.toString();
	}


}
