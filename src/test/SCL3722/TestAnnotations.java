package test.SCL3722;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestAnnotations extends TestCase {

	private com.joanju.proparse.Environment proparseEnv = null;		
	private org.prorefactor.core.schema.Schema proparseSchema = null;
	
	private JPNode node = null;
	
	protected void setUp() throws Exception {
		
		ParseUnit pu;
		File file;
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
			
			file = new File("C:\\Work\\Proparse\\GitHub\\proparse\\src\\test\\SCL3722\\test_code.cls");

			pu = new ParseUnit(file, "ISO8859-1");
			pu.treeParser01();
			
			node = pu.getTopNode();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Failed to initialize Proparse: " + e.getMessage());
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
	
	/**
	 * Tests class annotation with two attributes
	 */
	public void testTwoAttributes ()
	{
		JPNode anno;
		
		Assert.assertNotNull(this.node);
		
		anno = this.node.findDirectChild(NodeTypes.ANNOTATION);
		Assert.assertNotNull(anno);
		
		anno = anno.firstChild();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.FILE, anno.getType());
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.LEFTPAREN, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ID, anno.getType());
		Assert.assertEquals("type", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.EQUAL, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.QSTRING, anno.getType());
		Assert.assertEquals("\"REST\"", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.COMMA, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ID, anno.getType());
		Assert.assertEquals("executionMode", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.EQUAL, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.QSTRING, anno.getType());
		Assert.assertEquals("\"singleton\"", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.COMMA, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ID, anno.getType());
		Assert.assertEquals("useReturnValue", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.EQUAL, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.QSTRING, anno.getType());
		Assert.assertEquals("\"false\"", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.COMMA, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ID, anno.getType());
		Assert.assertEquals("writeDataSetBeforeImage", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.EQUAL, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.QSTRING, anno.getType());
		Assert.assertEquals("\"false\"", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.COMMA, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ID, anno.getType());
		Assert.assertEquals("display", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.EQUAL, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.QSTRING, anno.getType());
		Assert.assertEquals("\"test\"", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.RIGHTPAREN, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.PERIOD, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNull(anno);
	}
	
	/**
	 * Tests method annotation with one attribute
	 */
	public void testOneAttribute ()
	{
		JPNode anno;
		
		Assert.assertNotNull(this.node);
		
		anno = this.node.findDirectChild(NodeTypes.CLASS);
		Assert.assertNotNull(anno);
		anno = anno.findDirectChild(NodeTypes.Code_block);
		Assert.assertNotNull(anno);
		anno = anno.firstChild();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ANNOTATION, anno.getType());
		
		anno = anno.firstChild();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.LEFTPAREN, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ID, anno.getType());
		Assert.assertEquals("message", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.EQUAL, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.QSTRING, anno.getType());
		Assert.assertEquals("\"single attribute\"", anno.getText());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.RIGHTPAREN, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.PERIOD, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNull(anno);
	}
	
	/**
	 * Tests method annotation without attributes
	 */
	public void testNoAttribute ()
	{
		JPNode anno;
		
		Assert.assertNotNull(this.node);
		
		anno = this.node.findDirectChild(NodeTypes.CLASS);
		Assert.assertNotNull(anno);
		anno = anno.findDirectChild(NodeTypes.Code_block);
		Assert.assertNotNull(anno);
		anno = anno.firstChild();
		Assert.assertNotNull(anno);
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		anno = anno.nextSibling();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.ANNOTATION, anno.getType());
		
		anno = anno.firstChild();
		Assert.assertNotNull(anno);
		Assert.assertEquals(NodeTypes.PERIOD, anno.getType());
		
		anno = anno.nextSibling();
		Assert.assertNull(anno);
	}
}
