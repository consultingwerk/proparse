package com.joanju.proparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.prorefactor.core.TokenTypes;

public class ConditionalCompilationToken 
	extends ProToken 
{
	protected ProToken ampIf;
	protected ProToken ampEndIf;
	
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
		this.setEnclosedText ();
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
	 */
	public String getEnclosedText ()
	{
		return this.enclosedText;
	}
	
	/**
	 * Sets the enclosedText attribute as the text between &IF and &ENDIF
	 */
	protected void setEnclosedText () 
	{
		int beginLine;
		int beginCol;
		int endLine;
		int endCol;
		
		String beginFilename;
		String endFilename;
		
		ArrayList<String> lines;
		
		if (   this.ampIf == null
			|| this.ampEndIf == null)
			return;
		
		beginLine 	  = this.ampIf.getLine() - 1;
		beginCol  	  = this.ampIf.getColumn() - 1;
		beginFilename = this.ampIf.getFilename();
		
		endLine 	= this.ampEndIf.getLine() + ((this.ampIf.getLine() == this.ampEndIf.getLine()) ? -1 : 0) ;
		endCol 		= this.ampEndIf.getColumn() + this.ampEndIf.getText().length();
		endFilename = this.ampEndIf.getFilename();
		
		this.enclosedText = "";
		if (beginFilename.equals(endFilename))
		{
			lines = this.getLines(beginFilename);
			if(beginLine == endLine)
				this.enclosedText = lines.get(beginLine).substring (beginCol, endCol);
			else
			{
				for (int i = beginLine; i < endLine; i++)
				{
					if (i == beginLine)
						this.enclosedText += lines.get(i).substring (beginCol);
					else if (i == endLine)
						this.enclosedText += lines.get(i).substring (0, endCol);
					else
						this.enclosedText += lines.get(i);
				}
			}
		}
		else
		{
			lines = this.getLines(beginFilename);
			for (int i = beginLine; i < lines.size(); i++)
			{
				if (i == beginLine)
					this.enclosedText += lines.get(i).substring (beginCol);
				else
					this.enclosedText += lines.get(i);
			}
			
			lines = this.getLines(endFilename);
			for (int i = 0; i < endLine; i++)
			{
				if (i == endLine)
					this.enclosedText += lines.get(i).substring (0, endCol);
				else
					this.enclosedText += lines.get(i);
			}
		}
		// Remove last new Line
		this.enclosedText = this.enclosedText.substring(0, this.enclosedText.length() + ((this.ampIf.getLine() == this.ampEndIf.getLine()) ? -1 : -2));
	}
	
	/**
	 * Reads a file as a list of lines
	 * @param filename Name of the file to read
	 * @return The files text as list of lines
	 */
	private ArrayList<String> getLines (String filename)
	{
		BufferedReader in;
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		try 
		{
			in = new BufferedReader (new FileReader (new File (filename)));
			while ((line = in.readLine()) != null)
				lines.add(line + System.lineSeparator());
			in.close();
			
			return lines;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
}
