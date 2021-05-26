package org.prorefactor.core;

import com.joanju.proparse.ProToken;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.prorefactor.refactor.RefactorException;

/**
 * Helper class to get text from nodes
 */
public class NodeTextUtils 
{
	private final JPNode rootNode;
	private JPNode currNode;
	private HashMap<Integer, JPNode> nodeMap;
	private HashMap<String, String> macros;
	private boolean hideIncludeFileText = false;
	private Boolean skipNextSpace = false;
	
	/**
	 * Constructor for the NodeTextUtils class
	 * @param top The node from which to start
	 */
	public NodeTextUtils(JPNode top)
	{
		this.rootNode = top;
		
		this.mapNodes();
		this.macros = new HashMap<String, String>();
	}
	
	/**
	 * Helper method to map all nodes for easier access
	 */
	private void mapNodes()
	{
		nodeMap = new HashMap<Integer, JPNode>();
		this.mapNodes(rootNode);
	}
	
	/**
	 * Recursive worker method for mapNodes()
	 * @param node current node
	 */
	private void mapNodes(JPNode node)
	{
		if(!nodeMap.containsKey(node.getNodeNum()))
			nodeMap.put(node.getNodeNum(), node);
		
		if(node.firstChild() != null)
			this.mapNodes(node.firstChild());
		if(node.nextSibling() != null)
			this.mapNodes(node.nextSibling());
	}
	
	/**
	 * Returns the following node or null if EOF
	 * @param node The current node
	 * @return The next node
	 */
	private JPNode getNextNode(JPNode node)
	{
		if(node == null || !nodeMap.containsKey(node.getNodeNum() + 1))
			return null;
		return nodeMap.get(node.getNodeNum() + 1);
	}
	
	/**
	 * Get the full source text from a node.
	 * When run on top node, the result is the source-code.
	 * Conditional compilation causes a RefactorException as
	 * it currently can not be processed.
	 * @return The text as it is in the source file
	 * @throws RefactorException If conditional compilation is found (&IF..&THEN..&ENDIF etc.).
	 */
	public String getFullSourceText() throws RefactorException
	{
		StringBuilder bldr = new StringBuilder();
		String txt;
		
		this.currNode = this.rootNode;
		this.hideIncludeFileText = true;
				
		for(this.currNode  = this.rootNode; 
			this.currNode != null; 
			this.currNode  = this.getNextNode(this.currNode))
			bldr.append(this.getText(this.currNode));
		
		txt = this.replaceMacros(bldr.toString());
		
		this.hideIncludeFileText = false;
		return txt;
	}
	
	/**
	 * Replaces macro-text with their references
	 * @param txt The source code with macro-text
	 * @return The source code with macro-references
	 */
	private String replaceMacros(String txt)
	{
		String pattern;
		
		for(String key : macros.keySet())
		{
			pattern = macros.get(key);
			pattern = this.fixRegexEscape(pattern);

			txt = txt.replaceAll(pattern, key);
		}
		
		return txt;
	}
	
	/**
	 * Excepes characters used by regex
	 * @param txt The pattern for regex
	 * @return The fixed pattern
	 */
	private String fixRegexEscape(String txt)
	{
		txt = txt.replace("\\", "\\\\");
		txt = txt.replace("|", "\\|");
		txt = txt.replace("{", "\\{");
		txt = txt.replace("}", "\\}");
		txt = txt.replace("[", "\\[");
		txt = txt.replace("]", "\\]");
		txt = txt.replace("(", "\\(");
		txt = txt.replace(")", "\\)");
		txt = txt.replace("$", "\\$");
		txt = txt.replace("^", "\\^");
		txt = txt.replace("?", "\\?");
		txt = txt.replace("*", "\\*");
		txt = txt.replace("+", "\\+");
		txt = txt.replace(".", "\\.");
		
		return txt;
	}
	
