package test.SCL3364;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;
import junit.framework.TestCase;

public class TestClass_VAR extends TestCase 
{
	public void test_01 () throws RefactorException
	{
		ParseUnit pu;
		
		pu = new ParseUnit();
		pu.parse("test_01", "VAR INT a, b, x = a + b, y = a - b, z = x - y.");
		pu.treeParser01();
		
		assertNotNull(pu.getTopNode());
	}
	
	public void test_02 () throws RefactorException
	{
		ParseUnit pu;
		
		pu = new ParseUnit();
		pu.parse("test_02", "VAR INT u, v. VAR INT[] w = [u + v, u - v].");
		pu.treeParser01();
		
		assertNotNull(pu.getTopNode());
	}
	
	public void test_03 () throws RefactorException
	{
		ParseUnit pu;
		
		pu = new ParseUnit();
		pu.parse("test_03", "USING Progress.Lang.Object. VAR Object x = NEW Object().");
		pu.treeParser01();
		
		assertNotNull(pu.getTopNode());
	}
	
	public void test_04 () throws RefactorException
	{
		ParseUnit pu;
		
		pu = new ParseUnit();
		pu.parse("test_04", "VAR DATETIME dtm = DATETIME(TODAY,MTIME).");
		pu.treeParser01();
		
		assertNotNull(pu.getTopNode());
	}
}
