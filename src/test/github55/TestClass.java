package test.github55;

import java.io.File;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestClass extends TestCase {

	public void test() throws Exception {

		com.joanju.proparse.Environment proparseEnv = null;		
		org.prorefactor.core.schema.Schema proparseSchema = null;		
		
		proparseEnv = com.joanju.proparse.Environment.instance();
		proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
		
		proparseSchema.clear();
		
		proparseEnv.configSet ("batch-mode", "false");
		proparseEnv.configSet ("opsys", "WIN32");
		proparseEnv.configSet ("proversion", "12.2");
		proparseEnv.configSet ("window-system", "MS-WINXP");						
		
		
		File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\github55\\testCode.p");
		ParseUnit pu = new ParseUnit(file);
		
		pu.treeParser01();
		
	}	}
