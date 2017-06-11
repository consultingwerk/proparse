package org.prorefactor.core.unittest;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

public class TestMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File directory = new File("data/newsyntax/11.4");
		String [] extensions = {"p"};
		Collection files = FileUtils.listFiles(directory, extensions, true);
		for (Iterator it = files.iterator(); it.hasNext();) {
			File file = (File) it.next();
			System.out.println("Parse: " + file.getAbsolutePath());
			
			ParseUnit pu = new ParseUnit(file);
			try {
				//pu.treeParser(new JPTreeParser());
				pu.treeParser01();
			} catch (RefactorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
