package test.SCL6305;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.DoParse;
import com.joanju.proparse.NodeTypes;

import test.ProparseTestCase;

/**
 * SCL-6305: OpenEdge 13.0 numeric literals in scientific notation (m {E|e} [+|-] n, exponent of
 * one or two digits) are one NUMBER token with the original text. Before, 1.5e3 was an ID (tree
 * Field_ref > ID), and .5e2 / -2e3 were FILENAME tokens that failed to parse.
 *
 * Tokens that merely start like a number (1e3abc, 12abc, a three digit exponent, file names)
 * keep the token type they had before.
 */
public class TestScientificNotation extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL6305/";

	// ---------------------------------------------------------------- helpers

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

	/** All nodes of a type below a node, in tree order. */
	private static void collect(JPNode node, int type, ArrayList<JPNode> result) {
		for (JPNode child = node.firstChild(); child != null; child = child.nextSibling()) {
			if (child.getType() == type)
				result.add(child);
			collect(child, type, result);
		}
	}

	private static ArrayList<String> texts(JPNode node, int type) {
		ArrayList<JPNode> nodes = new ArrayList<JPNode>();
		collect(node, type, nodes);
		ArrayList<String> result = new ArrayList<String>();
		for (JPNode n : nodes)
			result.add(n.getText());
		return result;
	}

	/** The NUMBER texts of a statement, and that its only field reference is d. */
	private static void assertNumbers(JPNode statement, String... numbers) {
		ArrayList<String> expected = new ArrayList<String>();
		for (String n : numbers)
			expected.add(n);
		Assert.assertEquals("NUMBER nodes in line " + statement.getLine(), expected,
				texts(statement, NodeTypes.NUMBER));
		for (String id : texts(statement, NodeTypes.ID))
			Assert.assertEquals("Unexpected ID in line " + statement.getLine(), "d", id);
	}

	/** Assignment d = expression: ASSIGN > EQUAL (Field_ref, expression), PERIOD */
	private static JPNode assertAssignment(JPNode statement) {
		Assert.assertEquals(NodeTypes.ASSIGN, statement.getType());
		JPNode equal = statement.firstChild();
		Assert.assertEquals(NodeTypes.EQUAL, equal.getType());
		Assert.assertEquals(NodeTypes.Field_ref, equal.firstChild().getType());
		Assert.assertEquals("d", equal.firstChild().firstChild().getText());
		Assert.assertEquals(NodeTypes.PERIOD, equal.nextSibling().getType());
		return equal.firstChild().nextSibling();
	}

	/** Lexes a source with DoParse.setJustLex and returns "text TYPE" per token, without WS. */
	private static ArrayList<String> lex(String source) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream saved = System.out;
		System.setOut(new PrintStream(buffer, true, "ISO-8859-1"));
		try {
			DoParse doParse = new DoParse(null, source);
			doParse.setJustLex(true);
			doParse.doParse();
		} finally {
			System.setOut(saved);
		}
		ArrayList<String> tokens = new ArrayList<String>();
		for (String line : buffer.toString("ISO-8859-1").split("\r?\n")) {
			// "<line> <text> <type>"
			int first = line.indexOf(' ');
			int last = line.lastIndexOf(' ');
			if (first < 0 || last <= first)
				continue;
			String type = line.substring(last + 1);
			if (type.equals("WS") || type.equals("EOF"))
				continue;
			tokens.add(line.substring(first + 1, last) + " " + type);
		}
		return tokens;
	}

	private static void assertToken(String source, String type) throws Exception {
		ArrayList<String> tokens = lex(source);
		Assert.assertEquals("Tokens of " + source + ": " + tokens, 1, tokens.size());
		Assert.assertEquals(source + " " + type, tokens.get(0));
	}

	// ---------------------------------------------------------------- lexer

	public void testScientificNotationIsNumber() throws Exception {
		String[] literals = { "1.5e3", "1E6", "1e+6", "1.0E6", "1.23e-4", "143e9", "6.02214076e+23", "23766e-1",
				"1e+3", "1.5E-3", ".5e2", ".5E+12", "-2e3", "-2E-3", "+1.5e3", "-.5e2", "1e99" };
		for (String literal : literals)
			assertToken(literal, "NUMBER");
	}

	public void testPlainNumbersUnchanged() throws Exception {
		assertToken("12", "NUMBER");
		assertToken("1.5", "NUMBER");
		assertToken(".5", "NUMBER");
		assertToken("-2", "NUMBER");
		assertToken("-2.5", "NUMBER");
		assertToken("1/2/2026", "LEXDATE");
	}

	/** Tokens that start like a number but are no scientific notation literal keep their type. */
	public void testIdentifiersAndFilenamesUnchanged() throws Exception {
		// digitStart(): ID
		assertToken("12abc", "ID");
		assertToken("1e3abc", "ID");
		assertToken("1e3_", "ID");
		assertToken("1e123", "ID");
		assertToken("1.5e", "ID");
		assertToken("1e+", "ID");
		assertToken("1ex", "ID");
		assertToken("1e3e3", "ID");
		assertToken("1e3.p", "ID");
		assertToken("2abc-def", "ID");
		// periodStart(): FILENAME
		assertToken(".5abc", "FILENAME");
		assertToken(".5e123", "FILENAME");
		assertToken(".5e3x", "FILENAME");
		// plusMinusStart(): FILENAME
		assertToken("-2abc", "FILENAME");
		assertToken("-2e3x", "FILENAME");
		assertToken("-2e123", "FILENAME");
		// backslash: FILENAME
		assertToken("1e3\\x", "FILENAME");
	}

	// ---------------------------------------------------------------- tree

	public void testTree() throws RefactorException {
		ArrayList<JPNode> statements = statements(parse("scientific-notation.p"));
		Assert.assertEquals(11, statements.size());

		String[] simple = { "1.5e3", "1E6", "1.23e-4", "6.02214076e+23", "23766e-1", "1e+3", ".5e2", "-2e3" };
		for (int i = 0; i < simple.length; i++) {
			JPNode value = assertAssignment(statements.get(i));
			Assert.assertEquals(NodeTypes.NUMBER, value.getType());
			Assert.assertEquals(simple[i], value.getText());
			Assert.assertNull(value.firstChild());
		}

		// d = 2 + 3e2 - 1.  ->  MINUS (PLUS (2, 3e2), 1)
		JPNode value = assertAssignment(statements.get(8));
		Assert.assertEquals(NodeTypes.MINUS, value.getType());
		JPNode plus = value.firstChild();
		Assert.assertEquals(NodeTypes.PLUS, plus.getType());
		Assert.assertEquals(NodeTypes.NUMBER, plus.firstChild().getType());
		Assert.assertEquals("2", plus.firstChild().getText());
		Assert.assertEquals(NodeTypes.NUMBER, plus.firstChild().nextSibling().getType());
		Assert.assertEquals("3e2", plus.firstChild().nextSibling().getText());
		Assert.assertEquals(NodeTypes.NUMBER, plus.nextSibling().getType());
		Assert.assertEquals("1", plus.nextSibling().getText());
		assertNumbers(statements.get(8), "2", "3e2", "1");

		// message 1e3 (d * 1e+3).
		Assert.assertEquals(NodeTypes.MESSAGE, statements.get(9).getType());
		assertNumbers(statements.get(9), "1e3", "1e+3");
		ArrayList<JPNode> multiply = new ArrayList<JPNode>();
		collect(statements.get(9), NodeTypes.MULTIPLY, multiply);
		Assert.assertEquals(1, multiply.size());
		Assert.assertEquals(NodeTypes.Field_ref, multiply.get(0).firstChild().getType());
		Assert.assertEquals("1e+3", multiply.get(0).firstChild().nextSibling().getText());

		// d = 1.5E-3 * -2E+2.
		value = assertAssignment(statements.get(10));
		Assert.assertEquals(NodeTypes.MULTIPLY, value.getType());
		assertNumbers(statements.get(10), "1.5E-3", "-2E+2");
	}
}
