package test.SCL5228;

import java.io.File;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

public class OnDeleteOfThisProcedure
	extends ProparseTestCase {

	public void setUp () throws Exception {
		super.setUp();
	}

	@Override
	protected String getProversion() {
		return "12.8";
	}

	@Override
	protected String getPropathFileName() {
		return "src/test/propath_128.txt";
	}

	public void testParse () {
		File original;
		ParseUnit pu;

		original = new File ("src/test/SCL5228/procedure.p");

		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull("Failed to create ParseUnit", pu);
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse", pu.getTopNode());

		// Verify both ON blocks parsed correctly
		JPNode topNode = pu.getTopNode();
		JPNode onNode1 = topNode.findDirectChild(com.joanju.proparse.NodeTypes.ON);
		Assert.assertNotNull("First ON block not found", onNode1);

		// First ON DELETE OF THIS-PROCEDURE should parse via eventlist path
		// Tree structure: ON -> Event_list -> DELETE, ON -> Widget_ref -> THISPROCEDURE
		JPNode widgetRef1 = onNode1.findDirectChild(com.joanju.proparse.NodeTypes.Widget_ref);
		Assert.assertNotNull("First ON should have Widget_ref (THIS-PROCEDURE path)", widgetRef1);
		JPNode thisProcNode = widgetRef1.findDirectChild(com.joanju.proparse.NodeTypes.THISPROCEDURE);
		Assert.assertNotNull("Widget_ref should contain THISPROCEDURE", thisProcNode);

		// Find the second ON block (Customer)
		JPNode onNode2 = onNode1.nextSibling();
		while (onNode2 != null && onNode2.getType() != com.joanju.proparse.NodeTypes.ON) {
			onNode2 = onNode2.nextSibling();
		}
		Assert.assertNotNull("Second ON block not found", onNode2);

		// Second ON DELETE OF Customer should parse via dbevent path
		// Tree structure: ON -> DELETE, ON -> OF, ON -> RECORD_NAME
		JPNode recordNameNode = onNode2.findDirectChild(com.joanju.proparse.NodeTypes.RECORD_NAME);
		Assert.assertNotNull("Second ON should have RECORD_NAME (Customer table)", recordNameNode);
		Assert.assertEquals("Customer", recordNameNode.getText());
	}
}
