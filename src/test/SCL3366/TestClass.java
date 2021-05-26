package test.SCL3366;

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

public class TestClass extends TestCase {

	private File source;
	private ParseUnit pu;
	
	private com.joanju.proparse.Environment proparseEnv = null;		
	private org.prorefactor.core.schema.Schema proparseSchema = null;
	
	protected void setUp() throws Exception {
		
		super.setUp();
		
		this.source = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3366\\testCode.p");
		
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
		
		this.pu = new ParseUnit(source);
		
		pu.treeParser01();
	}	
	
	public void test() throws IOException
	{
		JPNode node;
		String txt;
		File target = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3366\\testCode_copy.p");
		FileWriter fw = new FileWriter(target);
		
		node = pu.getTopNode();
		try{
			txt = node.toStringSourceText();
	
			fw.write(txt);
			fw.flush();
			fw.close();
			
			this.compare(source, txt);
		} 
		catch (RefactorException e) 
		{
			System.out.println("Caught RefactorException: " + e.getMessage());
			for(StackTraceElement ste: e.getStackTrace())
			{
				System.out.println(ste.toString());
			}
		}
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
