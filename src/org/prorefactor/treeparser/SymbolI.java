/* Created on 19-Jan-2006
 * Authors: john
 */
package org.prorefactor.treeparser;

import java.util.Comparator;

import org.prorefactor.core.JPNode;
import com.joanju.Xferable;

public interface SymbolI extends Xferable {

	/** Comparator for sorting by name. */
	public static final Comparator NAME_ORDER = new Comparator() {
		public int compare(Object o1, Object o2) {
			Symbol s1 = (Symbol) o1;
			Symbol s2 = (Symbol) o2;
			return s1.getName().compareToIgnoreCase(s2.getName());
		}
	};

	/** Get the "full" name for this symbol. This is expected to be overridden
	 * in subclasses. For example, we might expect "database.buffer.field" to
	 * be the return for a field buffer.
	 */
	public abstract String fullName();

	public abstract int getAllRefsCount();

	public abstract int getNumReads();

	public abstract int getNumWrites();

	/** If this was defined AS something, then we have an AS node */
	public abstract JPNode getAsNode();

	/** If this symbol was defined directly by a DEFINE syntax,
	 * then this returns the DEFINE node, otherwise null.
	 */
	public abstract JPNode getDefineNode();

	/** If this symbol was defined with syntax other than a direct DEFINE,
	 * then this returns the ID node, otherwise null.
	 */
	public abstract JPNode getIndirectDefineIdNode();

	/** If this was defined LIKE something, then we have a LIKE node */
	public abstract JPNode getLikeNode();

	public abstract String getName();

	/** From TokenTypes: VARIABLE, FRAME, MENU, MENUITEM, etc.
	 * A TableBuffer object always returns BUFFER, regardless of whether
	 * the object is a named buffer or a default buffer.
	 * A FieldBuffer object always returns FIELD.
	 */
	public abstract int getProgressType();

	public abstract SymbolScope getScope();

	/** Is the symbol newly defined here and visible to other compile units?
	 * This includes PROTECTED members visible to subclasses.
	 */
	public abstract boolean isExported();

	/** Defined as SHARED? */
	public abstract boolean isImported();

	/** Take note of a symbol reference (read, write, reference by name). */
	public abstract void noteReference(int contextQualifier);

	/** @see #getAsNode() */
	public abstract void setAsNode(JPNode asNode);

	/** We store the DEFINE|FUNCTION|METHOD|PROCEDURE node if available and sensible.
	 * If defined in a syntax where there is no DEFINE node briefly
	 * preceeding the ID node, then we store the ID node.
	 */
	public abstract void setDefOrIdNode(JPNode node);

	/** @see #getLikeNode() */
	public abstract void setLikeNode(JPNode likeNode);

	public abstract void setName(String name);

}