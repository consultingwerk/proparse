package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

public abstract class ProparseTestCase 
	extends TestCase 
{
	protected com.joanju.proparse.Environment proparseEnv = null;		
	protected org.prorefactor.core.schema.Schema proparseSchema = null;
	
	protected void setUp() throws Exception {
		
		super.setUp();
		
		try
		{
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema(new File(this.getSchemaFileName()).getAbsolutePath());
			
			proparseEnv.configSet ("propath", this.getPropath());			
	
			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", this.getProversion());
			proparseEnv.configSet ("window-system", "MS-WINXP");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			fail("Failed to initialize Proparse: " + e.getMessage());
		}
	}
	
	protected String getSchemaFileName ()
	{
		return "src/test/schema.txt";
	}
	
	protected String getProversion ()
	{
		return "12.3";
	}

	protected String getPropath()
	{
		String propath = "";
		BufferedReader br;
		
		try
		{
			br = new BufferedReader(new FileReader(new File(this.getPropathFileName())));
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
	
	protected String getPropathFileName ()
	{
		return "src/test/propath.txt";
	}
}
