package test.SCL3260;

import java.io.File;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestClass2 extends TestCase {

	private File file;
	private ParseUnit pu;
	
	protected void setUp() throws Exception {
		
		super.setUp();
		
		this.file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3260\\test-procedure.p");
		this.pu = new ParseUnit(file);
		
		pu.treeParser01();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}
	
	public void test01_FullName() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("test-internal");
		this.assertValidAndType(node, "Code_block");
		
		node = node.query("ASSIGN")[0];
		
		assertEquals(node.firstChild().firstChild().getSymbol().fullName(), "i");
		assertNotNull("not valid symbol n", node.firstChild().firstChild().nextSibling().getSymbol());		
		assertEquals(node.firstChild().firstChild().nextSibling().getSymbol().fullName(), "n");		
	}
	
	public void test02_defineNode() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("test-internal");
		this.assertValidAndType(node, "Code_block");
		
		node = node.query("ASSIGN")[0];
		
		assertNotNull("DEFINE VARIABLE i", node.firstChild().firstChild().getSymbol().getDefineNode());
		assertNotNull("not valid symbol n", node.firstChild().firstChild().nextSibling().getSymbol());		
		assertNotNull("var n", node.firstChild().firstChild().nextSibling().getSymbol().getDefineNode());		
	}
	
	private void assertValidAndType(JPNode node, String type) {
		Assert.assertNotNull(("Invalid " + type + " node!"), node);
		Assert.assertEquals((type + " node has wrong type!"), NodeTypes.getTypeNum(type), node.getType());
	}
	
	private void assertValidAndTypeAndText(JPNode node, String type, String text) {
		this.assertValidAndType(node, type);
		Assert.assertEquals(type + " node has wrong text!", text, node.getText());
	}
	
	private JPNode getProcedureCodeBlock(String procName) {
		JPNode node;
		JPNode procID;
		
		if(this.pu != null){
			node = this.pu.getTopNode();
			for(JPNode childNode: node.getDirectChildren()) {
				if(NodeTypes.getTypeName(childNode.getType()).equals("PROCEDURE")) {
					procID = childNode.findDirectChild(NodeTypes.ID);				
					if (procID != null && procID.getText().equals(procName)) 
						return childNode.findDirectChild(NodeTypes.Code_block);
				}
			}
		}
		
		return null;
	}
}
