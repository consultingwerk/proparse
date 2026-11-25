package test.SCL6310;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import test.ProparseTestCase;

/**
 * SCL-6310: the type parameters of a generic class or interface definition
 * (CLASS Foo&lt;T AS Progress.Lang.Object&gt;:) are a structured subtree below the statement head:
 * Type_parameter_list &gt; LEFTANGLE, Type_parameter (&gt; ID, AS, TYPE_NAME), COMMA, ...,
 * RIGHTANGLE. The TYPE_NAME of the class keeps the plain class name. Before, the whole header was
 * folded into one TYPE_NAME node ("GenericProbe&lt;TasProgress.Lang.Object&gt;"). The Proparse ABL
 * engine of the SmartComponent Library produces the same tree (node type numbers 1250 / 1251).
 */
public class TestGenericTypeDefinitions extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL6310/";

	@Override
	protected String getProversion() {
		return "13.0";
	}

	/** The fixtures reference each other (INHERITS), so src/test is the PROPATH. */
	@Override
	protected String getPropath() {
		return new File("src/test").getAbsolutePath();
	}

	private ParseUnit parse(String fixture) throws RefactorException {
		ParseUnit pu = new ParseUnit(new File(FIXTURES + fixture), "ISO8859-1");
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse " + fixture, pu.getTopNode());
		return pu;
	}

	/** The first statement head of the given type below the program root. */
	private static JPNode statement(ParseUnit pu, int type) {
		for (JPNode node : pu.getTopNode().query(type))
			if (node.isStateHead())
				return node;
		Assert.fail("No " + NodeTypes.getTypeName(type) + " statement");
		return null;
	}

	/** TYPE 'text' (child, child, ...) */
	private static String dump(JPNode node) {
		StringBuilder sb = new StringBuilder();
		sb.append(NodeTypes.getTypeName(node.getType())).append(" '").append(node.getText()).append("'");
		if (node.firstChild() != null) {
			sb.append(" (");
			for (JPNode child = node.firstChild(); child != null; child = child.nextSibling()) {
				sb.append(dump(child));
				if (child.nextSibling() != null)
					sb.append(", ");
			}
			sb.append(")");
		}
		return sb.toString();
	}

	/** The children of the statement head up to and including the block colon. */
	private static String header(JPNode stateHead) {
		StringBuilder sb = new StringBuilder();
		for (JPNode child = stateHead.firstChild(); child != null; child = child.nextSibling()) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(dump(child));
			if (child.getType() == NodeTypes.LEXCOLON)
				break;
		}
		return sb.toString();
	}

	private static ArrayList<String> typeNames(JPNode root) {
		ArrayList<String> names = new ArrayList<String>();
		for (JPNode node : root.query(NodeTypes.TYPE_NAME))
			names.add(node.getText());
		return names;
	}

	// ------------------------------------------------------------ node types

	public void testNodeTypeNumbers() {
		Assert.assertEquals(1250, NodeTypes.Type_parameter_list);
		Assert.assertEquals(1251, NodeTypes.Type_parameter);
		// Last_Token_Number moves on with every new node type (SCL-6387: BITAND ... BITXOR 1252 - 1261)
		Assert.assertTrue(NodeTypes.Type_parameter < NodeTypes.Last_Token_Number);
		Assert.assertEquals("Type_parameter_list", NodeTypes.getTypeName(NodeTypes.Type_parameter_list));
		Assert.assertEquals("Type_parameter", NodeTypes.getTypeName(NodeTypes.Type_parameter));
		Assert.assertFalse(NodeTypes.isKeywordType(NodeTypes.Type_parameter_list));
		Assert.assertFalse(NodeTypes.isKeywordType(NodeTypes.Type_parameter));
	}

	// ------------------------------------------------------------ class definitions

	/** The probe of the ticket: one type parameter, the class name stays plain. */
	public void testSingleTypeParameter() throws RefactorException {
		ParseUnit pu = parse("GenericProbe.cls");
		JPNode classNode = statement(pu, NodeTypes.CLASS);
		Assert.assertEquals(
				"TYPE_NAME 'SCL6310.GenericProbe', "
				+ "Type_parameter_list '' (LEFTANGLE '<', "
				+ "Type_parameter '' (ID 'T', AS 'as', TYPE_NAME 'Progress.Lang.Object'), "
				+ "RIGHTANGLE '>'), "
				+ "LEXCOLON ':'",
				header(classNode));
		Assert.assertEquals("SCL6310.GenericProbe", pu.getRootScope().getClassName());

		// The type parameter inside the body stays a plain TYPE_NAME (property and return type).
		Assert.assertEquals("[SCL6310.GenericProbe, Progress.Lang.Object, T, T]",
				typeNames(pu.getTopNode()).toString());
	}

	/** Several type parameters, blanks and line breaks inside the brackets, modifiers after them. */
	public void testMultipleTypeParameters() throws RefactorException {
		ParseUnit pu = parse("GenericMulti.cls");
		JPNode classNode = statement(pu, NodeTypes.CLASS);
		Assert.assertEquals(
				"TYPE_NAME 'SCL6310.GenericMulti', "
				+ "Type_parameter_list '' (LEFTANGLE '<', "
				+ "Type_parameter '' (ID 'K', AS 'AS', TYPE_NAME 'Progress.Lang.Object'), "
				+ "COMMA ',', "
				+ "Type_parameter '' (ID 'V', AS 'AS', TYPE_NAME 'Progress.Lang.Object'), "
				+ "COMMA ',', "
				+ "Type_parameter '' (ID 'W', AS 'AS', TYPE_NAME 'Progress.Lang.Object'), "
				+ "RIGHTANGLE '>'), "
				+ "FINAL 'FINAL', SERIALIZABLE 'SERIALIZABLE', LEXCOLON ':'",
				header(classNode));
		Assert.assertEquals(1, pu.getTopNode().query(NodeTypes.Type_parameter_list).size());
		Assert.assertEquals(3, pu.getTopNode().query(NodeTypes.Type_parameter).size());
	}

	/** A constraint that is a bound generic type is one TYPE_NAME with the SCL-6029 text rule. */
	public void testGenericConstraint() throws RefactorException {
		ParseUnit pu = parse("GenericConstraint.cls");
		JPNode classNode = statement(pu, NodeTypes.CLASS);
		Assert.assertEquals(
				"TYPE_NAME 'SCL6310.GenericConstraint', "
				+ "Type_parameter_list '' (LEFTANGLE '<', "
				+ "Type_parameter '' (ID 'L', AS 'as', TYPE_NAME 'Progress.Collections.IList<Progress.Lang.Object>'), "
				+ "COMMA ',', "
				+ "Type_parameter '' (ID 'M', AS 'as', TYPE_NAME 'IMap<Progress.Lang.Object,Outer<Progress.Lang.Object,Inner>>'), "
				+ "RIGHTANGLE '>'), "
				+ "IMPLEMENTS 'implements' (TYPE_NAME 'IComparable<Progress.Lang.Object>'), "
				+ "USEWIDGETPOOL 'use-widget-pool', LEXCOLON ':'",
				header(classNode));

		// Bound generic type references in the body keep the SCL-6029 shape.
		Assert.assertEquals(
				"[Progress.Collections.*, SCL6310.GenericConstraint, "
				+ "Progress.Collections.IList<Progress.Lang.Object>, "
				+ "IMap<Progress.Lang.Object,Outer<Progress.Lang.Object,Inner>>, "
				+ "IComparable<Progress.Lang.Object>, Pair<Alpha,Beta>, "
				+ "Progress.Lang.Object, Pair<Alpha,Beta>]",
				typeNames(pu.getTopNode()).toString());
	}

	/** INHERITS behind the type parameter list: the super class is found and its members inherited. */
	public void testInheritsBehindTypeParameters() throws RefactorException {
		ParseUnit pu = parse("GenericChild.cls");
		JPNode classNode = statement(pu, NodeTypes.CLASS);
		Assert.assertEquals(
				"TYPE_NAME 'SCL6310.GenericChild', "
				+ "Type_parameter_list '' (LEFTANGLE '<', "
				+ "Type_parameter '' (ID 'T', AS 'as', TYPE_NAME 'Progress.Lang.Object'), "
				+ "RIGHTANGLE '>'), "
				+ "INHERITS 'inherits' (TYPE_NAME 'SCL6310.GenericBase'), LEXCOLON ':'",
				header(classNode));

		// cBase is defined in the super class only.
		JPNode fieldRef = pu.getTopNode().query(NodeTypes.Field_ref).get(0);
		Assert.assertEquals("cBase", fieldRef.firstChild().getText());
		Assert.assertNotNull("cBase not resolved from the super class", fieldRef.getSymbol());
		Assert.assertNotNull("No super scope", pu.getRootScope().getParentScope());
	}

	/** A class without type parameters has no Type_parameter_list. */
	public void testPlainClassUnchanged() throws RefactorException {
		ParseUnit pu = parse("Plain.cls");
		JPNode classNode = statement(pu, NodeTypes.CLASS);
		Assert.assertEquals(
				"TYPE_NAME 'SCL6310.Plain', "
				+ "INHERITS 'inherits' (TYPE_NAME 'SCL6310.GenericBase'), LEXCOLON ':'",
				header(classNode));
		Assert.assertEquals(0, pu.getTopNode().query(NodeTypes.Type_parameter_list).size());
		Assert.assertEquals("[SCL6310.Plain, SCL6310.GenericBase, SCL6310.GenericProbe<Progress.Lang.Object>]",
				typeNames(pu.getTopNode()).toString());
	}

	// ------------------------------------------------------------ interface definitions

	public void testInterface() throws RefactorException {
		ParseUnit pu = parse("IGenericProbe.cls");
		JPNode interfaceNode = statement(pu, NodeTypes.INTERFACE);
		Assert.assertEquals(
				"TYPE_NAME 'SCL6310.IGenericProbe', "
				+ "Type_parameter_list '' (LEFTANGLE '<', "
				+ "Type_parameter '' (ID 'T', AS 'as', TYPE_NAME 'Progress.Lang.Object'), "
				+ "COMMA ',', "
				+ "Type_parameter '' (ID 'U', AS 'as', TYPE_NAME 'SCL6310.GenericProbe<Progress.Lang.Object>'), "
				+ "RIGHTANGLE '>'), "
				+ "INHERITS 'inherits' (TYPE_NAME 'SCL6310.IPlain'), LEXCOLON ':'",
				header(interfaceNode));
	}

}
