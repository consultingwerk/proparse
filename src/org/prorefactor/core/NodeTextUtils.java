package org.prorefactor.core;

import com.joanju.proparse.ConditionalCompilationToken;
import com.joanju.proparse.MakroReferenceToken;
import com.joanju.proparse.ProToken;

import de.consultingwerk.proparse.refactor.RefactoredToken;

import java.util.ArrayList;

import org.prorefactor.refactor.RefactorException;

/**
 * Helper class to get text from nodes
 */
public class NodeTextUtils 
{
	private final JPNode rootNode;
	
	/**
	 * Constructor for the NodeTextUtils class
	 * @param top The node from which to start
	 */
	public NodeTextUtils(JPNode top)
	{
		this.rootNode = top;
	}
	
	/**
	 * Get the full source text from a node.
	 * When run on top node, the result is the source-code.
	 * Conditional compilation causes a RefactorException as
	 * it currently can not be processed.
	 * @return The text as it is in the source file
	 * @throws RefactorException If an error occurred
	 */
	public String getFullSourceText() throws RefactorException
	{
		ArrayList<Object> flatTree = new ArrayList<Object>();
		StringBuilder bldr;
		Object treeNode;
		ProToken token, token2;
		JPNode node;
		ConditionalCompilationToken ccToken = null;
		ArrayList<ProToken> processedTokens = new ArrayList<ProToken>();
		ArrayList<JPNode> processedNodes = new ArrayList<JPNode>();
		
		this.flattenTree (this.rootNode, flatTree);

		// replace makros
		for (int i = flatTree.size() - 1; i >= 0; i--)
		{
			treeNode = flatTree.get(i);
			if (treeNode instanceof MakroReferenceToken)
			{
				this.replaceMakro ((MakroReferenceToken) treeNode, flatTree);
				i--;
			}
		}
		
		// process conditional compilation
		for (Object obj: flatTree)
		{			
			// &IF .. &THEN .. &ELSEIF .. &ELSE .. &ENDIF
			if (obj instanceof ConditionalCompilationToken)
			{
				ccToken = (ConditionalCompilationToken) obj;
				if (ccToken.getFileIndex() == 0 && !processedTokens.contains(ccToken) && ccToken.isOpening())
					flatTree.set(flatTree.indexOf(ccToken), new RefactoredToken(ccToken, ccToken.getEnclosedText(0, processedTokens)));
			}
			else if (obj instanceof JPNode)
			{
				node = (JPNode) obj;
				if (processedTokens.contains(node.getToken())) 
					processedNodes.add(node);
			}
		}
		
		for (ProToken processedToken: processedTokens)
		{
			if (flatTree.contains(processedToken))
				flatTree.remove(processedToken);
		}
		for (JPNode processedNode: processedNodes)
		{
			if (flatTree.contains(processedNode))
				flatTree.remove(processedNode);
		}
		
		// Since whitespaces at the end of include files 
		// and after includefile references get combined 
		// as a token that is part of the include file,
		// those whitespaces need to be added to the reference text.
		for (int i = 0; i < flatTree.size() - 1; i++) 
		{
			treeNode = flatTree.get(i);
			if (treeNode instanceof ProToken && ((ProToken) treeNode).getType() == TokenTypes.INCLUDEFILEREFERENCE)
			{
				token = (ProToken) treeNode;				
				if(token.getHiddenAfter() != null && token.getHiddenBefore() != null)
				{
					if ((i + 1 < flatTree.size() - 1))
					{
						if (flatTree.get(i + 1) instanceof JPNode)
							token2 = ((JPNode) flatTree.get(i + 1)).getToken();
						else if (flatTree.get(i + 1) instanceof ProToken)
							token2 = (ProToken) flatTree.get(i + 1);
						else
							token2 = null;
						
						if (token2 != null && token2.getHiddenBefore() != null && token2.getHiddenBefore().getType() == TokenTypes.WS)
							flatTree.add(flatTree.indexOf(token) + 1, token2.getHiddenBefore());
					}
				}
			}
		}
		
		for (int i = 0; i < flatTree.size() - 2; i++)
		{
			token = this.getToken(flatTree.get(i));
			token2 = this.getToken(flatTree.get(i + 1));
			if (token.getType() == TokenTypes.WS && token2.getType() == TokenTypes.WS)
			{
				flatTree.remove(token);
				i--;
			}
		}
		for (int i = 0; i < flatTree.size() - 1; i++) 
		{
			token = this.getToken(flatTree.get(i));
			if (token != null && token.getType() == TokenTypes.INCLUDEFILEREFERENCE && (i + 1) < flatTree.size())
			{
				token2 = this.getNextTokenWithText(token, flatTree);		
				if (token2 != null && token2.getType() == TokenTypes.WS && token2.getText().startsWith(" "))
					token2.setText(token2.getText().substring(1));
			}
		}
		
		bldr = new StringBuilder ();
		for (Object obj: flatTree) 
		{
			if (obj instanceof JPNode)
				bldr.append(((JPNode)obj).getText());
			else if (obj instanceof ProToken)
				bldr.append(((ProToken)obj).getText());
		}
		
		return bldr.toString();
	}
	
