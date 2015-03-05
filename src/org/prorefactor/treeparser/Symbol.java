/**
 * Symbol.java
 * @author John Green
 * 6-Nov-2002
 * www.joanju.com
 * 
 * Copyright (c) 2002-2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */

package org.prorefactor.treeparser;


import org.prorefactor.core.JPNode;
import org.prorefactor.core.TokenTypes;

import java.io.IOException;

import com.joanju.DataXferStream;


/** Base class for any type of symbol which needs to be
 * kept track of when parsing a 4gl compile unit's AST.
 */
abstract public class Symbol implements SymbolI {

	/** Only to be used for persistence/serialization. */
	protected Symbol() {}

	protected Symbol(int allRefsCount, JPNode asNode, JPNode defNode, JPNode likeNode, String name, int numReads, int numWrites, SymbolScope scope) {
		this.allRefsCount = allRefsCount;
		this.asNode = asNode;
		this.defNode = defNode;
		this.likeNode = likeNode;
		this.name = name;
		this.numReads = numReads;
		this.numWrites = numWrites;
		this.scope = scope;
	}


	Symbol(SymbolScope scope) {
		this.scope = scope;
		scope.addSymbol(this);
	}

	private int allRefsCount = 0;
	private int numReads = 0;
	private int numWrites = 0;
	private JPNode asNode;

	/** We store the DEFINE node if available and sensible.
	 * If defined in a syntax where there is no DEFINE node briefly
	 * preceeding the ID node, then we store the ID node.
	 * If this is a schema symbol, then this member is null.
	 */
	private JPNode defNode;

	private JPNode likeNode;

	/** What scope this symbol was defined in. */
	private SymbolScope scope;

	/** Stores the full name, original (mixed) case as in definition. */	
	private String name;

	
	/** Generate a bare-bones copy of this symbol.
	 * Requires the scope to attach it to as the argument.
	 */
	public abstract Symbol copyBare(SymbolScope intoScope);


	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#fullName()
	 */
	public abstract String fullName();
	
	

	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getAllRefsCount()
	 */
	public int getAllRefsCount() { return allRefsCount; }

	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getNumReads()
	 */
	public int getNumReads() { return numReads; }
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getNumWrites()
	 */
	public int getNumWrites() { return numWrites; }


	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getAsNode()
	 */
	public JPNode getAsNode() { return asNode; }

	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getDefineNode()
	 */
	public JPNode getDefineNode() {
		if (defNode!=null && defNode.getType()!=TokenTypes.ID) return defNode;
		return null;
	}
	


	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getIndirectDefineIdNode()
	 */
	public JPNode getIndirectDefineIdNode() {
		if (defNode!=null && defNode.getType()==TokenTypes.ID) return defNode;
		return null;
	}

	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getLikeNode()
	 */
	public JPNode getLikeNode() { return likeNode; }

	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getName()
	 */
	public String getName() { return name; }

	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getProgressType()
	 */
	public abstract int getProgressType();
	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#getScope()
	 */
	public SymbolScope getScope() { return scope; }

	
	/* @see SymbolI#isExported() */
	public boolean isExported() {
		// If the symbol belongs to a SymbolScopeSuper, then not only has it already
		// been determined that the symbol is exported, but also the rest of this
		// method would just not work because there is never any AST linked to any
		// of the symbols in a SymbolScopeSuper.
		if (this.scope instanceof SymbolScopeSuper) return true;
		SymbolScopeRoot unitScope = scope.getRootScope();
		// If this is not at the unit (root) scope, then it cannot be visible.
		if (scope!=unitScope) return false;
		if (unitScope.getClassName()!=null) {
			// For class members, only elements declared PUBLIC|PROTECTED are visible.
			// Unnamed buffers don't have a DEFINE node.
			if (defNode==null) return false;
			return	(	defNode.findDirectChild(TokenTypes.PUBLIC)!=null
					||	defNode.findDirectChild(TokenTypes.PROTECTED)!=null
					);
		}
		// If there is no DEFINE node (inline var def), then it is not visible.
		if (defNode == null) return false;
		switch (defNode.getType()) {
			case TokenTypes.DEFINE:
				return defNode.firstChild().getType() == TokenTypes.NEW;
			case TokenTypes.FUNCTION:
			case TokenTypes.PROCEDURE:
				return defNode.findDirectChild(TokenTypes.PRIVATE) == null;
		}
		return false;
	}

	
	
	/* @see SymbolI#isImported() */
	public boolean isImported() { 
		// If there is no DEFINE node (inline var def), then it is not SHARED.
		if (	defNode == null
			||	defNode.getType() != TokenTypes.DEFINE
			) return false;
		if (defNode.firstChild().getType() == TokenTypes.SHARED) return true;
		return false;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#noteReference(int)
	 */
	public void noteReference(int contextQualifier) {
		allRefsCount++;
		if (CQ.isRead(contextQualifier)) numReads++;
		if (CQ.isWrite(contextQualifier)) numWrites++;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#setAsNode(org.prorefactor.core.JPNode)
	 */
	public void setAsNode(JPNode asNode) { this.asNode = asNode; }

	
	
	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#setDefOrIdNode(org.prorefactor.core.JPNode)
	 */
	public void setDefOrIdNode(JPNode node) { defNode = node; }



	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#setLikeNode(org.prorefactor.core.JPNode)
	 */
	public void setLikeNode(JPNode likeNode) { this.likeNode = likeNode; }



	/* (non-Javadoc)
	 * @see org.prorefactor.treeparser.SymbolI#setName(java.lang.String)
	 */
	public void setName(String name) { this.name = name; }


	@Override
	public String toString() {
		return fullName();
	}


	/** Implement Xferable */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeInt(allRefsCount);
		out.writeRef(asNode);
		out.writeRef(defNode);
		out.writeBool(isExported());
		out.writeBool(isImported());
		out.writeRef(likeNode);
		out.writeRef(name);
		out.writeInt(numReads);
		out.writeInt(numWrites);
		out.writeRef(scope);
	}
	/** Implement Xferable */
	public void writeXferSchema(DataXferStream out) throws IOException {
		out.schemaInt("allRefsCount");
		out.schemaRef("asNode");
		out.schemaRef("defNode");
		out.schemaBool("isExported");
		out.schemaBool("isImported");
		out.schemaRef("likeNode");
		out.schemaRef("name");
		out.schemaInt("numReads");
		out.schemaInt("numWrites");
		out.schemaRef("scope");
	}


}
