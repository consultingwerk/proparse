package test.SCL3370;

import java.io.File;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestClass extends TestCase 
{
	public void test() throws RefactorException
	{
		File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3370\\testCode_XOR_Assign.txt");
		ParseUnit pu = new ParseUnit(file);
		
		pu.treeParser01();

		assertNotNull(pu);
		assertNotNull(pu.getTopNode());
	}
}
