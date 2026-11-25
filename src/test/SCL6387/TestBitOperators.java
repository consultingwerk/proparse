package test.SCL6387;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.TokenTypes;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import test.ProparseTestCase;

/**
 * SCL-6387: the bitwise operators of the ABL Agent - BIT-AND, BIT-OR, BIT-XOR, BIT-NOT, BIT-NAND,
 * BIT-NOR, BIT-XNOR, BIT-SHL, BIT-SHR and BIT-LSHR.
 *
 * Node types BITAND (1252) ... BITXOR (1261) in alphabetical order, Last_Token_Number 1262 - agreed
 * with the Proparse ABL engine of the SmartComponent Library (SCL-6388). The keywords are
 * unreserved and have no abbreviation. A binary operator is an operator-rooted subtree like PLUS,
 * BIT-NOT has its operand as the only child like NOT.
 *
 * Precedence (highest first, measured on the ABL Agent): BIT-NOT, * / MODULO, + -,
 * BIT-SHL / BIT-SHR / BIT-LSHR, comparisons, BIT-AND / BIT-NAND, BIT-XOR / BIT-XNOR,
 * BIT-OR / BIT-NOR. Every binary level is left associative. In an expression BIT-NOT is always
 * the operator, the binary keywords are valid operands (variable names).
 *
 * Only the ABL Agent compiles the fixture, so it is a .txt file: the classic OpenEdge compiler
 * must never see it.
 */
