package test.SCL3369;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestClass extends TestCase 
{
	public void test_01()
	{
		ParseUnit pu;
		String text = "VAR INT a, b, x = a + b, y = a - b, z = x - y.";
		try 
		{
			pu = new ParseUnit();
			pu.parse(text, text);
			
			assertNotNull(pu);
			assertNotNull(pu.getTopNode());
		} 
		catch (RefactorException e) 
		{
			fail("Parsing failed with: " + e.getMessage());
		}
	}
	
	public void test_02()
	{
		ParseUnit pu;
		String text = "VAR INT u, v. VAR INT[] w = [u + v, u - v].";
		try 
		{
			pu = new ParseUnit();
			pu.parse(text, text);
			
			assertNotNull(pu);
			assertNotNull(pu.getTopNode());
		} 
		catch (RefactorException e) 
		{
			fail("Parsing failed with: " + e.getMessage());
		}
	}
	
	public void test_03()
	{
		ParseUnit pu;
		String text = "USING Progress.Lang.Object. VAR Object x = NEW Object().";
		try 
		{
			pu = new ParseUnit();
			pu.parse(text, text);
			
			assertNotNull(pu);
			assertNotNull(pu.getTopNode());
		} 
		catch (RefactorException e) 
		{
			fail("Parsing failed with: " + e.getMessage());
		}
	}
	
	public void test_04()
	{
		ParseUnit pu;
		String text = "VAR DATETIME dtm = DATETIME(TODAY,MTIME).";
		try 
		{
			pu = new ParseUnit();
			pu.parse(text, text);
			
			assertNotNull(pu);
			assertNotNull(pu.getTopNode());
		} 
		catch (RefactorException e) 
		{
			fail("Parsing failed with: " + e.getMessage() + "at" + e.getStackTrace());
		}
	}
}
