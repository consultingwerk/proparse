/* TreePrint.groovy
November 2008 by John Green

Copyright (C) 2008-2009 Joanju Software (www.joanju.com). All rights reserved.
This file is made available under the terms of the Eclipse Public License v1.0.
*/
package proparse

import java.io.File;
import com.joanju.scripting.Logger
import com.joanju.scripting.ScriptSupport
import org.apache.tools.ant.util.FileUtils;
import org.prorefactor.refactor.RefactorSession
import org.prorefactor.treeparser.ParseUnit


/** Runnable class for printing a Proparse AST to the console.
 * Args: project name, file name
 */
class TreePrint{


	public static void main(String [] args) {
		if (args.length < 2) {
			println('''Usage: TreePrint <projectName> <fileName>''')
			return
		}
		new TreePrint().run(args[0], args[1])
	}

	int indent = 0



	void run(String projectName, String fileName) {
		RefactorSession prsession = RefactorSession.getInstance()
		
		// Disable parser output.
		prsession.setProjectBinariesEnabledOff();

		prsession.loadProject(projectName)
		File f = new File(fileName)
		if (! f.exists()) {
			println('Could not find file: ' + fileName)
			return
		}
		ParseUnit pu = new ParseUnit(f)
		pu.loadOrBuildPUB()
		walker(pu.topNode)
	}


	void walker(node) {
		if (!node)
			return
		println '  ' * indent + node
		indent++
		walker(node.firstChild())
		indent--
		walker(node.nextSibling())
	}


}
