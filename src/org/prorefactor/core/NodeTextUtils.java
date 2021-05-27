package org.prorefactor.core;

import com.joanju.proparse.ProToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
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
	private JSONArray macros;
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
		this.macros = new JSONArray();
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
		int line;
		JSONObject json;
		ArrayList<String> lines;
		
		try
		{
			lines = this.stringToLines(txt);
			for(int i = this.macros.length() - 1; i >= 0; i--)
			{
				json = this.macros.getJSONObject(i);
				line = json.getInt("line");
				lines.set(line, this.replaceMacro(lines.get(line), json));
			}
			txt = this.linesToString(lines);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return txt;
	}
	
	/**
	 * Replaces a macros text with the reference
	 * @param txt The source code line with the macro-text
	 * @param macro The info about the macro as JSON
	 * @return The source code line with the macro-reference
	 * @throws JSONException 
	 */
	private String replaceMacro(String txt, JSONObject macro) throws JSONException
	{
		int col = macro.getInt("col");
		String name = macro.getString("refName");
		String text = this.fixRegexEscape(macro.getString("refText"));

		return (txt.substring(0, col) + txt.substring(col).replaceFirst(text, name));
	}
	
	/**
	 * Helper method to turn source-code into a list of lines
	 * @param txt The source code
	 * @return The list of lines
	 */
	private ArrayList<String> stringToLines(String txt)
	{
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br;
		
		try
		{
			br = new BufferedReader(new StringReader(txt));
			for(String line = br.readLine(); line != null; line = br.readLine())
				lines.add(line + "\n");
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return lines;
	}
	
	/**
	 * Escapes characters used by regex
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
	 * Helper method to turn a list of lines into source-code
	 * @param lines The list of lines
	 * @return The source-code
	 */
	private String linesToString(ArrayList<String> lines)
	{
		String txt = "";
		for(String line: lines)
			txt += line;
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
		
		// An EQUAL node does not necessarily have children
		// e.g. in assignments in annotations EQUAL is in a sibling
		// relationship with the sides of the assignment
		// before and after itself.
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
		
		// Iterating the nodes hidden tokens
		for (ProToken t = node.getHiddenFirst(); t!=null; t = t.getNext()) 
		{
			// Only the top-most file
			if((t.getFileIndex() == 0 || t.getType() == TokenTypes.MAKROREFERENCE) && this.hideIncludeFileText)
			{
				// &IF ... &THEN ... &ENDIF cannot be processed
				if(t.getType() == TokenTypes.CONDITIONALCOMPILATION)
					throw new RefactorException("The method JPNode.fullSourceText() does not support conditional compilation (&IF ... &THEN ... &ELSE)!");

				// Remember macros to replace them later
				if(t.getType() == TokenTypes.MAKROREFERENCE)
				{
					try
					{
						json = new JSONObject(t.getText());
						if(json.getInt("file") == 0)
							macros.put(json);
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
				}
				
				// Skip additional space after an include-file
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
