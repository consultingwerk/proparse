/** 18-Dec-06 by John Green
 * Copyright (C) 2006 Joanju Software. All rights reserved.
 */
package org.prorefactor.reports;

import java.io.BufferedWriter;

import org.prorefactor.core.IConstants;
import org.prorefactor.core.JPNode;
import org.prorefactor.core.TokenTypes;
import org.prorefactor.core.schema.Field;
import org.prorefactor.io.LogWriter;
import org.prorefactor.nodetypes.FieldRefNode;
import org.prorefactor.treeparser.FieldBuffer;
import org.prorefactor.treeparser.ParseUnit;
import org.prorefactor.treeparser.Symbol;

public class HowUsed {

	public HowUsed(BufferedWriter reportWriter, LogWriter log, Field reportField) {
		this.reportWriter = reportWriter;
		this.log = log;
		this.reportField = reportField;
	}
	
	BufferedWriter reportWriter;
	Field reportField;
	LogWriter log;
	
	
	public void report(ParseUnit pu) throws Exception {
		walkTree(pu.getTopNode());
	}
	
	private void walkTree(JPNode node) throws Exception {
		if (node==null) return;
		boolean match = false;
		if (node instanceof FieldRefNode) {
			Symbol symbol = node.getSymbol();
			if (	symbol!=null
				&&	symbol instanceof FieldBuffer
				&&	((FieldBuffer)symbol).getField() == reportField
				)
				match = true;
		}
		if (match) {
			printReference(node);
		} else {
			walkTree(node.firstChild());
		}
		walkTree(node.nextSibling());
	}
	

	private void printReference(JPNode node) throws Exception {
		JPNode stateHead = node.parent();
		while (stateHead.isStateHead()==false) stateHead = stateHead.parent();

		reportWriter.newLine();
		print4(stateHead, stateHead.getType());
		int state2 = stateHead.getState2();
		if (state2!=0) {
			reportWriter.write(" ");
			reportWriter.write(TokenTypes.getDefaultText(state2).toLowerCase());
		}
		reportWriter.write(" ... ");
		
		print2(node.parent(), false);
		reportWriter.newLine();
	}
	
	
	private void print2(JPNode node, boolean writeSiblings) throws Exception {
		if (node==null) return;
		JPNode firstChild = node.firstChild();
		JPNode nextSibling = node.nextSibling();
		if (node.attrGet(IConstants.OPERATOR) == IConstants.TRUE) {
			print2(firstChild, false);
			print3(node);
			print2(firstChild.nextSibling(), true);
		} else {
			print3(node);
			print2(firstChild, true);
		}
		if (writeSiblings) print2(nextSibling, true);
	}
	
	
	protected void print3(JPNode node) throws Exception {
		int type = node.getType();
		if (! TokenTypes.isNatural(type)) return;
		if
			(	type!=TokenTypes.PERIOD
			&&	type!=TokenTypes.LEXCOLON
			&&	type!=TokenTypes.OBJCOLON
			&&	node.prevNode().getType() != TokenTypes.OBJCOLON
			) {
			reportWriter.write(" ");
		}
		print4(node, type);
	}


	protected void print4(JPNode node, int type) throws Exception {
		if (TokenTypes.hasDefaultText(type)) {
			reportWriter.write(TokenTypes.getDefaultText(type).toLowerCase());
			return;
		}
		reportWriter.write(node.getText());
	}
	
	
}
