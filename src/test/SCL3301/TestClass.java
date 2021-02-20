package test.SCL3301;

import java.io.File;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
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
		proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3301\\schema.txt");
		
		proparseEnv.configSet ("propath", "C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL");			

		proparseEnv.configSet ("batch-mode", "false");
		proparseEnv.configSet ("opsys", "WIN32");
		proparseEnv.configSet ("proversion", "12.2");
		proparseEnv.configSet ("window-system", "MS-WINXP");						
		
		File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3301\\test-code.p");
		ParseUnit pu = new ParseUnit(file);
		
		pu.treeParser01();
		
	}	
	
}
