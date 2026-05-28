package test.SCL5228;

import java.io.File;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

public class TestOnStatement
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

	public void testParseOnDeleteOfThisProcedureAndCustomer () throws RefactorException {
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

	public void testParseOnEventlistVariants () throws RefactorException {
		ParseUnit pu = new ParseUnit (new File ("src/test/SCL5228/on_eventlist.p"), "ISO8859-1");
		Assert.assertNotNull("Failed to create ParseUnit", pu);
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse on_eventlist.p", pu.getTopNode());

		JPNode topNode = pu.getTopNode();
		JPNode onNode = topNode.findDirectChild(com.joanju.proparse.NodeTypes.ON);

		// 1: ON CLOSE OF THIS-PROCEDURE DO: END.
		Assert.assertNotNull("ON node 1 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE OF THIS-PROCEDURE");
		assertWidgetRefThisProcedure(onNode, "ON CLOSE OF THIS-PROCEDURE");

		// 2: ON DELETE OF THIS-PROCEDURE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 2 not found", onNode);
		assertEventlistPath(onNode, "ON DELETE OF THIS-PROCEDURE");
		assertWidgetRefThisProcedure(onNode, "ON DELETE OF THIS-PROCEDURE");

		// 3: ON U1 OF THIS-PROCEDURE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 3 not found", onNode);
		assertEventlistPath(onNode, "ON U1 OF THIS-PROCEDURE");
		assertWidgetRefThisProcedure(onNode, "ON U1 OF THIS-PROCEDURE");

		// 4: ON U1 ANYWHERE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 4 not found", onNode);
		assertEventlistPath(onNode, "ON U1 ANYWHERE");
		Assert.assertNotNull("ON U1 ANYWHERE should have ANYWHERE",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.ANYWHERE));

		// 5: ON CLOSE, U1 OF THIS-PROCEDURE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 5 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE, U1 OF THIS-PROCEDURE");
		assertWidgetRefThisProcedure(onNode, "ON CLOSE, U1 OF THIS-PROCEDURE");

		// 6: ON CLOSE OF THIS-PROCEDURE OR U1 OF THIS-PROCEDURE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 6 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE OF THIS-PROCEDURE OR U1 OF THIS-PROCEDURE");
		Assert.assertNotNull("ON CLOSE..OR..should have OR",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.OR));

		// 7: ON CLOSE OF THIS-PROCEDURE ANYWHERE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 7 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE OF THIS-PROCEDURE ANYWHERE");
		assertWidgetRefThisProcedure(onNode, "ON CLOSE OF THIS-PROCEDURE ANYWHERE");
		Assert.assertNotNull("ON CLOSE..ANYWHERE should have ANYWHERE",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.ANYWHERE));

		// 8: ON CLOSE OF THIS-PROCEDURE REVERT.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 8 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE OF THIS-PROCEDURE REVERT");
		Assert.assertNotNull("ON CLOSE..REVERT should have REVERT",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.REVERT));

		// 9: ON CLOSE OF THIS-PROCEDURE PERSISTENT RUN myproc.p.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 9 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE..PERSISTENT RUN");
		Assert.assertNotNull("ON CLOSE..PERSISTENT RUN should have PERSISTENT",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.PERSISTENT));
		Assert.assertNotNull("ON CLOSE..PERSISTENT RUN should have RUN",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.RUN));

		// 10: ON CLOSE OF THIS-PROCEDURE PERSISTENT RUN myproc.p ("param1").
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 10 not found", onNode);
		assertEventlistPath(onNode, "ON CLOSE..PERSISTENT RUN with params");
		Assert.assertNotNull("ON CLOSE..PERSISTENT RUN(params) should have PERSISTENT",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.PERSISTENT));
		Assert.assertNotNull("ON CLOSE..PERSISTENT RUN(params) should have RUN",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.RUN));
	}

	public void testParseOnDbeventVariants () throws RefactorException {
		ParseUnit pu = new ParseUnit (new File ("src/test/SCL5228/on_dbevent.p"), "ISO8859-1");
		Assert.assertNotNull("Failed to create ParseUnit", pu);
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse on_dbevent.p", pu.getTopNode());

		JPNode topNode = pu.getTopNode();
		JPNode onNode = topNode.findDirectChild(com.joanju.proparse.NodeTypes.ON);

		// 1: ON CREATE OF Customer DO: END.
		Assert.assertNotNull("ON node 1 not found", onNode);
		assertDbeventPath(onNode, "ON CREATE OF Customer");
		Assert.assertNotNull("ON CREATE should have CREATE",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.CREATE));
		assertRecordName(onNode, "Customer", "ON CREATE OF Customer");

		// 2: ON DELETE OF Customer DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 2 not found", onNode);
		assertDbeventPath(onNode, "ON DELETE OF Customer");
		Assert.assertNotNull("ON DELETE should have DELETE_KW",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.DELETE_KW));
		assertRecordName(onNode, "Customer", "ON DELETE OF Customer");

		// 3: ON FIND OF Customer DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 3 not found", onNode);
		assertDbeventPath(onNode, "ON FIND OF Customer");
		Assert.assertNotNull("ON FIND should have FIND",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.FIND));
		assertRecordName(onNode, "Customer", "ON FIND OF Customer");

		// 4: ON WRITE OF Customer DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 4 not found", onNode);
		assertDbeventPath(onNode, "ON WRITE OF Customer");
		Assert.assertNotNull("ON WRITE should have WRITE",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.WRITE));
		assertRecordName(onNode, "Customer", "ON WRITE OF Customer");

		// 5: ON WRITE OF Customer NEW new-cust OLD old-cust DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 5 not found", onNode);
		assertDbeventPath(onNode, "ON WRITE OF Customer NEW..OLD");
		Assert.assertNotNull("ON WRITE..NEW..OLD should have NEW",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.NEW));
		Assert.assertNotNull("ON WRITE..NEW..OLD should have OLD",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.OLD));

		// 6: ON WRITE OF Customer NEW BUFFER new-cust OLD BUFFER old-cust DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 6 not found", onNode);
		assertDbeventPath(onNode, "ON WRITE OF Customer NEW BUFFER..OLD BUFFER");
		Assert.assertNotNull("ON WRITE..NEW BUFFER..OLD BUFFER should have NEW",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.NEW));
		Assert.assertNotNull("ON WRITE..NEW BUFFER..OLD BUFFER should have OLD",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.OLD));

		// 7: ON ASSIGN OF Customer.CustNum DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 7 not found", onNode);
		assertDbeventPath(onNode, "ON ASSIGN OF Customer.CustNum");
		Assert.assertNotNull("ON ASSIGN should have ASSIGN",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.ASSIGN));

		// 8: ON ASSIGN OF Customer.CustNum OLD VALUE old-val DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 8 not found", onNode);
		assertDbeventPath(onNode, "ON ASSIGN OF Customer.CustNum OLD VALUE");
		Assert.assertNotNull("ON ASSIGN..OLD VALUE should have OLD",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.OLD));

		// 9: ON DELETE OF Customer OVERRIDE DO: END.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 9 not found", onNode);
		assertDbeventPath(onNode, "ON DELETE OF Customer OVERRIDE");
		Assert.assertNotNull("ON DELETE..OVERRIDE should have OVERRIDE",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.OVERRIDE));

		// 10: ON DELETE OF Customer REVERT.
		onNode = nextOnNode(onNode);
		Assert.assertNotNull("ON node 10 not found", onNode);
		assertDbeventPath(onNode, "ON DELETE OF Customer REVERT");
		Assert.assertNotNull("ON DELETE..REVERT should have REVERT",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.REVERT));
	}

	public void testParseOnKeylabelVariants () throws RefactorException {
		ParseUnit pu = new ParseUnit (new File ("src/test/SCL5228/on_keylabel.p"), "ISO8859-1");
		Assert.assertNotNull("Failed to create ParseUnit", pu);
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse on_keylabel.p", pu.getTopNode());

		JPNode topNode = pu.getTopNode();
		JPNode onNode = topNode.findDirectChild(com.joanju.proparse.NodeTypes.ON);

		// Key-label statements should NOT have Event_list, Widget_ref, or RECORD_NAME
		// They are simple: ON -> key-label -> key-function -> state_end
		for (int i = 1; i <= 5; i++) {
			Assert.assertNotNull("ON keylabel node " + i + " not found", onNode);
			Assert.assertNull("ON keylabel " + i + " should not have Event_list",
				onNode.findDirectChild(com.joanju.proparse.NodeTypes.Event_list));
			Assert.assertNull("ON keylabel " + i + " should not have Widget_ref",
				onNode.findDirectChild(com.joanju.proparse.NodeTypes.Widget_ref));
			Assert.assertNull("ON keylabel " + i + " should not have RECORD_NAME",
				onNode.findDirectChild(com.joanju.proparse.NodeTypes.RECORD_NAME));
			if (i < 5)
				onNode = nextOnNode(onNode);
		}
	}

	// --- Helper methods ---

	private JPNode nextOnNode (JPNode current) {
		JPNode node = current.nextSibling();
		while (node != null && node.getType() != com.joanju.proparse.NodeTypes.ON) {
			node = node.nextSibling();
		}
		return node;
	}

	private void assertEventlistPath (JPNode onNode, String context) {
		Assert.assertNotNull(context + ": should have Event_list",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.Event_list));
		Assert.assertNull(context + ": should NOT have RECORD_NAME",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.RECORD_NAME));
	}

	private void assertDbeventPath (JPNode onNode, String context) {
		Assert.assertNull(context + ": should NOT have Event_list",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.Event_list));
		Assert.assertNull(context + ": should NOT have Widget_ref",
			onNode.findDirectChild(com.joanju.proparse.NodeTypes.Widget_ref));
	}

	private void assertWidgetRefThisProcedure (JPNode onNode, String context) {
		JPNode widgetRef = onNode.findDirectChild(com.joanju.proparse.NodeTypes.Widget_ref);
		Assert.assertNotNull(context + ": should have Widget_ref", widgetRef);
		JPNode thisProcNode = widgetRef.findDirectChild(com.joanju.proparse.NodeTypes.THISPROCEDURE);
		Assert.assertNotNull(context + ": Widget_ref should contain THISPROCEDURE", thisProcNode);
	}

	private void assertRecordName (JPNode onNode, String expectedName, String context) {
		JPNode recordName = onNode.findDirectChild(com.joanju.proparse.NodeTypes.RECORD_NAME);
		Assert.assertNotNull(context + ": should have RECORD_NAME", recordName);
		Assert.assertEquals(context + ": RECORD_NAME text", expectedName, recordName.getText());
	}
}