	/**
	 * Returns a nodes text including hidden text, 
	 * also brings operators in the correct order
	 * @param node The node whose text is sought
	 * @return The nodes text
	 * @throws RefactorException If conditional compilation is found (&IF..&THEN..&ENDIF etc.).
	 */
	private String getText(JPNode node) throws RefactorException
	{
		StringBuilder bldr = new StringBuilder();
		
		if (   node.getType() == TokenTypes.PLUS
			|| node.getType() == TokenTypes.MINUS
			|| node.getType() == TokenTypes.MULTIPLY
			|| node.getType() == TokenTypes.DIVIDE
			|| node.getType() == TokenTypes.MODULO
			|| node.getType() == TokenTypes.EQUAL
			|| node.getType() == TokenTypes.OR
			|| node.getType() == TokenTypes.AND
			|| node.getType() == TokenTypes.LTHAN
			|| node.getType() == TokenTypes.GTHAN
			|| node.getType() == TokenTypes.LE
			|| node.getType() == TokenTypes.GE
			|| node.getType() == TokenTypes.EQ
			|| node.getType() == TokenTypes.NE
			|| node.getType() == TokenTypes.PLUS_EQUAL
			|| node.getType() == TokenTypes.MINUS_EQUAL
			|| node.getType() == TokenTypes.DIVIDE_EQUAL
			|| node.getType() == TokenTypes.MULTIPLY_EQUAL)
		{
			if(this.hideIncludeFileText && node.getFileIndex() == 0)
			{
				bldr.append(getOperatorText(node));
				this.currNode = this.skipNodeChildren(node);
			}
		}
		else
		{
			bldr.append(getHiddenText(node));
			if(this.hideIncludeFileText && node.getFileIndex() == 0)
				bldr.append(node.getText());
			this.currNode = node;
		}
		return bldr.toString();
	}
	
	/**
	 * Returns the last node descending from the given node
	 * @param node The given node
	 * @return The last node descending from node
	 */
	private JPNode skipNodeChildren(JPNode node)
	{
		if(node.lastDescendant() != null)
			return node.lastDescendant();
		else
			return node;
	}
	
	/**
	 * Returns an operators text in the correct order
	 * @param node The operator node
	 * @return The nodes text
	 * @throws RefactorException If conditional compilation is found (&IF..&THEN..&ENDIF etc.).
	 */
	private String getOperatorText(JPNode node) throws RefactorException
	{
		StringBuilder bldr = new StringBuilder();
		JPNode begin = null;
		JPNode end = null;
		
		if(node.firstChild() == null)
		{
			bldr.append(this.getHiddenText(node));
			bldr.append(node.getText());
			return bldr.toString();
		}
		
		// Get the text on the left side of the operator
		begin = node.firstChild();
		if(begin.lastDescendant() != null)
			end = begin.lastDescendant();
		else
			end = begin;
		bldr.append(this.getText(begin, end));
		
		// Get the operators text
		bldr.append(this.getHiddenText(node));
		bldr.append(node.getText());

		// Get the text on the right side of the operator
		begin = node.firstChild().nextSibling();
		if(begin.lastDescendant() != null)
			end = begin.lastDescendant();
		else
			end = begin;
		bldr.append(getText(begin, end));
		
		return bldr.toString();
	}
	
	/**
	 * Returns the text between two nodes
	 * @param begin The node from which to begin
	 * @param end The node at which to end
	 * @return The Text between begin and end
	 * @throws RefactorException If conditional compilation is found (&IF..&THEN..&ENDIF etc.).
	 */
	private String getText(JPNode begin, JPNode end) throws RefactorException
	{
		StringBuilder bldr = new StringBuilder();
		
		for(this.currNode = begin; 
			this.currNode.getNodeNum() <= end.getNodeNum(); 
			this.currNode = this.getNextNode(this.currNode))
			bldr.append(this.getText(this.currNode));
			
		return bldr.toString();
	}
	
	/**
	 * Returns the text of a nodes hidden tokens
	 * @param node The node whose hidden text is sought
	 * @return The nodes hidden text
	 * @throws RefactorException If conditional compilation is found (&IF..&THEN..&ENDIF etc.).
	 */
	private String getHiddenText(JPNode node) throws RefactorException
	{
		StringBuilder bldr = new StringBuilder();
		JSONObject json;
		
		for (ProToken t = node.getHiddenFirst(); t!=null; t = t.getNext()) 
		{
			if(t.getFileIndex() == 0 && this.hideIncludeFileText)
			{
				if(t.getType() == TokenTypes.CONDITIONALCOMPILATION)
					throw new RefactorException("The method JPNode.fullSourceText() does not support conditional compilation (&IF ... &THEN ... &ELSE)!");

				if(t.getType() == TokenTypes.MAKROREFERENCE)
				{
					try
					{
						json = new JSONObject(t.getText());
						if(!this.macros.containsKey(json.getString("refName")))
							this.macros.put(json.getString("refName"), json.getString("refName") + json.getString("refText"));

						bldr.append(json.getString("refName"));
					}
					catch(JSONException e)
					{
						bldr.append(t.getText());
					}
				}
				
				else if(skipNextSpace)
				{
					if(t.getType() == TokenTypes.WS)
						bldr.append(t.getText().substring(1));
					else
						bldr.append(t.getText());
					skipNextSpace = false;
				}
				else
					bldr.append(t.getText());
				if(t.getType() == TokenTypes.INCLUDEFILEREFERENCE)
					skipNextSpace = true;
			}
		}
		
		return bldr.toString();
	}
}
