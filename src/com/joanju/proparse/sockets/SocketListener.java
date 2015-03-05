/** July 2008 by John Green
 *
 * Copyright (C) 2008 Joanju Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.joanju.proparse.sockets;

import com.joanju.DataXferStream;
import com.joanju.proparse.NodeTypes;
import org.prorefactor.refactor.RefactorSession;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketListener implements Runnable {

	public SocketListener(Socket socket) {
		this.socket = socket;
	}

	Socket socket;

	ByteArrayOutputStream stringsBytes = new ByteArrayOutputStream();
	DataOutputStream strings = new DataOutputStream(stringsBytes);


	public void run() {

		DataOutputStream out = null;

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());

			String input = in.readLine();

			if (input == null)
				return;


			if (input.trim().equalsIgnoreCase("--allschema")) {

				DataXferStream xfer = allSchema();
				xfer.buildIndexes();
				out.writeInt(xfer.size());
				xfer.getBytes().writeTo(out);

			} else if (input.toLowerCase().startsWith("--buildnum")) {

				out.writeInt(BlobBuilder.getBuildNumber());

			} else if (input.toLowerCase().startsWith("--loadproject")) {

				loadProject(input);
				// Write a zero to indicate success.
				out.writeInt(0);

			} else if (input.trim().equalsIgnoreCase("--nodetypes")) {

				DataXferStream xfer = new DataXferStream();
				xfer.store(new NodeTypes());
				xfer.buildIndexes();
				out.writeInt(xfer.size());
				xfer.getBytes().writeTo(out);

			} else if (input.trim().equalsIgnoreCase("--shutdown")) {

				System.out.println("Shutdown requested by client.");
				System.exit(0);

			} else {

				BlobBuilder builder = new BlobBuilder(input);
				builder.build();

				ByteArrayOutputStream header = builder.getHeader();
				out.writeInt(header.size());
				header.writeTo(out);

				ByteArrayOutputStream blob = builder.getBlob();
				out.writeInt(blob.size());
				blob.writeTo(out);
			}

		} catch (Exception e) {
			e.printStackTrace();
			String errmsg = e.getMessage();
			if (out!=null) try {
				if (errmsg==null || errmsg.length()==0)
					errmsg = e.toString();
				byte [] bytes = errmsg.getBytes();
				out.writeInt(bytes.length * -1);
				out.write(bytes);
			} catch (Exception e2) {}
		}

	}


	/** Build an xfer blob containing schema for all transfered classes.
	 * This list is maintained by hand (sigh). Using reflection to find
	 * Xferable classes turned out to be more trouble than it was worth.
	 */
	private DataXferStream allSchema() throws Exception {
		DataXferStream xfer = new DataXferStream();
		xfer.addSchemaFor(com.joanju.proparse.ProToken.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.core.JPNode.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.core.schema.Database.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.core.schema.Field.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.core.schema.Table.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.macrolevel.IncludeRef.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.macrolevel.MacroDef.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.macrolevel.NamedMacroRef.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.nodetypes.BlockNode.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.nodetypes.FieldRefNode.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.nodetypes.ProgramRootNode.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.nodetypes.ProparseDirectiveNode.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.nodetypes.RecordNameNode.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Block.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.BufferScope.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Dataset.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Datasource.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.DataType.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.FieldBuffer.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Query.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Routine.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Stream.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.SymbolScope.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.SymbolScopeRoot.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.SymbolScopeSuper.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.TableBuffer.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.treeparser.Variable.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Browse.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Button.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Frame.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Image.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Menu.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.MenuItem.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Rectangle.class.newInstance());
		xfer.addSchemaFor(org.prorefactor.widgettypes.Submenu.class.newInstance());
		return xfer;
	}


	private void loadProject(String message) throws Exception {
		// --loadproject  is the first part of the message
		// 0123456789012
		// Strip that, and trim, to get the project name.
		String projectName = message.substring(13).trim();
		System.out.println("Proparse Server: Client requested load project: " + projectName);
		System.out.flush();
		RefactorSession.getInstance().loadProject(projectName);
	}


}
