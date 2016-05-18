package test;

import java.io.*;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.*;

import junit.framework.TestCase;

public class UnitTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParseUnit() {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\test.cls");
	
		ParseUnit pu = new ParseUnit(file);
	
		try {
			pu.treeParser01();
		} catch (RefactorException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
