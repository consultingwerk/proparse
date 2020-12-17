package test.SCL3260;

import java.io.File;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestClass extends TestCase {

	private File file;
	private ParseUnit pu;
	
	protected void setUp() throws Exception {
		
		super.setUp();
		
		this.file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3260\\testCode.p");
		this.pu = new ParseUnit(file);
		
		pu.treeParser01();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}
	
	// TODO remove test
	public void test00_print() {
		this.print(this.pu.getTopNode(), "");
	}
	
	public void test01_SingleVariable() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("SingleVariable");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "CHARACTER");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "cVar");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "PERIOD");
	}
	
	public void test02_MultiVariable() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("MultiVariable");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "INTEGER");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "iVar1");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "COMMA");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "iVar2");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "COMMA");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "iVar3");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "PERIOD");
	}
	
	public void test03_InitVariable() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("InitVariable");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "CHARACTER");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "cVar");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "EQUAL");
		
		this.assertValidAndTypeAndText(node.firstChild(), "QSTRING", "\"Test\":U");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "PERIOD");
	}
	
	public void test04_UndefArray() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("UndefArray");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "CHARACTER");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "LEFTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "RIGHTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "cVar");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "PERIOD");
	}
	
	public void test05_DefArray() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("DefArray");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "INTEGER");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "LEFTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "5");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "RIGHTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "iVar");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "PERIOD");
	}
	
	public void test06_InitArray() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("InitArray");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "INTEGER");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "LEFTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "RIGHTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "iVar");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "LEFTBRACE");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "1");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "COMMA");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "2");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "COMMA");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "3");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "RIGHTBRACE");
		
		node = node.parent().nextSibling();
		this.assertValidAndType(node, "PERIOD");
	}
	
	public void test07_DefVar() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("DefVar");
		this.assertValidAndType(node, "Code_block");
		
		node = node.firstChild();
		this.assertValidAndType(node, "DEFINE");
		
		node = node.firstChild();
		this.assertValidAndType(node, "VARIABLE");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "ID", "i");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "AS");
		
		this.assertValidAndType(node.firstChild(), "INTEGER");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "NOUNDO");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "PERIOD");
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
	
	// TODO remove method
	private void print(JPNode node, String format) {
		System.out.println(format + node.toString());
		if(node.firstChild() != null) 
			this.print(node.firstChild(), format + "\t");
		if (node.nextSibling() != null)
			this.print(node.nextSibling(), format);
	}
}
