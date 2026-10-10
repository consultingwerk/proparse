package test.SCL6029;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

import test.ProparseTestCase;

/**
 * SCL-6029: a bound generic type with more than one type argument
 * (GenericCharacterEnumPredicate&lt;SmartSchedulerJobPlanQuery, SchedulerJobPlanningTypeEnum&gt;)
 * failed to parse with "unexpected token: ,". The whole type-argument list is part of the
 * TYPE_NAME node now, in every position a type name can occur. The text of the node is the type
 * name without whitespace; the Proparse ABL engine produces the same text.
 */
public class TestGenericTypeArguments extends ProparseTestCase {

	private static final String FIXTURES = "src/test/SCL6029/";

	@Override
	protected String getProversion() {
		return "13.0";
	}

	private ParseUnit parse(String fixture) throws RefactorException {
		ParseUnit pu = new ParseUnit(new File(FIXTURES + fixture), "ISO8859-1");
		pu.treeParser01();
		Assert.assertNotNull("Failed to parse " + fixture, pu.getTopNode());
		return pu;
	}

	/** The texts of all TYPE_NAME nodes of the unit, in source order. */
	private static ArrayList<String> typeNames(ParseUnit pu) {
		ArrayList<String> names = new ArrayList<String>();
		for (JPNode node : pu.getTopNode().query(NodeTypes.TYPE_NAME))
			names.add(node.getText());
		return names;
	}

	private static void assertTypeNames(ParseUnit pu, String... expected) {
		ArrayList<String> actual = typeNames(pu);
		Assert.assertEquals("TYPE_NAME nodes: " + actual, expected.length, actual.size());
		for (int i = 0; i < expected.length; i++)
			Assert.assertEquals("TYPE_NAME node " + i, expected[i], actual.get(i));
	}

	/** The first statement head of the given type below the program root. */
	private static JPNode statement(ParseUnit pu, int type) {
		for (JPNode node : pu.getTopNode().query(type))
			if (node.isStateHead())
				return node;
		Assert.fail("No " + NodeTypes.getTypeName(type) + " statement");
		return null;
	}

	// ------------------------------------------------------------ the ticket's reproduction

	/** The single-line shape the Business Entity Generator emits (DEFINE PROPERTY, NEW). */
	public void testBusinessEntityGeneratorShape() throws RefactorException {
		ParseUnit pu = parse("SmartSchedulerJobPlanQuery.cls");
		assertTypeNames(pu,
				"Consultingwerk.OERA.QueryBuilder.*",
				"Consultingwerk.SmartFramework.Scheduler.*",
				"SCL6029.SmartSchedulerJobPlanQuery",
				"TableQuery",
				"GenericCharacterEnumPredicate<SmartSchedulerJobPlanQuery,Consultingwerk.SmartFramework.Scheduler.SchedulerJobPlanningTypeEnum>",
				"GenericCharacterPredicate<SmartSchedulerJobPlanQuery>",
				"SmartSchedulerJobPlanQuery",                        // CONSTRUCTOR
				"GenericCharacterEnumPredicate<SmartSchedulerJobPlanQuery,Consultingwerk.SmartFramework.Scheduler.SchedulerJobPlanningTypeEnum>",
				"GenericCharacterPredicate<SmartSchedulerJobPlanQuery>");
	}

	/** The type name, the opening bracket and the second argument on separate lines. */
	public void testMultiLine() throws RefactorException {
		ParseUnit pu = parse("multiline.p");
		assertTypeNames(pu,
				"Consultingwerk.OERA.QueryBuilder.*",
				"Consultingwerk.SmartFramework.Scheduler.*",
				"GenericCharacterEnumPredicate<SmartSchedulerJobPlanQuery,Consultingwerk.SmartFramework.Scheduler.SchedulerJobPlanningTypeEnum>",
				"Pair<Alpha,Beta>");

		// The type name is the only child of AS, followed by NO-UNDO; no stray COMMA or angle bracket nodes.
		JPNode define = statement(pu, NodeTypes.DEFINE);
		JPNode as = define.query(NodeTypes.AS).get(0);
		Assert.assertEquals(NodeTypes.TYPE_NAME, as.firstChild().getType());
		Assert.assertNull(as.firstChild().nextSibling());
		Assert.assertEquals(NodeTypes.NOUNDO, as.nextSibling().getType());
		Assert.assertEquals(0, pu.getTopNode().query(NodeTypes.COMMA).size());
		Assert.assertEquals(0, pu.getTopNode().query(NodeTypes.LEFTANGLE).size());
		Assert.assertEquals(0, pu.getTopNode().query(NodeTypes.RIGHTANGLE).size());
	}

	// ------------------------------------------------------------ every position of a type name

	public void testPositionsInClass() throws RefactorException {
		ParseUnit pu = parse("positions.cls");
		assertTypeNames(pu,
				"SCL6029.Types.*",
				"SCL6029.Positions",
				"Base<Alpha,Beta>",                                  // INHERITS
				"IFirst<Alpha,Beta>", "ISecond<Gamma>",              // IMPLEMENTS a, b
				"Pair<Alpha,Beta>",                                  // DEFINE VARIABLE
				"Outer<Alpha,Inner<Beta,Gamma>>",                    // nested
				"Single<Alpha>",                                     // one argument, as before
				"Alpha",                                             // no argument, as before
				"System.Collections.Generic.List<Alpha,Beta>[]",     // .NET array of a generic type
				"Pair<Alpha,Beta>",                                  // DEFINE PROPERTY
				"Pair<Alpha,Beta>",                                  // event signature parameter
				"Positions",                                         // CONSTRUCTOR
				"Pair<Alpha,Beta>",                                  // constructor parameter
				"Pair<Alpha,Beta>",                                  // method return type
				"Pair<Alpha,Beta>", "Alpha",                         // method parameters
				"Pair<Alpha,Beta>",                                  // DEFINE VARIABLE in method
				"Pair<Alpha,Beta>",                                  // NEW
				"Pair<Alpha,Beta>",                                  // CAST
				"Pair<Alpha,Beta>",                                  // TYPE-OF
				"Pair<Alpha,Beta>",                                  // GET-CLASS
				"Pair<Alpha,Beta>");                                 // DYNAMIC-INVOKE

		// The comparisons "i < 2" and "i > 1" are still expressions (LTHAN / GTHAN operator nodes).
		Assert.assertEquals(1, pu.getTopNode().query(NodeTypes.LTHAN).size());
		Assert.assertEquals(1, pu.getTopNode().query(NodeTypes.GTHAN).size());
		Assert.assertEquals(0, pu.getTopNode().query(NodeTypes.LEFTANGLE).size());
		Assert.assertEquals(0, pu.getTopNode().query(NodeTypes.RIGHTANGLE).size());
	}

	public void testPositionsInProcedure() throws RefactorException {
		ParseUnit pu = parse("procedure.p");
		assertTypeNames(pu,
				"Pair<Alpha,Beta>",          // DEFINE INPUT PARAMETER
				"Pair<Alpha,Beta>",          // DEFINE OUTPUT PARAMETER
				"Pair<Alpha,Beta>",          // DEFINE VARIABLE
				"Pair<Alpha,Beta>",          // FUNCTION RETURNS
				"Pair<Alpha,Beta>",          // function parameter
				"Pair<Alpha,Beta>");         // internal procedure parameter
	}

}
