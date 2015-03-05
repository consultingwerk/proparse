/* MacroRef.java
 * Created on Nov 29, 2003
 * John Green
 *
 * Copyright (C) 2003-2007 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.macrolevel;


import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

import org.prorefactor.core.Util;
import org.prorefactor.refactor.RefactorException;
import com.joanju.DataXferStream;


/** Abstract class for a macro reference.
 * There are two subclasses: one for references to named macros
 * (i.e. those named with &global, &scoped, or an include argument),
 * and one for references to include files.
 */
public abstract class MacroRef implements MacroEvent {
	
	/** Only to be used for persistence/serialization. */
	protected MacroRef() {}


	MacroRef(int listingFileLine) {
		this.listingFileLine = listingFileLine;
	}

	int listingFileLine;
	public int refColumn;
	public int refLine;
	public MacroRef parent = null;

	/** A list of macro references and defines that are in this macro's source */
	public ArrayList<MacroEvent> macroEventList = new ArrayList<MacroEvent>();


	public int getListingFileLine() {return listingFileLine;}
	

	public MacroRef getParent() { return parent; }
	
	
	/** Find <i>external macro references</i>.
	 * An external macro is an include file, a &GLOBAL or a &SCOPED from another file,
	 * and include args.
	 * 
	 * TODO: (Jan 26) This doesn't seem right to me anymore. An &UNDEFINE only affects
	 * the local scope. If re-implemented after building a pseudoprocessor, consider
	 * dropping this.
	 * &UNDEFINE of a &GLOBAL or of a &SCOPED from another file is considered a reference.
	 * &UNDEFINE of an include argument is considered a reference.
	 * 
	 * The subroutine is recursive, because a local define may incur an external reference!
	 * @return An array of objects: MacroRef and MacroDef (for UNDEFINE).
	 */
	public ArrayList<MacroEvent> findExternalMacroReferences() {
		ArrayList<MacroEvent> ret = new ArrayList<MacroEvent>();
		for (Iterator<MacroEvent> it = macroEventList.iterator(); it.hasNext(); ) {
			findExternalMacroReferences(it.next(), ret);
		}
		return ret;
	}
	/**
	 * @see #findExternalMacroReferences()
	 * @param begin An array of two integers to indicate the beginning line/column.
	 * May be null to indicate the beginning of the range is open ended. 
	 * @param end An array of two integers to indicate the ending line/column.
	 * May be null to indicate the ending of the range is open ended. 
	 */
	public ArrayList<MacroEvent> findExternalMacroReferences(int [] begin, int [] end) {
		ArrayList<MacroEvent> ret = new ArrayList<MacroEvent>();
		for (Iterator it = macroEventList.iterator(); it.hasNext(); ) {
			Object next = it.next();
			if (next instanceof MacroRef) {
				MacroRef ref = (MacroRef)next;
				if (Util.isInRange(ref.refLine, ref.refColumn, begin, end)) {
					findExternalMacroReferences(ref, ret);
				}
				continue;
			}
			if (next instanceof MacroDef) {
				MacroDef def = (MacroDef)next;
				if (Util.isInRange(def.line, def.column, begin, end))
					findExternalMacroReferences(def, ret);
			}
		}
		return ret;
	}
	private void findExternalMacroReferences(MacroEvent obj, ArrayList<MacroEvent> list) {
		if (obj==null) return;
		if (obj instanceof IncludeRef) {
			list.add(obj);
			return;
		}
		if (obj instanceof MacroDef) {
			MacroDef def = (MacroDef)obj;
			if (def.type==MacroDef.UNDEFINE) {
				if (def.undefWhat.type==MacroDef.NAMEDARG) {
					list.add(def);
					return;
				}
				if (! isMine(def.undefWhat.parent)) list.add(def);
			}
			return;
		}
		// Only one last type we're interested in...
		if (! (obj instanceof NamedMacroRef)) return;
		NamedMacroRef ref = (NamedMacroRef)obj;
		if (! isMine(ref)) {
			list.add(ref);
			return;
		}
		// It's possible for an internal macro to refer to an external macro
		for (Iterator<MacroEvent> it = ref.macroEventList.iterator(); it.hasNext(); ) {
			findExternalMacroReferences(it.next(), list);			
		}
	}



	/** Find references to an include file by the include file's file index number.
	 * Search is recursive, beginning at this MacroRef object.
	 * @param fileIndex The fileIndex for the include file we want references to.
	 * @return An array of IncludeRef objects.
	 */
	public ArrayList<IncludeRef> findIncludeReferences(int fileIndex) {
		ArrayList<IncludeRef> ret = new ArrayList<IncludeRef>();
		findIncludeReferences(fileIndex, this, ret);
		return ret;
	}
	private void findIncludeReferences(int fileIndex, MacroRef ref, ArrayList<IncludeRef> list) {
		if (ref==null) return;
		if (ref instanceof IncludeRef) {
			IncludeRef incl = (IncludeRef) ref;
			if (incl.fileIndex==fileIndex) list.add(incl);
		}
		for (Iterator it = ref.macroEventList.iterator(); it.hasNext(); ) {
			Object next = it.next();
			if (next instanceof MacroRef)
				findIncludeReferences(fileIndex, (MacroRef)next, list);
		}
	}
	
	
	public abstract int getFileIndex();


	/** Get an int[3] file/line/column position for this macro reference. */
	public int[] getPosition() throws RefactorException {
		int [] ret = { parent.getFileIndex(), refLine, refColumn };
		return ret;
	}


	/** Is a macro ref/def myself, or, a child of mine? */
	private boolean isMine(MacroEvent obj) {
		if (obj==null) return false;
		if (obj==this) return true;
		return isMine(obj.getParent());
	}


	/** Implement Xferable. */
	public void writeXferBytes(DataXferStream out) throws IOException {
		out.writeInt(listingFileLine);
		out.writeRef(macroEventList);
		out.writeRef(parent);
		out.writeInt(refColumn);
		out.writeInt(refLine);
	}
	/** Implement Xferable. */
	public void writeXferSchema(DataXferStream out) throws IOException {
		out.schemaInt("listingFileLine");
		out.schemaRef("macroEventList");
		out.schemaRef("parent");
		out.schemaInt("refColumn");
		out.schemaInt("refLine");
	}


}
