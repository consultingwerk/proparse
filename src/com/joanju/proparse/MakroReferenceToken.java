package com.joanju.proparse;

import org.prorefactor.core.NodeTextUtils;
import org.prorefactor.core.TokenTypes;

public class MakroReferenceToken 
	extends ProToken {

	protected String refName = "";
	protected String refText = "";
	protected int length = 0;
	
	public MakroReferenceToken (IntegerIndex<String> filenameList, 
								int file, 
								int line, 
								int col, 
								int macroSourceNum, 
								String refName, 
								String refText)
	{
		super (filenameList,
			   TokenTypes.MAKROREFERENCE,
			   "",
			   file,
			   line,
			   col,
			   macroSourceNum);
		this.setReferenceName (refName);
		this.setReferenceText (refText);
	}
	
	public String getReferenceName ()
	{
		return this.refName;
	}
	
	public void setReferenceName (String refName)
	{
		this.refName = refName;
	}
	
	public String getReferenceText ()
	{
		return this.refText;
	}
	
	public String getEscapedReferenceText ()
	{
		return NodeTextUtils.fixRegexEscape(this.getReferenceText());
	}
	
	public void setReferenceText (String refText)
	{
		this.refText = refText;
		this.length = refText.length();
	}
	
	public int length ()
	{
		return this.length;
	}
}
