package test.SCL6077;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import test.ProparseTestCase;

/**
 * SCL-6077: DYNAMIC-PROPERTY as a built-in function and as an assignment target, in the plain
 * assignment statement (also with the compound operators) and inside ASSIGN. Before, both
 * statement forms failed with "unexpected token: (".
 *
 * DYNAMICPROPERTY (1249, reserved keyword, no abbreviation) is a function node with its
 * arguments as funargs (LEFTPAREN expression (COMMA expression)* RIGHTPAREN). As an assignment
 * target it is a pseudfn like ENTRY or SUBSTRING. The Proparse ABL engine (SCL-6078) produces the
 * same tree.
 */
public class TestDynamicProperty extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL6077/";

	private ParseUnit parse(String fixture) throws RefactorException {
		ParseUnit pu = new ParseUnit(new File(FIXTURES + fixture), "ISO8859-1");
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse " + fixture, pu.getTopNode());
		return pu;
	}

	/** Asserts the types of the direct children of a node, in order. */
	private static void assertChildren(JPNode parent, int... types) {
		JPNode child = parent.firstChild();
		for (int i = 0; i < types.length; i++) {
			Assert.assertNotNull("Child " + i + " (" + NodeTypes.getTypeName(types[i]) + ") missing below "
					+ NodeTypes.getTypeName(parent.getType()), child);
			Assert.assertEquals("Child " + i + " below " + NodeTypes.getTypeName(parent.getType()),
					NodeTypes.getTypeName(types[i]), NodeTypes.getTypeName(child.getType()));
			child = child.nextSibling();
		}
		Assert.assertNull("Unexpected extra child " + (child == null ? "" : NodeTypes.getTypeName(child.getType()))
				+ " below " + NodeTypes.getTypeName(parent.getType()), child);
	}

	private static JPNode child(JPNode parent, int index) {
		JPNode child = parent.firstChild();
		for (int i = 0; i < index; i++)
			child = child.nextSibling();
		return child;
	}

	/** The statement heads below the program root after the three DEFINE VARIABLE statements. */
	private static ArrayList<JPNode> statements(ParseUnit pu) {
		ArrayList<JPNode> list = new ArrayList<JPNode>();
		for (JPNode n = pu.getTopNode().firstChild(); n != null; n = n.nextSibling())
			if (n.isStateHead() && n.getType() != NodeTypes.DEFINE)
				list.add(n);
		return list;
	}

	/** DYNAMIC-PROPERTY (oObj, cProp) */
	private static void assertDynamicProperty(JPNode node) {
		Assert.assertEquals(NodeTypes.DYNAMICPROPERTY, node.getType());
		Assert.assertEquals("dynamic-property", node.getText());
		assertChildren(node, NodeTypes.LEFTPAREN, NodeTypes.Field_ref, NodeTypes.COMMA, NodeTypes.Field_ref,
				NodeTypes.RIGHTPAREN);
		Assert.assertEquals("oObj", child(node, 1).firstChild().getText());
		Assert.assertEquals("cProp", child(node, 3).firstChild().getText());
	}

	/** An assignment statement: [ASSIGN] (operator (DYNAMICPROPERTY ...) value) [NO-ERROR] PERIOD */
	private static JPNode assertAssignment(JPNode statement, String keyword, int operator, boolean noError) {
		Assert.assertEquals(NodeTypes.ASSIGN, statement.getType());
		Assert.assertEquals(keyword, statement.getText());
		if (noError)
			assertChildren(statement, operator, NodeTypes.NOERROR_KW, NodeTypes.PERIOD);
		else
			assertChildren(statement, operator, NodeTypes.PERIOD);
		JPNode op = statement.firstChild();
		Assert.assertEquals(2, op.getDirectChildren().size());
		Assert.assertEquals(NodeTypes.DYNAMICPROPERTY, op.firstChild().getType());
		return op;
	}

	// ---------------------------------------------------------------- token table

	public void testTokenTable() {
		Assert.assertEquals(1249, NodeTypes.DYNAMICPROPERTY);
		Assert.assertEquals(1250, NodeTypes.Last_Token_Number);
		Assert.assertEquals("DYNAMICPROPERTY", NodeTypes.getTypeName(NodeTypes.DYNAMICPROPERTY));
		Assert.assertEquals(NodeTypes.DYNAMICPROPERTY, NodeTypes.getTypeNum("DYNAMICPROPERTY"));
		Assert.assertEquals("DYNAMIC-PROPERTY", NodeTypes.getFullText(NodeTypes.DYNAMICPROPERTY));
		Assert.assertTrue(NodeTypes.isKeywordType(NodeTypes.DYNAMICPROPERTY));
		Assert.assertTrue(org.prorefactor.core.TokenTypes.isNatural(NodeTypes.DYNAMICPROPERTY));
	}

	// ---------------------------------------------------------------- tree shapes

	public void testStatementsAndFunctions() throws RefactorException {
		ArrayList<JPNode> statements = statements(parse("dynamic-property.p"));
		Assert.assertEquals(14, statements.size());

		// dynamic-property (oObj, cProp) = "x":u .
		JPNode op = assertAssignment(statements.get(0), "", NodeTypes.EQUAL, false);
		assertDynamicProperty(op.firstChild());
		Assert.assertEquals(NodeTypes.QSTRING, child(op, 1).getType());

		// assign dynamic-property (oObj, cProp) = "y":u .
		op = assertAssignment(statements.get(1), "assign", NodeTypes.EQUAL, false);
		assertDynamicProperty(op.firstChild());

		// assign cProp = "Name":u dynamic-property (oObj, cProp) = "z":u .
		JPNode assign = statements.get(2);
		Assert.assertEquals("assign", assign.getText());
		assertChildren(assign, NodeTypes.EQUAL, NodeTypes.EQUAL, NodeTypes.PERIOD);
		assertChildren(child(assign, 0), NodeTypes.Field_ref, NodeTypes.QSTRING);
		assertChildren(child(assign, 1), NodeTypes.DYNAMICPROPERTY, NodeTypes.QSTRING);
		assertDynamicProperty(child(assign, 1).firstChild());

		// cValue = dynamic-property (oObj, cProp) .
		assign = statements.get(3);
		assertChildren(assign, NodeTypes.EQUAL, NodeTypes.PERIOD);
		assertChildren(child(assign, 0), NodeTypes.Field_ref, NodeTypes.DYNAMICPROPERTY);
		assertDynamicProperty(child(child(assign, 0), 1));

		// dynamic-property (oObj, cProp, 2) = "a":u .
		op = assertAssignment(statements.get(4), "", NodeTypes.EQUAL, false);
		assertChildren(op.firstChild(), NodeTypes.LEFTPAREN, NodeTypes.Field_ref, NodeTypes.COMMA, NodeTypes.Field_ref,
				NodeTypes.COMMA, NodeTypes.NUMBER, NodeTypes.RIGHTPAREN);

		// dynamic-property ("Consultingwerk.Util.LogManager":u, cProp) = "b":u .
		op = assertAssignment(statements.get(5), "", NodeTypes.EQUAL, false);
		assertChildren(op.firstChild(), NodeTypes.LEFTPAREN, NodeTypes.QSTRING, NodeTypes.COMMA, NodeTypes.Field_ref,
				NodeTypes.RIGHTPAREN);

		// dynamic-property (oObj, cProp) = dynamic-property (oObj, "Other":u) no-error .
		op = assertAssignment(statements.get(6), "", NodeTypes.EQUAL, true);
		assertDynamicProperty(op.firstChild());
		Assert.assertEquals(NodeTypes.DYNAMICPROPERTY, child(op, 1).getType());

		// assign dynamic-property (oObj, cProp) += "c":u .
		op = assertAssignment(statements.get(7), "assign", NodeTypes.PLUS_EQUAL, false);
		assertDynamicProperty(op.firstChild());

		// compound assignment statements
		op = assertAssignment(statements.get(8), "", NodeTypes.PLUS_EQUAL, false);
		assertDynamicProperty(op.firstChild());
		assertAssignment(statements.get(9), "", NodeTypes.MINUS_EQUAL, false);
		assertAssignment(statements.get(10), "", NodeTypes.MULTIPLY_EQUAL, true);
		assertAssignment(statements.get(11), "", NodeTypes.DIVIDE_EQUAL, false);

		// cValue = string (dynamic-property (oObj, cProp)) + "d":u .
		assign = statements.get(12);
		Assert.assertEquals(1, assign.query(NodeTypes.DYNAMICPROPERTY).size());
		JPNode dynprop = assign.query(NodeTypes.DYNAMICPROPERTY).get(0);
		Assert.assertEquals(NodeTypes.STRING, dynprop.parent().getType());
		assertDynamicProperty(dynprop);

		// dynamic-property (oObj, cProp) .
		JPNode expr = statements.get(13);
		Assert.assertEquals(NodeTypes.Expr_statement, expr.getType());
		assertChildren(expr, NodeTypes.DYNAMICPROPERTY, NodeTypes.PERIOD);
		assertDynamicProperty(expr.firstChild());
	}

	/** The arguments are expressions: the variables used in them resolve to their symbols. */
	public void testArgumentsResolveToVariables() throws RefactorException {
		ParseUnit pu = parse("dynamic-property.p");
		for (JPNode dynprop : pu.getTopNode().query(NodeTypes.DYNAMICPROPERTY))
			for (JPNode ref : dynprop.query(NodeTypes.Field_ref))
				Assert.assertNotNull("No symbol for " + ref.firstChild().getText() + " in line " + ref.getLine(),
						ref.getSymbol());
	}

}
