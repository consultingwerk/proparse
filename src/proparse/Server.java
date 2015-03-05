/** July 2008 by John Green
 *
 * Copyright (C) 2008 Joanju Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package proparse;

import com.joanju.proparse.sockets.SocketListener;
import org.prorefactor.refactor.RefactorSession;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileFilter;
import java.util.Properties;

public class Server {

	private static int port = 55001;
	private static String projectName = null;



	public static void main(String[] args) {
		try {
			loadProperties();
			RefactorSession rsession = RefactorSession.getInstance();
			if (projectName!=null && projectName.length()!=0) {
				System.out.println("Proparse Server is loading project " + projectName);
				rsession.loadProject(projectName);
			} else {
				System.out.println("Proparse Server not configured with any project.");
			}
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Proparse Server is listening on socket " + port);
			while (true) {
				Socket socket = serverSocket.accept();
				SocketListener listener = new SocketListener(socket);
				Thread thread = new Thread(listener);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/** Returns the first project directory found, if any. */
	private static String findFirstProjectName() {
		File projectSettingsDir = new File("./prorefactor/projects");
		if (! projectSettingsDir.exists())
			return null;
		FileFilter filter = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}
		};
		File [] dirs = projectSettingsDir.listFiles(filter);
		if (dirs.length==0)
			return null;
		return dirs[0].getName();
	}


	private static void loadProperties() throws IOException {
		File propsfile = new File("proparseserver.properties");
		if (! propsfile.exists()) {
			projectName = findFirstProjectName();
			return;
		}
		Properties props = new Properties();
		props.load(new FileInputStream(propsfile));
		String portString = props.getProperty("port");
		if (portString!=null) {
			port = Integer.parseInt(portString);
		}
		String projectString = props.getProperty("project");
		if (projectString!=null && projectString.length()>0)
			projectName = projectString;
		else
			projectName = findFirstProjectName();
	}


}


