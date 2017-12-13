/**
 * 
 */
package de.consultingwerk.proparse.refactor;

import com.joanju.proparse.IntegerIndex;
import com.joanju.proparse.ProToken;

/**
 * @author Mike Fechner
 *
 */
public class RefactoredToken extends ProToken {

	private String _text;

	/**
	 * Constructor for the RefactoredToken class. 
	 * Initializes the Token based on the original token
	 * @param orig
	 */
	public RefactoredToken(ProToken orig, String text) {
		super(orig);
		
		this.setHiddenBefore(orig.getHiddenBefore());
		this.setHiddenAfter(orig.getHiddenAfter());
		
		this._text  = text;
	}
	
	public String getText () {
		return this._text;
	}

	public String setText () {
		return this._text;
	}	
}
