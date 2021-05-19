package test.SCL3366;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
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
		File target = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3366\\testCode_copy.p");
		FileWriter fw = new FileWriter(target);
		
		node = pu.getTopNode();
		try{
			txt = node.toStringSourceText();
	
			fw.write(txt);
			fw.flush();
			fw.close();
		} catch (RefactorException e) {
			System.out.println("Caught RefactorException: " + e.getMessage());
			for(StackTraceElement ste: e.getStackTrace())
			{
				System.out.println(ste.toString());
			}
		}
	}
}