	/**
	 * Returns the next token in the tree that does have text
	 * @param token The current token
	 * @param tree The list of tokens in which to search
	 * @return The next token with text or null
	 */
	private ProToken getNextTokenWithText (ProToken token, ArrayList<Object> tree)
	{
		ProToken token2;
		for (int i = tree.indexOf(token) + 1; i < tree.size() - 1; i++)
		{
			token2 = this.getToken(tree.get(i));
			if (token2 != null && !token2.getText().isEmpty())
				return token2;
		}
		return null;
	}
	
	/**
	 * Replaces a makro reference with its content
	 * @param token The token of the macro reference
	 * @param tree The tree in which to replace it
	 */
	private void replaceMakro (ProToken token, ArrayList<Object> tree)
	{
		int start = tree.indexOf(token);
		int i = start - 1;
		MakroReferenceToken makro;
		String text = "";
		ProToken newToken;
		ProToken token2;

		
		if (tree.get(i) instanceof MakroReferenceToken)
			this.replaceNestedMakro (tree, token, this.getToken(tree.get(start - 1)));
		
		makro = (MakroReferenceToken) token;
		
		if (this.getToken(tree.get(i)).getType() == TokenTypes.AMPSCOPEDDEFINE || this.getToken(tree.get(i)).getType() == TokenTypes.AMPGLOBALDEFINE)
		{
			token2 = this.getToken(tree.get(i));
			token2.setText(token2.getText().replaceFirst(makro.getEscapedReferenceText(), makro.getReferenceName()));
			tree.remove(token);
			return;
		}
		
		while (text.trim().length() <= makro.getReferenceText().trim().length() && i < tree.size() && i >= 0)
		{
			if (this.getToken(tree.get(i)).getType() != TokenTypes.MAKROREFERENCE)
				text += this.getToken(tree.get(i)).getText();
			i++;
		}

		if (!text.contains(makro.getReferenceText()))
			return;
		
		text = text.replaceFirst(makro.getEscapedReferenceText(), makro.getReferenceName());
		newToken = new RefactoredToken(token, text);
		if (makro.getReferenceText().isEmpty())
			this.replaceEmptyMakro (tree, 
									this.getToken(tree.get(tree.indexOf(token) - 1)), 
									token, 
									this.getToken(tree.get(tree.indexOf(token) + 1)), 
									makro.getReferenceText(), 
									makro.getReferenceName());			
		else
		{				
			for (int j = i - 1; j >= start - 1; j--)
				tree.remove(j);
			tree.add(start - 1, newToken);
		}
	}
	
	/**
	 * Replaces a makro reference inside another makro reference
	 * @param tree
	 * @param outer
	 * @param inner
	 */
	private void replaceNestedMakro (ArrayList<Object> tree, ProToken outer, ProToken inner)
	{
		MakroReferenceToken outerMakro = (MakroReferenceToken) outer;
		MakroReferenceToken innerMakro = (MakroReferenceToken) inner;
		
		if (!outerMakro.getReferenceName().contains(innerMakro.getEscapedReferenceText()))
			return;
		
		outerMakro.setReferenceName (outerMakro.getReferenceName().replaceFirst (innerMakro.getEscapedReferenceText(), 
																				 innerMakro.getReferenceName()));
		tree.remove(inner);
	}
	
