/**
 * ParseUnit.java
 * @author John Green
 * Aug 12, 2004
 * www.joanju.com
 *
 * Copyright (C) 2004-2007 Joanju Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.treeparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.PRCException;
import org.prorefactor.macrolevel.IncludeRef;
import org.prorefactor.macrolevel.ListingParser;
import org.prorefactor.macrolevel.MacroLevel;
import org.prorefactor.macrolevel.MacroRef;
import org.prorefactor.nodetypes.ProgramRootNode;
import org.prorefactor.refactor.FileStuff;
import org.prorefactor.refactor.PUB;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.treeparser01.TP01Action;
import org.prorefactor.treeparser01.TreeParser01;

import com.joanju.proparse.DoParse;

/**
 * Provides parse unit information, such as the symbol table and a reference to
 * the AST. TreeParser01 calls symbolUsage() in this class in order to build the
 * symbol table.
 */
public class ParseUnit {

	public ParseUnit() {
		this("dummy.p");
	}

	public ParseUnit(String fileName) {
		this(new File(fileName));
	}

	public ParseUnit(File file) {
		this.file = file;
	}
	
	/**
	 * Creates a new ParseUnit with a custom codepage
	 * @param file The file that is to be parsed
	 * @param codepage Name of the codepage that should be used for file operations instead of the default
	 * @throws IllegalCharsetNameException Thrown if the codepage contains illegal characters See: https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html#names
	 * @throws UnsupportedCharsetException Thrown if the codepage is not recognized/supported by the system
	 * @throws IllegalArgumentException Thrown if the codepage is null
	 */
	public ParseUnit(File file, String codepage) throws IllegalCharsetNameException,
														UnsupportedCharsetException,
							 					 		IllegalArgumentException {
		this.file = file;
		try {
			
			Charset.forName(codepage);
			System.setProperty("file.encoding", codepage);
			
		} catch (IllegalCharsetNameException e) {
				
				throw new IllegalCharsetNameException("Codepage " + codepage + " is not a valid name for a codepage!");
				
		}catch (UnsupportedCharsetException e) {
			
			throw new UnsupportedCharsetException("Codepage " + codepage + " is not supported!");
			
		} catch (IllegalArgumentException e) {
			
			throw new IllegalArgumentException("The codepage can not be null!", new NullPointerException());
			
		} 
	}

	/** The JPNode tree is "connected" to Proparse, by default */
	public static final int CONNECTED = 0;

	/** The JPNode tree is "connected" to Proparse, by default */
	public static final int DEFAULT = 0;

	/**
	 * Working with JPNode in disconnected mode may not yet be fully supported.
	 * See JPNode docs.
	 */
	public static final int DISCONNECTED = 1 << 1;

	protected int style = DEFAULT;
	protected File file;
	private IncludeRef macroGraph = null;
	private ProgramRootNode topNode;
	protected PUB pub = null;
	protected RefactorSession refpack = RefactorSession.getInstance();
	private SymbolScopeRoot rootScope;

	public File getFile() {
		if (file == null) {
			// A lot of old code starts with a string filename, sends that to
			// Proparse, gets the top node
			// handle, builds JPNode, and then runs TreeParser01 from that. (All
			// the stuff ParseUnit does
			// now.) In those cases, this ParseUnit might have been created as
			// an empty shell by TreeParser01
			// itself, and "file" would not be set. In that case, we attempt to
			// find the File from the file index.
			if (topNode == null)
				return null;
			file = new File(topNode.getFilenames()[0]);
		}
		return file;
	}

	/**
	 * Get the file index, either from the PUB file or from the parser,
	 * whichever was used to get the tree. The return is the array of file
	 * names. The file at index zero is always the compile unit. The others are
	 * include files. The array index position corresponds to
	 * JPNode.getFileIndex().
	 * 
	 * @see org.prorefactor.nodetypes.ProgramRootNode#getFilenames()
	 */
	public String[] getFileIndex() {
		if (topNode == null)
			return null;
		return topNode.getFilenames();
	}

	/** This will trigger a parse if the PUB is out of date. */
	public IncludeRef getMacroGraph() throws RefactorException, IOException {
		if (macroGraph != null)
			return macroGraph;
		getPUB();
		if (!pub.isChecked())
			pub.loadTo(PUB.HEADER);
		if (!pub.isCurrent()) {
			// Does the parse, which causes the macroGraph to be built and
			// written to disc.
			pub.build();
		} else {
			try {
				FileInputStream fileIn = new FileInputStream(macroGraphFile());
				ObjectInputStream in = new ObjectInputStream(fileIn);
				macroGraph = (IncludeRef) in.readObject();
				in.close();
				fileIn.close();
			} catch (Exception e) {
				throw new RefactorException(e);
			}
		}
		return macroGraph;
	}

	/**
	 * This is just a shortcut for calling getMacroGraph() and
	 * MacroLevel.sourceArray(). Build and return an array of the MacroRef
	 * objects, which would map to the SOURCENUM attribute from JPNode. Built
	 * simply by walking the tree and adding every MacroRef to the array.
	 * 
	 * @see org.prorefactor.macrolevel.MacroLevel#sourceArray(MacroRef)
	 */
	public MacroRef[] getMacroSourceArray() throws RefactorException, IOException {
		return MacroLevel.sourceArray(getMacroGraph());
	}

