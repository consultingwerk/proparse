/* Created on Mar 26, 2006
 * John Green
 *
 * Copyright (C) 2006 Joanju Software
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.refactor;

import java.io.File;


/** An interface to the IDE from ProRefactor.
 * Other than the user interface, ProRefactor is independent
 * of Eclipse. However, there are a few services that any IDE
 * (even one other than Eclipse) should provide. An IDEDefault
 * is provided within ProRefactor for testing and for simple
 * environments. ProRefactor's Eclipse component also provides
 * one of these, so that some Eclipse services can be visible
 * without ProRefactor becoming dependent on Eclipse.
 * 
 * If ProRefactor is ever used as a component to another IDE,
 * then that IDE should implement and provide this IDE
 * interface.
 */
public interface IDE {

	/** Find the project name and the project relative path for a File object.
	 * The return is an array of two strings. The first entry is the project
	 * name, ex: "projectName". The second entry is the relative path, ex:
	 * "src/com/joanju/Example.cls".
	 * @return Must never return null. The project name may be anything
	 * including null. The relative path must never be null.
	 */
	String [] getProjectRelativePath(File file);
	
}