	/**
	 * Performs the replacement for a macro without content
	 * @param tree The flattened tree
	 * @param prev The token before the makro token
	 * @param token The makro token
	 * @param next The token after the makro
	 * @param refText The macro content text
	 * @param refName The macro reference text
	 */
	private void replaceEmptyMakro (ArrayList<Object> tree, ProToken prev, ProToken token, ProToken next, String refText, String refName)
	{
		String ws;
		int lineDiff, colDiff;
		
		if (prev.getLine() == token.getLine() && prev.getText().length() + prev.getColumn() == token.getColumn())
			prev.setText(String.format("%s%s", prev.getText(), refName));
		else if (next.getLine() == token.getLine() && next.getColumn() == token.getColumn() + refText.length())
			next.setText(String.format("%s%s", refName, next.getText()));
		else if (prev.getType() == TokenTypes.WS)
		{
			ws = prev.getText();
			lineDiff = token.getLine() - prev.getLine();
			if (lineDiff == 0)
				colDiff = token.getColumn() - prev.getColumn();
			else
				colDiff = lineDiff + token.getColumn();
			
			if (colDiff < ws.length())
				ws = String.format ("%s%s%s", 
						ws.substring(0, colDiff), 
						refName, 
						ws.substring(colDiff));
			else
				ws = String.format ("%s%s", ws, refName);

			prev.setText(ws);
		}
		tree.remove(token);
	}
	
	/**
	 * Attempts to return a token for the given object 
	 * @param obj A JPNode or ProToken
	 * @return The token or null 
	 */
	private ProToken getToken (Object obj) {
		if (obj instanceof ProToken)
			return (ProToken) obj;
		else if (obj instanceof JPNode)
			return ((JPNode) obj).getToken();
		else
			return null;
	}
	
	/**
	 * Flattens the nodes and hidden tokens of a tree
	 * @param node The node at which to start
	 * @param list The list to fill
	 */
	private void flattenTree (JPNode node, ArrayList<Object> list)
	{
		if (node == null || list == null)
			return;
		
		if (this.isOperator(node)) {
			this.flattenTree(node.firstChild(), list);
			this.addNodeAndToken(node, list);
			this.flattenTree(node.firstChild().nextSibling(), list);
		}
		else {
			this.addNodeAndToken(node, list);
			if (node.firstChild() != null)
				this.flattenTree(node.firstChild(), list);
		}
		
		if (node.nextSibling() != null && !this.isOperator(node.parent()))
			this.flattenTree(node.nextSibling(), list);
	}
	
	/**
	 * Adds a node and its tokens to the list
	 * @param node The node to add
	 * @param list The list to which to add it
	 */
	private void addNodeAndToken (JPNode node, ArrayList<Object> list)
	{
		for (ProToken t = node.getHiddenFirst(); t!=null; t = t.getNext())
		{
			if (t.getFileIndex() == 0)
				list.add(t);
		}
		if (node.getFileIndex() == 0)
			list.add(node);
	}
	
	/**
	 * Escapes characters used by regex
	 * @param txt The pattern for regex
	 * @return The fixed pattern
	 */
	public static String fixRegexEscape(String txt)
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
	 * Returns whether the given node is an operator node
	 * @param node The node to test
	 * @return Whether the node is an operator
	 */
	private boolean isOperator (JPNode node) {
		
		if (node == null)
			return false;
		
		if (node.firstChild() == null || node.firstChild().nextSibling() == null || node.firstChild().nextSibling().nextSibling() != null)
			return false;
		
		return node.getType() == TokenTypes.PLUS
			|| node.getType() == TokenTypes.MINUS
			|| node.getType() == TokenTypes.MULTIPLY
			|| node.getType() == TokenTypes.DIVIDE
			|| node.getType() == TokenTypes.MODULO
			|| node.getType() == TokenTypes.EQ
			|| node.getType() == TokenTypes.EQUAL
			|| node.getType() == TokenTypes.OR
			|| node.getType() == TokenTypes.AND
			|| node.getType() == TokenTypes.XOR
			|| node.getType() == TokenTypes.LTHAN
			|| node.getType() == TokenTypes.GTHAN
			|| node.getType() == TokenTypes.LE
			|| node.getType() == TokenTypes.GE
			|| node.getType() == TokenTypes.NE
			|| node.getType() == TokenTypes.PLUS_EQUAL
			|| node.getType() == TokenTypes.MINUS_EQUAL
			|| node.getType() == TokenTypes.DIVIDE_EQUAL
			|| node.getType() == TokenTypes.MULTIPLY_EQUAL
			|| node.getType() == TokenTypes.BEGINS
			|| node.getType() == TokenTypes.MATCHES
			|| node.getType() == TokenTypes.CONTAINS;
	}
}
