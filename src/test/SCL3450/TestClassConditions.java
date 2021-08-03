package test.SCL3450;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestClassConditions extends TestCase 
{
	private com.joanju.proparse.Environment proparseEnv = null;		
	private org.prorefactor.core.schema.Schema proparseSchema = null;
	
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
	
	public void test_01() throws IOException, RefactorException
	{
		File source;
		ParseUnit pu;
		
		JPNode node;
		String txt;
		File target;
		FileWriter fw;
		
		source = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3450\\test_cond.p");

		target = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3450\\test_cond_copy.p");
		fw = new FileWriter(target);

		pu = new ParseUnit(source, "ISO8859-1");
		pu.treeParser01();
		
		node = pu.getTopNode();

		txt = node.toStringSourceText();
	
		fw.write(txt);
		fw.flush();
		fw.close();
		
		this.compare(source, txt);
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
		
		int i = 0;
		
		try 
		{
			brOrig = new BufferedReader(new FileReader(orig));
			brCopy = new BufferedReader(new StringReader(copy));
			
			assertNotNull(brOrig);
			assertNotNull(brCopy);
			
			lineOrig = brOrig.readLine();
			lineCopy = brCopy.readLine();

			while(lineOrig != null && lineCopy != null)
			{
				assertEquals("Error in Line " + i , lineOrig, lineCopy);
				
				lineOrig = brOrig.readLine();
				lineCopy = brCopy.readLine();
				i++;
			}
			assertNull(lineOrig);
			assertNull(lineCopy);
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
