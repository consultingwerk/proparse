/* Created on 19-Jan-2006
 * Authors: john
 */
package org.prorefactor.treeparser;

public interface WidgetI extends SymbolI {

	/** For this subclass of Symbol, fullName() returns the same value as getName(). */
	public abstract String fullName();

}