	/** Get or create a PUB */
	public PUB getPUB() {
		if (pub == null) {
			pub = new PUB(FileStuff.fullpath(getFile()));
			pub.setParseUnit(this);
		}
		return pub;
	}

	public SymbolScopeRoot getRootScope() {
		return rootScope;
	}

	/** Get the syntax tree top (Program_root) node */
	public ProgramRootNode getTopNode() {
		if (topNode == null && pub != null)
			setTopNode(pub.getTree());
		return topNode;
	}

	/**
	 * Load from PUB, or build PUB if it's out of date. TreeParser01 is run in
	 * order to build the PUB. If the PUB was up to date, then TreeParser01 is
	 * run after the PUB is loaded. (Either way, the symbol tables etc. are
	 * available.)
	 */
	public void loadOrBuildPUB() throws RefactorException, IOException {
		getPUB();
		if (pub.load()) {
			setTopNode(pub.getTree());
			treeParser01();
		} else {
			pub.build();
		}
	}

	private File macroGraphFile() {
		// .msg = Macro Source Graph. Common source of heartburn.
		return new File(PUB.pubDirFileName(file.getAbsolutePath()) + ".msg");
	}

	public void parse() throws RefactorException {
		parse(file, null);
	}

	public void parse(String fileName) throws RefactorException {
		parse(fileName, null);
	}
	
	public void parse(String fileName, String fileContent) throws RefactorException {
		parse(new File(fileName), fileContent);
	}
		
	public void parse(File file, String fileContent) throws RefactorException {
		String errmsg = "";
		
		if (file != null) {
			errmsg = String.format ("Error parsing %s%n%n", file.getName()); 
		}
		
		if (fileContent != null && !fileContent.isEmpty()) {
			errmsg = String.format ("%sError parsing fileContent.%n%n", errmsg);
		}
		
		if (refpack == null) {
			throw new RefactorException (errmsg + "refpack is null");
		}
		
		DoParse doParse; 
		
		try {
			refpack.enableParserListing();
			doParse = new DoParse(file.getAbsolutePath(), fileContent);
		}
		catch (Exception e) {
			throw new RefactorException (e, errmsg);
		}
		
		try {
			doParse.doParse();
			ListingParser listingParser = new ListingParser(RefactorSession.getListingFileName());
			listingParser.parse();
			macroGraph = listingParser.getRoot();
			if (refpack.getProjectBinaraiesEnabled()) {
				File macroGraphFile = macroGraphFile();
				macroGraphFile.getParentFile().mkdirs();
				FileOutputStream fileOut = new FileOutputStream(macroGraphFile);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(macroGraph);
				out.close();
				fileOut.close();
			}
		} catch (Exception e) {
			throw new RefactorException (e, errmsg);
		}
		
		try {
			setTopNode(doParse.getTopNode());
		}
		catch (Exception e) {
			throw new RefactorException (e, errmsg);
		}
	}

	public ParseUnit setPUB(PUB pub) {
		this.pub = pub;
		if (pub.getParseUnit() != this)
			pub.setParseUnit(this);
		return this;
	}

	public void setRootScope(SymbolScopeRoot rootScope) {
		this.rootScope = rootScope;
	}

	/** Set the syntax tree top (Program_root) node. */
	public void setTopNode(JPNode topNode) {
		this.topNode = (ProgramRootNode) topNode;
	}

	/**
	 * Run any IJPTreeParser against the AST. This will call parse() if the
	 * JPNode AST has not already been built.
	 */
	public void treeParser(IJPTreeParser tp) throws RefactorException {
		if (this.getTopNode() == null)
			parse();
		try {
			TreeParserWrapper.run2(tp, this.getTopNode());
		} catch (PRCException e) {
			if (e.getMessage() == null)
				throw new RefactorException (e);
			else
				throw new RefactorException(e.getMessage(), e);
		}
	}

	/**
	 * Run TreeParser01. Takes care of calling parse() first, if that has not
	 * already been done.
	 */
	public void treeParser01() throws RefactorException {
		if (this.getTopNode() == null)
			parse();
		TreeParser01 tp = new TreeParser01();
		tp.getActionObject().setParseUnit(this);
		treeParser(tp);
	}
	
	/**
	 * Run TreeParser01. First parse the input content.
	 */
	public void treeParser01(String inputContent) throws RefactorException {
		treeParser01(inputContent, "dummy.p");
	}
	
	/**
	 * Run TreeParser01. First parse the input content.
	 */
	public void treeParser01(String inputContent, String fileName) throws RefactorException {
		parse(fileName, inputContent);
		TreeParser01 tp = new TreeParser01();
		tp.getActionObject().setParseUnit(this);
		treeParser(tp);
	}

	/**
	 * Run TreeParser01 with any TP01Action object. Takes care of calling
	 * parse() first, if that has not already been done.
	 */
	public void treeParser01(TP01Action action) throws RefactorException {
		if (this.getTopNode() == null)
			parse();
		TreeParser01 tp = new TreeParser01();
		tp.setActionObject(action);
		action.setParseUnit(this);
		treeParser(tp);
	}
}
