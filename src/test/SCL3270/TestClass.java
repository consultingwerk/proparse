package test.SCL3270;

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
		
		this.file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\SCL3270\\testCode.p");
		this.pu = new ParseUnit(file);
		
		pu.treeParser01();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}
	
	public void test01_IntegerAddEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("IntegerAddEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "PLUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "4");
		
	}
	
	public void test02_IntegerSubEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("IntegerSubEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "MINUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "4");
		
	}
	
	public void test03_IntegerMulEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("IntegerMulEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "MULTIPLY_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "10");
		
	}
	
	public void test04_IntegerDivEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("IntegerDivEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "DIVIDE_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "10");
		
	}	
	
	public void test05_CharacterAddEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("CharacterAddEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "PLUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "c");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "QSTRING", "\"def\"");
		
	}
	
	public void test06_DateAddEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("DateAddEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "PLUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "dt");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "2");
		
	}	
	
	public void test07_DateSubEq() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("DateSubEq");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "MINUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "dt");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "3");
		
	}	
	
	public void test08_AssignState() {
		
		JPNode node;
		
		node = this.getProcedureCodeBlock("AssignState");
		this.assertValidAndType(node, "Code_block");
		
		node = node.findDirectChild(NodeTypes.ASSIGN);
		this.assertValidAndType(node, "ASSIGN");
		
		node = node.firstChild();
		this.assertValidAndType(node, "PLUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i1");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "1");
		
		node = node.parent().nextSibling();
		this.assertValidAndType(node, "MINUS_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i2");
		
		node = node.nextSibling();
		this.assertValidAndTypeAndText(node, "NUMBER", "1");
		
		node = node.parent().nextSibling();
		this.assertValidAndType(node, "MULTIPLY_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i3");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i1");
		
		node = node.parent().nextSibling();
		this.assertValidAndType(node, "DIVIDE_EQUAL");
		
		node = node.firstChild();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i4");
		
		node = node.nextSibling();
		this.assertValidAndType(node, "Field_ref");
		this.assertValidAndTypeAndText(node.firstChild(), "ID", "i2");
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
