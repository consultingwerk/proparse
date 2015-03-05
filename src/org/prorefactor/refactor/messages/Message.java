/* Message.java
 * Created on Dec 18, 2003
 * John Green
 *
 * Copyright (C) 2003 Joanju Limited
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor.messages;

import java.io.File;


/** General purpose message holder, for messages from a
 * refactoring to the programmer/user.
 * Initial implementation designed around messages which
 * refer to a file and line number.
 */
public class Message {
	
	public int line;
	public int column;
	public File file;
	public String message;

}
