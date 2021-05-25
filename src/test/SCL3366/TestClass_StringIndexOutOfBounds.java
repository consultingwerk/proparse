package test.SCL3366;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestClass_StringIndexOutOfBounds 
	extends TestCase 
{
	
	com.joanju.proparse.Environment proparseEnv = null;		
	org.prorefactor.core.schema.Schema proparseSchema = null;	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		try
		{
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\schema.txt");
			
			proparseEnv.configSet ("propath", this.getPropath());			
	
			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", "12.3");
			proparseEnv.configSet ("window-system", "MS-WINXP");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			fail("Failed to initialize Proparse: " + e.getMessage());
		}
	}

	public void test_01() throws RefactorException
	{
		File source = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop123\\UnitTests\\Consultingwerk\\RootPackageTest\\SCL2582\\TestClass.cls");
		ParseUnit pu = new ParseUnit(source, "ISO8859-1");
		JPNode node;

		pu.treeParser01();
		node = pu.getTopNode();
		
		assertNotNull(node);
		compare(source, node.toStringSourceText());
	}

	public void test_02() throws RefactorException
	{
		File source = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop123\\UnitTests\\Consultingwerk\\OeraTests\\SCL2577\\Module\\ModuleBusinessEntity.cls");
		ParseUnit pu = new ParseUnit(source, "ISO8859-1");
		JPNode node;

		pu.treeParser01();
		node = pu.getTopNode();
		
		assertNotNull(node);
		compare(source, node.toStringSourceText());
	}
	
	public void test_03() throws RefactorException
	{
		File source = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop123\\UnitTests\\Consultingwerk\\SmartComponentsTests\\SCL3337\\TestClass.cls");
		ParseUnit pu = new ParseUnit(source, "ISO8859-1");
		JPNode node;

		pu.treeParser01();
		node = pu.getTopNode();
		
		assertNotNull(node);
		compare(source, node.toStringSourceText());
	}
	
	public void test_04() throws RefactorException
	{
		File source = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop123\\UnitTests\\Consultingwerk\\RestAppServerTests\\MockRestServerSessionActivator.cls");
		ParseUnit pu = new ParseUnit(source, "ISO8859-1");
		JPNode node;

		pu.treeParser01();
		node = pu.getTopNode();
		
		assertNotNull(node);
		compare(source, node.toStringSourceText());
	}
	
	private void compare(File orig, String copy)
	{
		BufferedReader brOrig;
		BufferedReader brCopy;
		
		String lineOrig;
		String lineCopy;
		
		assertNotNull(orig);
		assertTrue(orig.canRead());
		assertNotNull(copy);
		
		try 
		{
			brOrig = new BufferedReader(new FileReader(orig));
			brCopy = new BufferedReader(new StringReader(copy));
			
			assertNotNull(brOrig);
			assertNotNull(brCopy);
			assertEquals(brOrig.lines().count(), brCopy.lines().count());
			
			lineOrig = brOrig.readLine();
			lineCopy = brCopy.readLine();
			while(lineOrig != null && lineCopy != null)
			{
				assertEquals(lineOrig, lineCopy);
				
				lineOrig = brOrig.readLine();
				lineCopy = brCopy.readLine();
			}
		} 
		catch (IOException e) 
		{
			fail("File not found: " + e.getMessage());
		}
	}
	
	private String getPropath()
	{
		String propath = "";
		BufferedReader br;
		
		try
		{
			br = new BufferedReader(new FileReader(new File("src/test/propath.txt")));
			for(String line = br.readLine(); line != null; line = br.readLine())
				propath += line;
			
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			fail("Could not get PROPATH!");
		}
		
		return propath;
	}
}
