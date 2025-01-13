package com.joanju.proparse;

import java.util.ArrayList;
import java.util.HashMap;

import org.prorefactor.core.TokenTypes;
import org.prorefactor.refactor.RefactorException;

public class ConditionalCompilationToken 
	extends ProToken 
{
	protected ProToken ampIf;
	protected ProToken ampEndIf;
	
	protected final ArrayList<ProToken> tokens;
	protected final HashMap<ProToken, ConditionalCompilationToken> children;
	
	protected String enclosedText;
	
	protected boolean open;
	
	/**
	 * Copy-Constructor for the ConditionalCompilationToken class to create the closing token
	 * @param token The original token
	 */
	public ConditionalCompilationToken (ConditionalCompilationToken token)
	{
		super (token.filenameList,
			   TokenTypes.CONDITIONALCOMPILATION,
			   "",
			   token.fileIndex,
			   token.line,
			   token.col,
			   token.macroSourceNum);
		this.setAmpIf(token.getAmpIf());
		this.setEndIf(token.getEndIf());
		this.open = false;
		this.tokens = new ArrayList<ProToken>();
		this.children = new HashMap<ProToken, ConditionalCompilationToken>();
	}
	
	/**
	 * Constructor for the ConditionalCompilationToken class
	 * @param filenameList IntegerIndex for the list of filenames
	 * @param file The index of the file
	 * @param textStartLine The starting line
	 * @param textStartCol The starting column
	 * @param textStartSource The starting macroSourceNum
	 */
	public ConditionalCompilationToken (IntegerIndex<String> filenameList,
										int file,
										int textStartLine, 
										int textStartCol, 
										int textStartSource) 
	{
		super (filenameList, TokenTypes.CONDITIONALCOMPILATION, "", file, textStartLine, textStartCol, textStartSource);
		this.open = true;
		this.tokens = new ArrayList<ProToken>();
		this.children = new HashMap<ProToken, ConditionalCompilationToken>();
	}
	
	public void addToken (ProToken token)
	{
		if (!this.tokens.contains(token))
		{
			this.tokens.add(token);
			token.addedToArrayList(this.tokens);
		}
	}
	
	public void addChild (ConditionalCompilationToken token)
	{
		if (!this.children.containsKey(token.getAmpIf()))
			this.children.put (token.getAmpIf(), token);
	}
	
	/**
	 * Returns whether this class is for the &IF token
	 * @return Whether this class is for the &IF token
	 */
	public boolean isOpening ()
	{
		return this.open;
	}

	/**
	 * Gets the &IF token
	 * @return The &IF token
	 */
	public ProToken getAmpIf ()
	{
		return this.ampIf;
	}
	
	/**
	 * Sets the &IF token
	 * @param ampIf The &IF token
	 */
	public void setAmpIf (ProToken ampIf)
	{
		this.ampIf = ampIf;
	}
	
	/**
	 * Sets the &ENDIF token and gets the enclosed text
	 * @param ampEndIf The &ENDIF token
	 */
	public void setEndIf (ProToken ampEndIf)
	{
		this.ampEndIf = ampEndIf;
	}
	
	/**
	 * Returns the &ENDIF token
	 * @return The &ENDIF token
	 */
	public ProToken getEndIf ()
	{
		return this.ampEndIf;
	}
	
	/**
	 * Returns the text between &IF and &ENDIF
	 * @return The text between &IF and &ENDIF
	 * @throws RefactorException 
	 */
	public String getEnclosedText (int level, ArrayList<ProToken> processed) throws RefactorException
	{
		StringBuilder sb = new StringBuilder ();
		HashMap<Integer, ProToken> macros = new HashMap<Integer, ProToken>();
		Object[] keys;
		MakroReferenceToken macro;
		String text;
		
		if (!processed.contains(this.ampIf))
		{
			sb.append(this.ampIf.getText());
			processed.add(this.ampIf);
		}
		else
			return "";
		
		for (ProToken token: this.tokens)
		{
			if (token.equals(this.ampEndIf))
				break;
			if (!processed.contains(token))
			{
				if (this.children.containsKey(token))
					sb.append(this.children.get(token).getEnclosedText(level + 1, processed));
				else if (token.getType() != TokenTypes.MAKROREFERENCE)	
				{
					sb.append(token.getText());
					processed.add(token);
				}
				else 
					macros.put(sb.length(), token);
			}
		}

		if (!processed.contains(this.ampEndIf))
		{
			sb.append (this.ampEndIf.getText());
			processed.add(this.ampEndIf);
		}
		
		keys = macros.keySet().toArray();
		text = sb.toString();
		for (int i = keys.length - 1; i >= 0; i--)
		{
			macro = (MakroReferenceToken)macros.get((int) keys[i]);
			text = String.format ("%s%s", 
							      text.substring(0, (int) keys[i] - macro.length()),
							      text.substring((int) keys[i] - macro.length()).replaceFirst (macro.getEscapedReferenceText(), 
							    		  													   macro.getReferenceName()));
		}
		
		return text;
	}
}