public class TestBitOperators extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL6387/";

	private ParseUnit parse(String fixture) throws RefactorException {
		ParseUnit pu = new ParseUnit(new File(FIXTURES + fixture), "ISO8859-1");
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse " + fixture, pu.getTopNode());
		return pu;
	}

	/** The statement heads below the program root, without the DEFINE statements. */
	private static ArrayList<JPNode> statements(ParseUnit pu) {
		ArrayList<JPNode> list = new ArrayList<JPNode>();
		for (JPNode n = pu.getTopNode().firstChild(); n != null; n = n.nextSibling())
			if (n.isStateHead() && n.getType() != NodeTypes.DEFINE)
				list.add(n);
		return list;
	}

	/**
	 * Renders a subtree as TYPE(child,child,...). Leaves are rendered by their type name, NUMBER
	 * and ID leaves by their text.
	 */
	private static String tree(JPNode node) {
		JPNode child = node.firstChild();
		if (child == null) {
			if (node.getType() == NodeTypes.NUMBER || node.getType() == NodeTypes.ID)
				return node.getText();
			return NodeTypes.getTypeName(node.getType());
		}
		StringBuilder sb = new StringBuilder(NodeTypes.getTypeName(node.getType())).append('(');
		for (; child != null; child = child.nextSibling()) {
			sb.append(tree(child));
			if (child.nextSibling() != null)
				sb.append(',');
		}
		return sb.append(')').toString();
	}

	/** The expression of an assignment statement "x = expression ." */
	private static JPNode assignedExpression(JPNode statement) {
		Assert.assertEquals(NodeTypes.ASSIGN, statement.getType());
		JPNode equal = statement.firstChild();
		Assert.assertEquals(NodeTypes.EQUAL, equal.getType());
		Assert.assertEquals(NodeTypes.PERIOD, equal.nextSibling().getType());
		return equal.firstChild().nextSibling();
	}

	// ---------------------------------------------------------------- token table

	public void testTokenTable() {
		int[] types = { NodeTypes.BITAND, NodeTypes.BITLSHR, NodeTypes.BITNAND, NodeTypes.BITNOR, NodeTypes.BITNOT,
				NodeTypes.BITOR, NodeTypes.BITSHL, NodeTypes.BITSHR, NodeTypes.BITXNOR, NodeTypes.BITXOR };
		String[] names = { "BITAND", "BITLSHR", "BITNAND", "BITNOR", "BITNOT", "BITOR", "BITSHL", "BITSHR", "BITXNOR",
				"BITXOR" };
		String[] keywords = { "BIT-AND", "BIT-LSHR", "BIT-NAND", "BIT-NOR", "BIT-NOT", "BIT-OR", "BIT-SHL", "BIT-SHR",
				"BIT-XNOR", "BIT-XOR" };
		for (int i = 0; i < types.length; i++) {
			Assert.assertEquals(names[i], 1252 + i, types[i]);
			Assert.assertEquals(names[i], NodeTypes.getTypeName(types[i]));
			Assert.assertEquals(types[i], NodeTypes.getTypeNum(names[i]));
			Assert.assertEquals(keywords[i], NodeTypes.getFullText(types[i]));
			Assert.assertTrue(names[i], NodeTypes.isKeywordType(types[i]));
			Assert.assertTrue(names[i], TokenTypes.isNatural(types[i]));
			// no abbreviation (unreserved: see testKeywordVariablesResolve)
			String keyword = keywords[i].toLowerCase();
			Assert.assertEquals(keywords[i], NodeTypes.getFullText(keyword));
			Assert.assertEquals(keyword + " has no abbreviation", "",
					NodeTypes.getFullText(keyword.substring(0, keyword.length() - 1)));
		}
		Assert.assertEquals(1262, NodeTypes.Last_Token_Number);
	}

	// ---------------------------------------------------------------- tree shapes

	public void testTrees() throws RefactorException {
		ArrayList<JPNode> statements = statements(parse("bit-operators.txt"));
		String[] expected = {
				// every operator
				"BITAND(Field_ref(iA),Field_ref(iB))",
				"BITOR(Field_ref(iA),Field_ref(iB))",
				"BITXOR(Field_ref(iA),Field_ref(iB))",
				"BITNAND(Field_ref(iA),Field_ref(iB))",
				"BITNOR(Field_ref(iA),Field_ref(iB))",
				"BITXNOR(Field_ref(iA),Field_ref(iB))",
				"BITSHL(Field_ref(iA),Field_ref(iB))",
				"BITSHR(Field_ref(iA),Field_ref(iB))",
				"BITLSHR(Field_ref(iA),Field_ref(iB))",
				"BITNOT(Field_ref(iA))",
				// BIT-NOT with parentheses, nested, below an additive operator
				"BITNOT(LEFTPAREN(Field_ref(iA),RIGHTPAREN))",
				"BITNOT(BITNOT(Field_ref(iA)))",
				"PLUS(BITNOT(Field_ref(iA)),1)",
				// precedence levels
				"BITOR(MULTIPLY(2,Field_ref(iA)),1)",
				"BITAND(Field_ref(iA),PLUS(Field_ref(iB),1))",
				"BITSHL(1,PLUS(Field_ref(iB),1))",
				"EQ(BITSHL(1,2),4)",
				"BITXOR(4,BITAND(4,0))",
				"BITOR(4,BITXOR(4,4))",
				"EQ(LEFTPAREN(BITOR(Field_ref(iA),Field_ref(iB)),RIGHTPAREN),7)",
				"EQ(BITNOT(1),-2)",
				"BITAND(UNARY_MINUS(Field_ref(iA)),3)",
				"BITOR(BITXOR(BITAND(Field_ref(iA),Field_ref(iB)),Field_ref(iA)),Field_ref(iB))",
				// left associativity
				"BITSHL(BITSHR(9,1),1)",
				"BITAND(BITNAND(4,4),0)",
				"BITNOR(BITOR(4,0),4)",
				// unreserved keywords used as variable names
				"BITOR(Field_ref(bit-or),Field_ref(bit-or))" };
		Assert.assertEquals(expected.length + 2, statements.size());
		for (int i = 0; i < expected.length; i++)
			Assert.assertEquals("Statement " + i, expected[i], tree(assignedExpression(statements.get(i))));

		// bit-not = 5 . - the keyword as the target of an assignment
		JPNode statement = statements.get(expected.length);
		Assert.assertEquals("ASSIGN(EQUAL(Field_ref(bit-not),5),PERIOD)", tree(statement));

		// message iR lR .
		Assert.assertEquals(NodeTypes.MESSAGE, statements.get(expected.length + 1).getType());
	}

	public void testKeywordVariablesResolve() throws RefactorException {
		ArrayList<JPNode> statements = statements(parse("bit-operators.txt"));

		// iR = bit-or bit-or bit-or . - both operands are the variable bit-or
		JPNode op = assignedExpression(statements.get(26));
		Assert.assertEquals(NodeTypes.BITOR, op.getType());
		Assert.assertEquals("bit-or", op.getText());
		for (JPNode ref = op.firstChild(); ref != null; ref = ref.nextSibling()) {
			Assert.assertEquals(NodeTypes.Field_ref, ref.getType());
			Assert.assertEquals(NodeTypes.ID, ref.firstChild().getType());
			Assert.assertNotNull(ref.getSymbol());
			Assert.assertEquals("bit-or", ref.getSymbol().getName());
		}

		// bit-not = 5 .
		JPNode ref = statements.get(27).firstChild().firstChild();
		Assert.assertEquals(NodeTypes.Field_ref, ref.getType());
		Assert.assertNotNull(ref.getSymbol());
		Assert.assertEquals("bit-not", ref.getSymbol().getName());
	}

}
