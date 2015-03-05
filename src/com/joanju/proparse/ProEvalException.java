/*
ProEvalException.java

ProEval exceptions

Copyright (C) 2001-2008 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package com.joanju.proparse;


public class ProEvalException extends RuntimeException {

	public ProEvalException() {
		super();
	}
	public ProEvalException(String message) {
		super(message);
	}
	public ProEvalException(String message, Throwable cause) {
		super(message, cause);
	}

	private StringBuilder moreMessage = new StringBuilder();
	String filename = "";
	int column = 0;
	int line = 0;




	void appendMessage(String s) {moreMessage.append(s);}


	@Override
	public String getMessage() {
		return super.getMessage() + moreMessage.toString();
	}

}
