/** 11-Feb-07 by John Green
 * Copyright (C) 2007 Joanju Software. All rights reserved.
 */
package org.prorefactor.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.prorefactor.refactor.RefactorException;

import antlr.CommonHiddenStreamToken;
import antlr.Token;

import com.joanju.proparse.NodeTypes;
import com.joanju.proparse.ProParserTokenTypes;
import com.joanju.proparse.ProToken;


/** Utilities for working with a JPNode syntax tree. */
public class TreeUtils {

	private ArrayList<JPNode> nodeList;
	

	
	/** Get a flat list of nodes, with operator nodes moved inline. */
	public static ArrayList<JPNode> flatList(JPNode top) {
		return new FlatListBuilder().build(top);
	}
	/** This variant is primarily for ease of use from ABL. */
	public static JPNode [] flatListAsArray(JPNode top) {
		ArrayList<JPNode> list = flatList(top);
		JPNode [] ret = new JPNode[list.size()];
		list.toArray(ret);
		return ret;
	}
	private static class FlatListBuilder {
		ArrayList<JPNode> list = new ArrayList<JPNode>();
		ArrayList<JPNode> build(JPNode node) {
			walker(node, false);
			return list;
		}
		private void walker(JPNode node, boolean showSiblings) {
			if (node==null) return;
			if (node.attrGet(IConstants.OPERATOR) == IConstants.TRUE) {
				walker(node.firstChild(), false);
				list.add(node);
				walker(node.firstChild().nextSibling(), true);
			} else {
				list.add(node);
				walker(node.firstChild(), true);
			}
			if (showSiblings) walker(node.nextSibling(), true);
		}
	}


	/** Get the full, preprocessed text from a node.
	 * When run on top node, the result is very comparable to COMPILE..PREPROCESS.
	 * This is the same as the old C++ Proparse API writeNode().
	 * Also see org.joanju.proparse.Iwdiff.
	 */
	public static String fullPreproText(JPNode top) {
		ArrayList<JPNode> list = flatList(top);
		StringBuilder bldr = new StringBuilder();
		for (JPNode node : list) {
			for (ProToken t = node.getHiddenFirst(); t!=null; t = t.getNext()) {
				int type = t.getType();
				if (type==TokenTypes.COMMENT || type==TokenTypes.WS)
					bldr.append(t.getText());
			}
			bldr.append(node.getText());
		}
		return bldr.toString();
	}
	
	/**
	 * Get the full source text from a node.
	 * When run on top node, the result is the source-code.
	 * Conditional compilation causes a RefactorException as
	 * it currently can not be processed.
	 */
	public static String fullSourceText(JPNode top) throws RefactorException {
		ArrayList<JPNode> list = flatList(top);
		StringBuilder bldr = new StringBuilder();
		Boolean skipNode = false;
		Boolean skipNextSpace = false;

		for (JPNode node : list) 
		{
			for (ProToken t = node.getHiddenFirst(); t!=null; t = t.getNext()) 
			{
				if(t.getFileIndex() == 0)
				{
					if(t.getType() == ProParserTokenTypes.MAKROREFERENCE)
						skipNode = true;
					if(t.getType() == ProParserTokenTypes.CONDITIONALCOMPILATION)
						throw new RefactorException("The method JPNode.fullSourceText() does not support conditional compilation (&IF ... &THEN ... &ELSE)!");

					if(skipNextSpace)
					{
						if(t.getType() == ProParserTokenTypes.WS)
							bldr.append(t.getText().substring(1));
						else
							bldr.append(t.getText());
						skipNextSpace = false;
					}
					else
						bldr.append(t.getText());
					if(t.getType() == ProParserTokenTypes.INCLUDEFILEREFERENCE)
						skipNextSpace = true;
				}
			}
			if(!skipNode)
			{
				if(node.getFileIndex() == 0)
					bldr.append(node.getText());
			}
			else
				skipNode = false;
		}
		
		return bldr.toString();
	}
	
	/** Get an array of nodes, such that the array index matches the node number. */
	public static JPNode [] nodeArray(JPNode top) {
		TreeUtils instance = new TreeUtils();
		instance.nodeList = new ArrayList<JPNode>();
		instance.nodeArray2(top);
		JPNode [] ret = new JPNode[instance.nodeList.size()];
		return instance.nodeList.toArray(ret);
	}
	private void nodeArray2(JPNode node) {
		if (node==null) return;
		nodeList.add(node);
		nodeArray2(node.firstChild());
		nodeArray2(node.nextSibling());
	}
	


}
