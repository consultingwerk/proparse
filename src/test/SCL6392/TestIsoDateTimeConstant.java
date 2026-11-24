package test.SCL6392;

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
 * SCL-6392: an ISO 8601 DATETIME / DATETIME-TZ constant
 * (yyyy-mm-dd{T|t}hh:mm[:ss[.f...]][{+|-}hh[[:]mm]], e.g. 2026-11-24T10:30:00.123+05:00) is one
 * LEXDATE token with the original text, like the mm/dd/yyyy date constant - as INITIAL value and
 * in expressions. Before, the lexer stopped at the first colon (ID '2026-11-24T10') and the parse
 * failed with "unexpected token".
 *
 * Forms the ABL compiler rejects (no time, an hour without minutes, the designator Z, a decimal
 * comma) are no LEXDATE - the decimal comma ends the constant like the comma of an extent
 * initial list.
 */
public class TestIsoDateTimeConstant extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL6392/";

	// ---------------------------------------------------------------- helpers

	private ParseUnit parse(String fixture) throws RefactorException {
		ParseUnit pu = new ParseUnit(new File(FIXTURES + fixture), "ISO8859-1");
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse " + fixture, pu.getTopNode());
		return pu;
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

	private static ArrayList<String> list(String... values) {
		ArrayList<String> result = new ArrayList<String>();
		for (String value : values)
			result.add(value);
		return result;
	}

	/** The statement heads below the program root. */
	private static ArrayList<JPNode> statements(ParseUnit pu) {
		ArrayList<JPNode> result = new ArrayList<JPNode>();
		for (JPNode n = pu.getTopNode().firstChild(); n != null; n = n.nextSibling())
			if (n.isStateHead())
				result.add(n);
		return result;
	}

	/** The child of a node with the given type, or null. */
	private static JPNode child(JPNode node, int type) {
		for (JPNode child = node.firstChild(); child != null; child = child.nextSibling())
			if (child.getType() == type)
				return child;
		return null;
	}

	/** The single child of the INITIAL node of a DEFINE VARIABLE statement or a FIELD node. */
	private static JPNode initialValue(JPNode node) {
		JPNode initial = child(node, NodeTypes.INITIAL);
		Assert.assertNotNull("INITIAL in line " + node.getLine(), initial);
		Assert.assertNotNull("INITIAL value in line " + node.getLine(), initial.firstChild());
		Assert.assertNull("INITIAL has one child in line " + node.getLine(), initial.firstChild().nextSibling());
		return initial.firstChild();
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

	private static void assertNoLexdate(String source) throws Exception {
		for (String token : lex(source))
			Assert.assertFalse("LEXDATE in " + source + ": " + token, token.endsWith(" LEXDATE"));
	}

	// ---------------------------------------------------------------- lexer

	public void testIsoDateTimeIsLexdate() throws Exception {
		String[] constants = { "2026-11-24T10:30:00.123", "2026-11-24T10:30:00", "2026-11-24T10:30",
				"2026-11-24t10:30:00", "2026-11-24T10:30:00.123456", "2026-11-24T10:30:00.123+05:00",
				"2026-11-24T10:30:00.123-05:00", "2026-11-24T10:30:00+0500", "2026-11-24T10:30:00+05",
				"2026-11-24T10:30+05:00", "2026-11-24T10:30:00.123+14:00" };
		for (String constant : constants)
			assertToken(constant, "LEXDATE");
	}

	/** The period after the constant ends the statement. */
	public void testStatementPeriod() throws Exception {
		Assert.assertEquals(list("2026-11-24T10:30:00 LEXDATE", ". PERIOD"), lex("2026-11-24T10:30:00."));
		Assert.assertEquals(list("2026-11-24T10:30:00.123 LEXDATE", ". PERIOD"), lex("2026-11-24T10:30:00.123."));
		Assert.assertEquals(list("2026-11-24T10:30:00.123+05:00 LEXDATE", ". PERIOD"),
				lex("2026-11-24T10:30:00.123+05:00."));
	}

	/** Forms the ABL compiler rejects are no LEXDATE. */
	public void testRejectedFormsAreNoLexdate() throws Exception {
		String[] sources = { "2026-11-24", "2026-11-24T10", "2026-11-24T10:30:00Z",
				"2026-1-24T10:30:00", "2026-11-24T1:30:00", "26-11-24T10:30:00", "2026-11-24T10:30:00+5" };
		for (String source : sources)
			assertNoLexdate(source);
	}

	/** Tokens that start with digits and are no ISO constant keep their type. */
	public void testOtherTokensUnchanged() throws Exception {
		assertToken("11/24/2026", "LEXDATE");
		assertToken("12", "NUMBER");
		assertToken("2026-11-24", "NUMBER");
		assertToken("12abc", "ID");
		assertToken("2026-11-24T10", "ID");
		// a colon after a number still ends the token
		Assert.assertEquals("12 NUMBER", lex("12:30").get(0));
		Assert.assertEquals("2026 NUMBER", lex("2026:30").get(0));
	}

	// ---------------------------------------------------------------- tree

	public void testTree() throws RefactorException {
		ArrayList<JPNode> statements = statements(parse("iso-datetime.p"));
		Assert.assertEquals(7, statements.size());

		// define variable ... initial <constant>.
		JPNode value = initialValue(statements.get(0));
		Assert.assertEquals(NodeTypes.LEXDATE, value.getType());
		Assert.assertEquals("2026-11-24T10:30:00.123", value.getText());

		value = initialValue(statements.get(1));
		Assert.assertEquals(NodeTypes.LEXDATE, value.getType());
		Assert.assertEquals("2026-11-24T10:30:00.123+05:00", value.getText());

		// extent initial list
		Assert.assertEquals(list("2026-11-24T10:30:00", "2026-11-25T11:00"),
				texts(statements.get(2), NodeTypes.LEXDATE));

		// temp-table fields
		ArrayList<JPNode> fields = new ArrayList<JPNode>();
		collect(statements.get(3), NodeTypes.FIELD, fields);
		Assert.assertEquals(3, fields.size());
		value = initialValue(fields.get(0));
		Assert.assertEquals(NodeTypes.LEXDATE, value.getType());
		Assert.assertEquals("2026-11-24T10:30:00.123", value.getText());
		value = initialValue(fields.get(1));
		Assert.assertEquals(NodeTypes.LEXDATE, value.getType());
		Assert.assertEquals("2026-11-24t10:30:00-05:00", value.getText());
		value = initialValue(fields.get(2));
		Assert.assertEquals(NodeTypes.LEXDATE, value.getType());
		Assert.assertEquals("11/24/2026", value.getText());

		// dt = 2026-11-24T10:30:00.  ->  ASSIGN > EQUAL (Field_ref, LEXDATE)
		JPNode assign = statements.get(4);
		Assert.assertEquals(NodeTypes.ASSIGN, assign.getType());
		JPNode equal = assign.firstChild();
		Assert.assertEquals(NodeTypes.EQUAL, equal.getType());
		Assert.assertEquals(NodeTypes.Field_ref, equal.firstChild().getType());
		Assert.assertEquals(NodeTypes.LEXDATE, equal.firstChild().nextSibling().getType());
		Assert.assertEquals("2026-11-24T10:30:00", equal.firstChild().nextSibling().getText());

		// dtz = <constant> + 1000.  ->  PLUS (LEXDATE, NUMBER)
		equal = statements.get(5).firstChild();
		JPNode plus = equal.firstChild().nextSibling();
		Assert.assertEquals(NodeTypes.PLUS, plus.getType());
		Assert.assertEquals(NodeTypes.LEXDATE, plus.firstChild().getType());
		Assert.assertEquals("2026-11-24T10:30:00.123+0500", plus.firstChild().getText());
		Assert.assertEquals("1000", plus.firstChild().nextSibling().getText());

		// if dt = <constant> then message <constant>.
		JPNode ifNode = statements.get(6);
		Assert.assertEquals(NodeTypes.IF, ifNode.getType());
		Assert.assertEquals(list("2026-11-24T10:30", "2026-11-24T10:30:00.123456"),
				texts(ifNode, NodeTypes.LEXDATE));
		Assert.assertTrue("No ID below IF", texts(ifNode, NodeTypes.ID).size() == 1);
	}
}
