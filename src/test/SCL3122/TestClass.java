package test.SCL3122;

import java.io.File;

import org.prorefactor.treeparser.ParseUnit;
import org.prorefactor.treeparser.RunHandle;
import org.prorefactor.treeparser.Routine;
import org.prorefactor.treeparser.SymbolScope;
import org.prorefactor.treeparser.Variable;

import test.ProparseTestCase;

public class TestClass
	extends ProparseTestCase {

	@Override
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

	public void testMethod1_RunHandleValue() throws Exception {

		File file = new File("src/test/SCL3122/TestCode.cls");
		ParseUnit pu = new ParseUnit(file);
		pu.treeParser01();

		SymbolScope rootScope = pu.getRootScope();
		assertNotNull("Root scope should not be null", rootScope);

		Routine methodOne = rootScope.lookupRoutine("MethodOne");
		assertNotNull("MethodOne routine should be found", methodOne);

		SymbolScope methodScope = methodOne.getRoutineScope();
		assertNotNull("Method scope should not be null", methodScope);

		java.util.Collection<Variable> variables = methodScope.getVariables();
		assertNotNull("Variables collection should not be null", variables);

		Variable[] vars = variables.toArray(new Variable[0]);
		assertEquals("Should have exactly 3 variables", 3, vars.length);

		// vars[0] = hSetOnce (line 20)
		Object value = vars[0].getValue();
		assertNotNull("hSetOnce should have a value", value);
		assertTrue("hSetOnce value should be a RunHandle", value instanceof RunHandle);

		RunHandle runHandle = (RunHandle) value;
		assertEquals("RunHandle value should be TestProcedure.p", "TestProcedure.p", runHandle.getValue());
	}

	public void testMethod2_RunHandleValue() throws Exception {

		File file = new File("src/test/SCL3122/TestCode.cls");
		ParseUnit pu = new ParseUnit(file);
		pu.treeParser01();

		SymbolScope rootScope = pu.getRootScope();
		assertNotNull("Root scope should not be null", rootScope);

		Routine methodOne = rootScope.lookupRoutine("MethodOne");
		assertNotNull("MethodOne routine should be found", methodOne);

		SymbolScope methodScope = methodOne.getRoutineScope();
		assertNotNull("Method scope should not be null", methodScope);

		java.util.Collection<Variable> variables = methodScope.getVariables();
		assertNotNull("Variables collection should not be null", variables);

		Variable[] vars = variables.toArray(new Variable[0]);
		assertEquals("Should have exactly 3 variables", 3, vars.length);

		// vars[2] = hSetTwice (line 22)
		Object value = vars[2].getValue();
		assertNotNull("hSetTwice should have a value", value);
		assertTrue("hSetTwice value should be a RunHandle", value instanceof RunHandle);

		RunHandle runHandle = (RunHandle) value;
		assertEquals("RunHandle value should be TestProcedure1.p", "TestProcedure1.p", runHandle.getValue());
	}

	public void testMethod3_NoRunHandleValue() throws Exception {

		File file = new File("src/test/SCL3122/TestCode.cls");
		ParseUnit pu = new ParseUnit(file);
		pu.treeParser01();

		SymbolScope rootScope = pu.getRootScope();
		assertNotNull("Root scope should not be null", rootScope);

		Routine methodOne = rootScope.lookupRoutine("MethodOne");
		assertNotNull("MethodOne routine should be found", methodOne);

		SymbolScope methodScope = methodOne.getRoutineScope();
		assertNotNull("Method scope should not be null", methodScope);

		java.util.Collection<Variable> variables = methodScope.getVariables();
		assertNotNull("Variables collection should not be null", variables);

		Variable[] vars = variables.toArray(new Variable[0]);
		assertEquals("Should have exactly 3 variables", 3, vars.length);

		// vars[1] = cText (line 21)
		Object value = vars[1].getValue();
		assertNull("cText should not have a RunHandle value (it's just a CHARACTER)", value);
	}
}
