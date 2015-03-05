/** July 2008 by John Green
 *
 * Copyright (C) 2008 Joanju Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.joanju.proparse.sockets;

import org.prorefactor.core.TreeUtils;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import com.joanju.DataXferStream;

public class BlobBuilder {

	BlobBuilder(String filename) {
		this.filename = filename;
	}

	private ByteArrayOutputStream headerBytes = new ByteArrayOutputStream();
	private DataOutputStream header = new DataOutputStream(headerBytes);
	private DataXferStream xfer = new DataXferStream();
	private String filename;

	private static int proparseBuildNum = 0;

	static {
		setBuildNumFromJar();
	}


	public void build() throws RefactorException, IOException {
		ParseUnit pu = new ParseUnit(new File(filename));
		pu.treeParser01();
		pu.getMacroGraph();
		int sourceFilesOffset = xfer.store(pu.getFileIndex());
		int nodesOffset = xfer.store(TreeUtils.nodeArray(pu.getTopNode()));
		int rootScopeOffset = xfer.store(pu.getRootScope());
		int macroSourcesOffset = xfer.store(pu.getMacroSourceArray());
		xfer.buildIndexes();
		header.writeInt(sourceFilesOffset);
		header.writeInt(nodesOffset);
		header.writeInt(rootScopeOffset);
		header.writeInt(macroSourcesOffset);
		header.writeInt(xfer.getSchemaMapOffset());
		header.writeInt(xfer.getIndexOffset());
		header.writeInt(proparseBuildNum);
	}


	public ByteArrayOutputStream getBlob() {return xfer.getBytes();}

	public ByteArrayOutputStream getHeader() {return headerBytes;}

	public static int getBuildNumber() {return proparseBuildNum;}


	private static void setBuildNumFromJar() {
		JarFile jarFile = null;
		try {
			URL jarURL = BlobBuilder.class.getClassLoader().getResource(
					"com/joanju/proparse/sockets" );
			JarURLConnection jurlConn = (JarURLConnection)jarURL.openConnection();
			jarFile = jurlConn.getJarFile();
		} catch (Throwable e) {
			// An exception would be thrown if this is being run from a
			// development environment, rather than from the .jar.
			// I do my builds in '../ant', so I can look for a proparse.jar
			// file there to try reading the manifest from.
			// This is OK for unit testing.
			try {
				jarFile = new JarFile("../ant/proparse.jar");
			} catch (IOException e1) {
				System.out.println(e1);
			}
		}
		if (jarFile==null)
			return; // build# will just be zero.
		Manifest mf = null;
		try {
			mf = jarFile.getManifest();
			Attributes attr = mf.getAttributes("Proparse");
			String buildString = attr.getValue("Build");
			proparseBuildNum = Integer.parseInt(buildString);
		} catch (IOException e) { }
	}


}
