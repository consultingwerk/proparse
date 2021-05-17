package test.SCL3366;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

import junit.framework.TestCase;

public class TestClass extends TestCase {

	private File source;
	private ParseUnit pu;
	
	protected void setUp() throws Exception {
		
		super.setUp();
		
		this.source = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3366\\testCode.p");
		this.pu = new ParseUnit(source);
		
		pu.treeParser01();
	}	
	
	public void test() throws IOException
	{
		JPNode node;
		String txt;
		String txt_source;
		String txt_target;
		File target = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3366\\testCode_copy.p");
		FileWriter fw = new FileWriter(target);
		BufferedReader br_source = new BufferedReader(new FileReader(this.source));
		BufferedReader br_target = new BufferedReader(new FileReader(target));
		
		node = pu.getTopNode();
		txt = node.toStringSourceText();

		fw.write(txt);
		fw.flush();
		fw.close();
		
		txt_source = br_source.readLine();
		txt_target = br_target.readLine();
		while(txt_source != null && txt_target != null)
		{
			assertEquals(txt_source, txt_target);
			
			txt_source = br_source.readLine();
			txt_target = br_target.readLine();
		}
		assertNull(txt_source);
		assertNull(txt_target);
		
		br_source.close();
		br_target.close();
	}
}
