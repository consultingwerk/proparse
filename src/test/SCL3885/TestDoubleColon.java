package test.SCL3885;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestDoubleColon extends TestCase 
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
			proparseSchema.loadSchema(new File("src/test/schema.txt").getAbsolutePath());
			
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
		File file;
		ParseUnit pu;
		
		file = new File("src/test/SCL3885/TestCode.cls");
		pu = new ParseUnit(file, "ISO8859-1");
		pu.treeParser01();
		
		this.printTree ("", pu.getTopNode());
	}
	
	private void printTree (String indent, JPNode node) 
	{
		System.out.println(indent + node.toString());
		
		if (node.firstChild() != null)
			this.printTree (indent + "  ", node.firstChild());
		if (node.nextSibling() != null)
			this.printTree (indent, node.nextSibling());
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
