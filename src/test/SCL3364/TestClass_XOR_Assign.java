package test.SCL3364;

import java.io.File;
import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;
import junit.framework.TestCase;

public class TestClass_XOR_Assign extends TestCase 
{
	private File file;
	private ParseUnit pu;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		
		this.file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3364\\testCode_XOR_Assign.p");
		this.pu = new ParseUnit(file);
		
		this.pu.treeParser01();
	}

	public void test_01 ()
	{
		JPNode node;
		
		assertNotNull(this.pu);
		
		node = this.pu.getTopNode();
		assertNotNull(node);
	}
}
