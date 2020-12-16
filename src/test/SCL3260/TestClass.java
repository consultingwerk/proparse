package test.SCL3260;

import java.io.File;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestClass extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	// For now just attempts to iterate through the tree and print the file
	public void testMethod01() throws RefactorException {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3260\\testCode.p");
	
		ParseUnit pu = new ParseUnit(file);
		pu.treeParser01();
		JPNode node = pu.getTopNode();
		
		this.print(node, "");
	}
	
	private void print(JPNode node, String format) {
		System.out.println(format + node.toString());
		
		if(node.firstChild() != null) 
			this.print(node.firstChild(), format + "\t");
		if (node.nextSibling() != null)
			this.print(node.nextSibling(), format);
	}
}
