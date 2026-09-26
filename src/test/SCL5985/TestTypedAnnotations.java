package test.SCL5985;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.prorefactor.core.IConstants;
import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import test.ProparseTestCase;

/**
 * SCL-5985: OpenEdge 13.1 strongly typed annotations (epic SCL-5984).
 *
 * The fixtures are the ones of the Proparse ABL implementation (SCL-5986,
 * UnitTests/Consultingwerk/ProparseAblTests/SCL5986/Fixtures); the node types and tree shapes
 * asserted here are the contract both engines share (R3 / R5 of the epic).
 */
public class TestTypedAnnotations extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL5985/";

	@Override
	protected String getProversion() {
		return "13.1";
	}

	/** The PROPATH holds stubs of the types the resolution fixture refers to. */
	@Override
	protected String getPropath() {
		return new File(FIXTURES + "propath").getAbsolutePath();
	}

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

	/** The statement heads of a Code_block / Program_root, in order. */
	private static ArrayList<JPNode> statements(JPNode block) {
		ArrayList<JPNode> list = new ArrayList<JPNode>();
		for (JPNode n = block.firstChild(); n != null; n = n.nextSibling())
			if (n.isStateHead())
				list.add(n);
		return list;
	}

	// ---------------------------------------------------------------- token table

	public void testTokenTable() {
		Assert.assertEquals(1247, NodeTypes.ANNOTATION_TYPE);
		Assert.assertEquals(1248, NodeTypes.Typed_annotation);
		Assert.assertEquals(1249, NodeTypes.Last_Token_Number);
		Assert.assertEquals("ANNOTATION_TYPE", NodeTypes.getTypeName(NodeTypes.ANNOTATION_TYPE));
		Assert.assertEquals("Typed_annotation", NodeTypes.getTypeName(NodeTypes.Typed_annotation));
		Assert.assertEquals(NodeTypes.ANNOTATION_TYPE, NodeTypes.getTypeNum("ANNOTATION_TYPE"));
		Assert.assertEquals(NodeTypes.Typed_annotation, NodeTypes.getTypeNum("Typed_annotation"));
		// ANNOTATION keyword: unreserved, no abbreviation; the @ annotation type is untouched
		Assert.assertEquals("ANNOTATION", NodeTypes.getFullText(NodeTypes.ANNOTATION_TYPE));
		Assert.assertTrue(NodeTypes.isKeywordType(NodeTypes.ANNOTATION_TYPE));
		Assert.assertFalse(NodeTypes.isKeywordType(NodeTypes.ANNOTATION));
		Assert.assertTrue(org.prorefactor.core.TokenTypes.isNatural(NodeTypes.ANNOTATION_TYPE));
		Assert.assertFalse(org.prorefactor.core.TokenTypes.isNatural(NodeTypes.Typed_annotation));
	}

	// ---------------------------------------------------------------- R1: ANNOTATION statement

	public void testAnnotationTypeMarker() throws RefactorException {
		ParseUnit pu = parse("annotation-type-marker.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> statements = statements(top);
		// @lowercase. / block-level ... / annotation ... end.
		JPNode anno = top.findDirectChild(NodeTypes.ANNOTATION_TYPE);
		Assert.assertNotNull("ANNOTATION_TYPE statement missing", anno);
		Assert.assertSame(statements.get(statements.size() - 1), anno);
		Assert.assertTrue(anno.isStateHead());
		Assert.assertEquals("annotation", anno.getText());
		Assert.assertEquals(7, anno.getLine());
		assertChildren(anno, NodeTypes.TYPE_NAME, NodeTypes.LEXCOLON, NodeTypes.Code_block, NodeTypes.END, NodeTypes.PERIOD);
		Assert.assertEquals("Consultingwerk.ProparseAblTests.SCL5986.Fixtures.Exportable", child(anno, 0).getText());
		// a marker type has no properties, a bare END closes it
		Assert.assertNull(child(anno, 2).firstChild());
		Assert.assertNull(child(anno, 3).firstChild());
		// the unit is class-like: the root scope carries the type name
		Assert.assertEquals("Consultingwerk.ProparseAblTests.SCL5986.Fixtures.Exportable", pu.getRootScope().getClassName());
	}

	public void testAnnotationTypeProperties() throws RefactorException {
		ParseUnit pu = parse("annotation-type-properties.txt");
		JPNode anno = pu.getTopNode().findDirectChild(NodeTypes.ANNOTATION_TYPE);
		Assert.assertNotNull(anno);
		assertChildren(anno, NodeTypes.TYPE_NAME, NodeTypes.LEXCOLON, NodeTypes.Code_block, NodeTypes.END, NodeTypes.PERIOD);
		Assert.assertEquals("Consultingwerk.ProparseAblTests.SCL5986.Fixtures.Settings", child(anno, 0).getText());
		// END ANNOTATION.
		assertChildren(child(anno, 3), NodeTypes.ANNOTATION_TYPE);
		Assert.assertEquals("annotation", child(anno, 3).firstChild().getText());

		ArrayList<JPNode> properties = statements(child(anno, 2));
		Assert.assertEquals(10, properties.size());
		for (JPNode define : properties) {
			Assert.assertEquals(NodeTypes.DEFINE, define.getType());
			Assert.assertEquals(NodeTypes.PROPERTY, define.getState2());
		}
		// define public property Name as character initial "unnamed":u.
		JPNode define = properties.get(0);
		assertChildren(define, NodeTypes.PUBLIC, NodeTypes.PROPERTY, NodeTypes.ID, NodeTypes.AS, NodeTypes.CHARACTER,
				NodeTypes.INITIAL, NodeTypes.PERIOD);
		Assert.assertEquals("Name", child(define, 2).getText());
		assertChildren(child(define, 5), NodeTypes.QSTRING);
		Assert.assertTrue(child(define, 5).firstChild().getText().startsWith("\"unnamed\""));
		// define public property Count as integer initial 42.
		define = properties.get(1);
		assertChildren(define, NodeTypes.PUBLIC, NodeTypes.PROPERTY, NodeTypes.ID, NodeTypes.AS, NodeTypes.INTEGER,
				NodeTypes.INITIAL, NodeTypes.PERIOD);
		Assert.assertEquals("42", child(define, 5).firstChild().getText());
		// define public property ValidFrom as date initial 01/01/2026.
		define = properties.get(4);
		Assert.assertEquals(NodeTypes.DATE, child(define, 4).getType());
		Assert.assertEquals(NodeTypes.LEXDATE, child(define, 5).firstChild().getType());
		// define public property ChangedAt as datetime-tz.
		define = properties.get(6);
		assertChildren(define, NodeTypes.PUBLIC, NodeTypes.PROPERTY, NodeTypes.ID, NodeTypes.AS, NodeTypes.DATETIMETZ,
				NodeTypes.PERIOD);
		// define public property Enabled as logical initial true.
		define = properties.get(7);
		Assert.assertEquals(NodeTypes.TRUE_KW, child(define, 5).firstChild().getType());
		// define property Timeout as integer initial {&DefaultTimeout}. (PUBLIC omitted, preprocessor value)
		define = properties.get(8);
		assertChildren(define, NodeTypes.PROPERTY, NodeTypes.ID, NodeTypes.AS, NodeTypes.INTEGER, NodeTypes.INITIAL,
				NodeTypes.PERIOD);
		Assert.assertEquals("Timeout", child(define, 1).getText());
		Assert.assertEquals("30", child(define, 4).firstChild().getText());
		// define public property Format as character. (a property named like a keyword)
		define = properties.get(9);
		Assert.assertEquals(NodeTypes.ID, child(define, 2).getType());
		Assert.assertEquals("Format", child(define, 2).getText());

		// symbol level: the properties are variables of the root scope
		Assert.assertEquals("Consultingwerk.ProparseAblTests.SCL5986.Fixtures.Settings", pu.getRootScope().getClassName());
		Assert.assertNotNull(pu.getRootScope().lookupVariable("Name"));
		Assert.assertNotNull(pu.getRootScope().lookupVariable("Timeout"));
		Assert.assertNotNull(pu.getRootScope().lookupVariable("Format"));
		Assert.assertEquals(10, pu.getRootScope().getVariables().size());
	}

	public void testAnnotationTypeResourceMapping() throws RefactorException {
		ParseUnit pu = parse("annotation-type-resourcemapping.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> statements = statements(top);
		// USING ... / @Deprecated (...). / ANNOTATION ... END ANNOTATION.
		Assert.assertEquals(3, statements.size());
		Assert.assertEquals(NodeTypes.USING, statements.get(0).getType());
		Assert.assertEquals(NodeTypes.ANNOTATION, statements.get(1).getType());
		JPNode anno = statements.get(2);
		Assert.assertEquals(NodeTypes.ANNOTATION_TYPE, anno.getType());
		Assert.assertEquals("ANNOTATION", anno.getText());
		assertChildren(anno, NodeTypes.TYPE_NAME, NodeTypes.LEXCOLON, NodeTypes.Code_block, NodeTypes.END, NodeTypes.PERIOD);
		Assert.assertEquals("service.ResourceMapping", child(anno, 0).getText());
		// the declared name is not resolved against the PROPATH (type_name2, like CLASS)
		Assert.assertEquals("", child(anno, 0).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		Assert.assertEquals(5, statements(child(anno, 2)).size());
		assertChildren(child(anno, 3), NodeTypes.ANNOTATION_TYPE);
		Assert.assertEquals("service.ResourceMapping", pu.getRootScope().getClassName());
	}

	// ---------------------------------------------------------------- R2 / R3: usages

	public void testTypedAnnotationClass() throws RefactorException {
		ParseUnit pu = parse("typed-annotation-class.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> usages = top.query(NodeTypes.Typed_annotation);
		Assert.assertEquals(10, usages.size());
		for (JPNode usage : usages) {
			Assert.assertTrue(usage.isStateHead());
			Assert.assertEquals("", usage.getText());
			Assert.assertEquals(NodeTypes.LEFTBRACE, usage.firstChild().getType());
			Assert.assertEquals(NodeTypes.TYPE_NAME, child(usage, 1).getType());
			Assert.assertEquals(NodeTypes.PERIOD, usage.lastChild().getType());
			Assert.assertEquals(NodeTypes.RIGHTBRACE, usage.lastChild().prevSibling().getType());
		}

		// in front of the CLASS statement: [Exportable]. @Deprecated (...). [ResourceMapping (...)].
		JPNode classNode = top.findDirectChild(NodeTypes.CLASS);
		Assert.assertNotNull(classNode);
		JPNode prev = classNode.prevSibling();
		Assert.assertEquals(NodeTypes.Typed_annotation, prev.getType());
		Assert.assertEquals("ResourceMapping", child(prev, 1).getText());
		prev = prev.prevSibling();
		Assert.assertEquals(NodeTypes.ANNOTATION, prev.getType());
		prev = prev.prevSibling();
		Assert.assertEquals(NodeTypes.Typed_annotation, prev.getType());
		Assert.assertEquals("Exportable", child(prev, 1).getText());
		// [Exportable].  - a marker without parentheses
		assertChildren(prev, NodeTypes.LEFTBRACE, NodeTypes.TYPE_NAME, NodeTypes.RIGHTBRACE, NodeTypes.PERIOD);
		Assert.assertEquals(14, prev.firstChild().getLine());
		Assert.assertEquals(1, prev.firstChild().getColumn());
		// [ResourceMapping (type = "REST", alias = "orders")].
		JPNode usage = classNode.prevSibling();
		assertChildren(usage, NodeTypes.LEFTBRACE, NodeTypes.TYPE_NAME, NodeTypes.LEFTPAREN,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.QSTRING, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.QSTRING,
				NodeTypes.RIGHTPAREN, NodeTypes.RIGHTBRACE, NodeTypes.PERIOD);
		Assert.assertEquals("type", child(usage, 3).getText());
		Assert.assertEquals("\"REST\"", child(usage, 5).getText());
		Assert.assertEquals("alias", child(usage, 7).getText());

		ArrayList<JPNode> members = statements(classNode.findDirectChild(NodeTypes.Code_block));
		// [Exportable()]. define private variable cValues ...
		usage = members.get(0);
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		assertChildren(usage, NodeTypes.LEFTBRACE, NodeTypes.TYPE_NAME, NodeTypes.LEFTPAREN, NodeTypes.RIGHTPAREN,
				NodeTypes.RIGHTBRACE, NodeTypes.PERIOD);
		Assert.assertEquals(NodeTypes.DEFINE, members.get(1).getType());
		Assert.assertEquals(NodeTypes.VARIABLE, members.get(1).getState2());

		// [service.ResourceMapping ( ... multi-line, every string form ... )].
		usage = members.get(2);
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		Assert.assertEquals("service.ResourceMapping", child(usage, 1).getText());
		Assert.assertEquals(NodeTypes.ID, child(usage, 3).getType());
		Assert.assertEquals("type", child(usage, 3).getText());
		Assert.assertTrue(child(usage, 5).getText().startsWith("\"REST\""));
		Assert.assertEquals(NodeTypes.QSTRING, child(usage, 5).getType());
		Assert.assertEquals("\"?filter=~{filter~}\"", child(usage, 13).getText());
		Assert.assertEquals("\"\"", child(usage, 17).getText());
		Assert.assertEquals(NodeTypes.RIGHTPAREN, child(usage, 22).getType());
		Assert.assertEquals(NodeTypes.RIGHTBRACE, child(usage, 23).getType());
		Assert.assertEquals(NodeTypes.PERIOD, child(usage, 24).getType());

		// [Limits (count = -1, ratio = 1.5, enabled = true, hidden = FALSE, visible = yes, locked = no,
		//          validFrom = 01/02/2026, changedAt = "...", comment = ?)].
		usage = members.get(3);
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		Assert.assertEquals("Limits", child(usage, 1).getText());
		assertChildren(usage, NodeTypes.LEFTBRACE, NodeTypes.TYPE_NAME, NodeTypes.LEFTPAREN,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.NUMBER, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.NUMBER, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.TRUE_KW, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.FALSE_KW, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.YES, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.NO, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.LEXDATE, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.QSTRING, NodeTypes.COMMA,
				NodeTypes.ID, NodeTypes.EQUAL, NodeTypes.UNKNOWNVALUE,
				NodeTypes.RIGHTPAREN, NodeTypes.RIGHTBRACE, NodeTypes.PERIOD);
		Assert.assertEquals("count", child(usage, 3).getText());
		Assert.assertEquals("-1", child(usage, 5).getText());
		Assert.assertEquals("1.5", child(usage, 9).getText());
		Assert.assertEquals("01/02/2026", child(usage, 29).getText());
		Assert.assertEquals("comment", child(usage, 35).getText());
		// both usages belong to the property definition that follows
		Assert.assertEquals(NodeTypes.DEFINE, members.get(4).getType());
		Assert.assertEquals(NodeTypes.PROPERTY, members.get(4).getState2());

		// [Exportable]. define public event ... / [Exportable]. constructor ...
		Assert.assertEquals(NodeTypes.Typed_annotation, members.get(5).getType());
		Assert.assertEquals(NodeTypes.DEFINE, members.get(6).getType());
		Assert.assertEquals(NodeTypes.EVENT, members.get(6).getState2());
		Assert.assertEquals(NodeTypes.Typed_annotation, members.get(7).getType());
		Assert.assertEquals(NodeTypes.CONSTRUCTOR, members.get(8).getType());

		// [ResourceMapping( ... closing bracket on its own line )]. @RestMethod (...). [Column (format = ..., label = ...)]. method ...
		usage = members.get(9);
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		Assert.assertEquals("ResourceMapping", child(usage, 1).getText());
		Assert.assertEquals(NodeTypes.RIGHTBRACE, usage.lastChild().prevSibling().getType());
		Assert.assertEquals(usage.lastChild().prevSibling().prevSibling().getLine() + 1, usage.lastChild().prevSibling().getLine());
		Assert.assertEquals(NodeTypes.ANNOTATION, members.get(10).getType());
		usage = members.get(11);
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		Assert.assertEquals("Column", child(usage, 1).getText());
		// property names that are keywords are plain IDs
		Assert.assertEquals(NodeTypes.ID, child(usage, 3).getType());
		Assert.assertEquals("format", child(usage, 3).getText());
		Assert.assertEquals("\"x(8)\"", child(usage, 5).getText());
		Assert.assertEquals(NodeTypes.ID, child(usage, 7).getType());
		Assert.assertEquals("label", child(usage, 7).getText());
		JPNode method = members.get(12);
		Assert.assertEquals(NodeTypes.METHOD, method.getType());
		// walking back from the member over its annotations, like a consumer does
		Assert.assertSame(members.get(11), method.prevSibling());
		Assert.assertSame(members.get(10), method.prevSibling().prevSibling());
		Assert.assertSame(members.get(9), method.prevSibling().prevSibling().prevSibling());

		// [Final]. - an annotation type named like a keyword
		usage = members.get(13);
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		Assert.assertEquals(NodeTypes.TYPE_NAME, child(usage, 1).getType());
		Assert.assertEquals("Final", child(usage, 1).getText());
		Assert.assertEquals(NodeTypes.METHOD, members.get(14).getType());
		Assert.assertEquals(15, members.size());

		// R4: array subscripts in the method body are still subscripts
		Assert.assertEquals(3, top.query(NodeTypes.Array_subscript).size());
		Assert.assertEquals(0, top.query(NodeTypes.ANNOTATION_TYPE).size());
	}

	public void testTypedAnnotationInterface() throws RefactorException {
		ParseUnit pu = parse("typed-annotation-interface.txt");
		JPNode top = pu.getTopNode();
		Assert.assertEquals(5, top.query(NodeTypes.Typed_annotation).size());
		JPNode iface = top.findDirectChild(NodeTypes.INTERFACE);
		Assert.assertNotNull(iface);
		Assert.assertEquals(NodeTypes.Typed_annotation, iface.prevSibling().getType());
		Assert.assertEquals("ResourceMapping", child(iface.prevSibling(), 1).getText());
		Assert.assertEquals(NodeTypes.Typed_annotation, iface.prevSibling().prevSibling().getType());
		ArrayList<JPNode> members = statements(iface.findDirectChild(NodeTypes.Code_block));
		Assert.assertEquals(7, members.size());
		Assert.assertEquals(NodeTypes.Typed_annotation, members.get(0).getType());
		Assert.assertEquals(NodeTypes.DEFINE, members.get(1).getType());
		Assert.assertEquals(NodeTypes.Typed_annotation, members.get(2).getType());
		Assert.assertEquals(NodeTypes.DEFINE, members.get(3).getType());
		Assert.assertEquals(NodeTypes.Typed_annotation, members.get(4).getType());
		Assert.assertEquals("operation", child(members.get(4), 3).getText());
		Assert.assertEquals(NodeTypes.METHOD, members.get(5).getType());
		Assert.assertEquals(NodeTypes.METHOD, members.get(6).getType());
	}

	public void testTypedAnnotationEnum() throws RefactorException {
		ParseUnit pu = parse("typed-annotation-enum.txt");
		JPNode top = pu.getTopNode();
		JPNode enumNode = top.findDirectChild(NodeTypes.ENUM);
		Assert.assertNotNull(enumNode);
		// [Exportable]. @Serializable. enum ...
		Assert.assertEquals(NodeTypes.ANNOTATION, enumNode.prevSibling().getType());
		JPNode usage = enumNode.prevSibling().prevSibling();
		Assert.assertEquals(NodeTypes.Typed_annotation, usage.getType());
		assertChildren(usage, NodeTypes.LEFTBRACE, NodeTypes.TYPE_NAME, NodeTypes.RIGHTBRACE, NodeTypes.PERIOD);
		Assert.assertEquals(1, top.query(NodeTypes.Typed_annotation).size());
	}

	public void testTypedAnnotationInMethodBody() throws RefactorException {
		// not allowed by the compiler, but framed like every other usage (R2: syntax only)
		ParseUnit pu = parse("typed-annotation-body.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> usages = top.query(NodeTypes.Typed_annotation);
		Assert.assertEquals(2, usages.size());
		Assert.assertEquals("Trace", child(usages.get(0), 1).getText());
		Assert.assertEquals(NodeTypes.ASSIGN, usages.get(0).nextSibling().getType());
		Assert.assertEquals("Exportable", child(usages.get(1), 1).getText());
		// the second one sits inside the DO block
		Assert.assertEquals(NodeTypes.Code_block, usages.get(1).parent().getType());
		Assert.assertEquals(NodeTypes.DO, usages.get(1).parent().parent().getType());
	}

	public void testTypedAnnotationInProcedure() throws RefactorException {
		ParseUnit pu = parse("typed-annotation-procedure.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> usages = top.query(NodeTypes.Typed_annotation);
		Assert.assertEquals(3, usages.size());
		Assert.assertEquals(NodeTypes.ASSIGN, usages.get(0).nextSibling().getType());
		Assert.assertEquals(NodeTypes.PROCEDURE, usages.get(1).nextSibling().getType());
		Assert.assertEquals("Trace", child(usages.get(1), 1).getText());
		Assert.assertEquals("label", child(usages.get(1), 7).getText());
		Assert.assertEquals(NodeTypes.ID, child(usages.get(1), 7).getType());
		Assert.assertEquals(NodeTypes.ASSIGN, usages.get(2).nextSibling().getType());
	}

	/** R3: the annotation type name of a usage is resolved like every other TYPE_NAME. */
	public void testTypeNameResolution() throws RefactorException {
		ParseUnit pu = parse("typed-annotation-resolution.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> usages = top.query(NodeTypes.Typed_annotation);
		Assert.assertEquals(4, usages.size());
		// [FrameworkSettings]. - imported by USING Consultingwerk.Framework.*, the .cls exists on the PROPATH
		Assert.assertEquals("FrameworkSettings", child(usages.get(0), 1).getText());
		Assert.assertEquals("Consultingwerk.Framework.FrameworkSettings",
				child(usages.get(0), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		// [Consultingwerk.Framework.FrameworkSettings (WaitStateManager = "x")]. - the qualified name as written
		Assert.assertEquals("Consultingwerk.Framework.FrameworkSettings", child(usages.get(1), 1).getText());
		Assert.assertEquals("Consultingwerk.Framework.FrameworkSettings",
				child(usages.get(1), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		// [Exportable]. - nowhere on the PROPATH: not attributed
		Assert.assertEquals("Exportable", child(usages.get(2), 1).getText());
		Assert.assertEquals("", child(usages.get(2), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		// [FrameworkSettings]. in front of the method
		Assert.assertEquals("Consultingwerk.Framework.FrameworkSettings",
				child(usages.get(3), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		Assert.assertEquals(NodeTypes.METHOD, usages.get(3).nextSibling().getType());
	}

	/** R3: a usage in front of a class member whose type lives on the PROPATH as an annotation type. */
	public void testTypeNameResolutionOfAnnotationType() throws RefactorException {
		ParseUnit pu = parse("typed-annotation-class.txt");
		ArrayList<JPNode> usages = pu.getTopNode().query(NodeTypes.Typed_annotation);
		// using service.* from propath. + [ResourceMapping (...)]. -> service/ResourceMapping.cls (a stub on the PROPATH)
		Assert.assertEquals("ResourceMapping", child(usages.get(1), 1).getText());
		Assert.assertEquals("service.ResourceMapping", child(usages.get(1), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		Assert.assertEquals("service.ResourceMapping", child(usages.get(3), 1).getText());
		Assert.assertEquals("service.ResourceMapping", child(usages.get(3), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
		Assert.assertEquals("", child(usages.get(0), 1).attrGetS(IConstants.QUALIFIED_CLASS_INT));
	}

	// ---------------------------------------------------------------- R4: no regressions

	/** "annotation" as an identifier and brackets as array subscripts keep parsing as before. */
	public void testAnnotationAsIdentifier() throws RefactorException {
		ParseUnit pu = parse("annotation-identifier.txt");
		JPNode top = pu.getTopNode();
		Assert.assertEquals(0, top.query(NodeTypes.ANNOTATION_TYPE).size());
		Assert.assertEquals(0, top.query(NodeTypes.Typed_annotation).size());
		// cValues[1] = ..., cValues[2] = cValues[1] + ..., ... = cValues[2], cValues[3] = ...
		Assert.assertEquals(5, top.query(NodeTypes.Array_subscript).size());
		// three @ annotations: @lowercase. and @Test. in front of the class and of the method
		Assert.assertEquals(3, top.query(NodeTypes.ANNOTATION).size());
		// the block label: "annotation:" and "leave annotation."
		ArrayList<JPNode> labels = top.query(NodeTypes.BLOCK_LABEL);
		Assert.assertEquals(2, labels.size());
		Assert.assertEquals("annotation", labels.get(0).getText());
		Assert.assertEquals("annotation", labels.get(1).getText());
		// the method named annotation
		JPNode method = top.query(NodeTypes.METHOD).get(0);
		JPNode name = method.findDirectChild(NodeTypes.ID);
		Assert.assertNotNull(name);
		Assert.assertEquals("annotation", name.getText());
		// the variable named annotation is a symbol of the class
		Assert.assertNotNull(pu.getRootScope().lookupVariable("annotation"));
		// every other occurrence is a plain identifier
		int count = 0;
		for (JPNode id : top.query(NodeTypes.ID))
			if (id.getText().equalsIgnoreCase("annotation"))
				count++;
		Assert.assertTrue("expected a number of ID nodes named annotation, got " + count, count >= 12);
	}

	/** "annotation" as a member name behind a colon keeps the keyword type, like every unreserved keyword. */
	public void testAnnotationAsMemberName() throws RefactorException {
		ParseUnit pu = parse("annotation-member.txt");
		JPNode top = pu.getTopNode();
		Assert.assertEquals(0, top.query(NodeTypes.Typed_annotation).size());
		// this-object:Annotation and poOther:Annotation
		ArrayList<JPNode> members = top.query(NodeTypes.ANNOTATION_TYPE);
		Assert.assertEquals(2, members.size());
		for (JPNode member : members) {
			Assert.assertEquals("Annotation", member.getText());
			Assert.assertEquals(NodeTypes.OBJCOLON, member.prevSibling().getType());
		}
		// the same convention holds for LABEL: this-object:Label and poOther:Label
		ArrayList<JPNode> labels = top.query(NodeTypes.LABEL);
		Assert.assertEquals(2, labels.size());
		// the property definitions name their property with an ID
		int properties = 0;
		for (JPNode define : top.query(NodeTypes.DEFINE)) {
			if (define.getState2() != NodeTypes.PROPERTY)
				continue;
			properties++;
			Assert.assertEquals(NodeTypes.ID, child(define, 2).getType());
		}
		Assert.assertEquals(2, properties);
	}

	public void testUnreservedKeywordMember() throws RefactorException {
		ParseUnit pu = parse("unreserved-member.txt");
		JPNode top = pu.getTopNode();
		ArrayList<JPNode> labels = top.query(NodeTypes.LABEL);
		Assert.assertEquals(2, labels.size());
		Assert.assertEquals("Label", labels.get(0).getText());
		Assert.assertEquals(NodeTypes.Widget_ref, labels.get(0).parent().getType());
	}

	// ---------------------------------------------------------------- syntax errors

	private void assertParseFails(String fixture) {
		try {
			parse(fixture);
			Assert.fail(fixture + " should not parse");
		} catch (RefactorException e) {
			// expected: a malformed usage must not swallow the following statement
		}
	}

	public void testSyntaxErrors() {
		assertParseFails("invalid-unclosed-bracket.txt");
		assertParseFails("invalid-unclosed-parenthesis.txt");
		assertParseFails("invalid-missing-period.txt");
		assertParseFails("invalid-missing-name.txt");
	}

}
