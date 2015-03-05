package org.prorefactor.core.unittest;

import java.io.File;

import junit.framework.TestCase;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.Environment;
import com.joanju.proparse.NodeTypes;

/** For testing API and Backwards API access to the parser. */
public class ApiTest1  extends TestCase {

	Environment env = Environment.instance();
	
	public void test01() throws Exception {
		
		JPNode node;
		
		parserConfigSet("batch-mode", "false");
		parserConfigSet("opsys", "WIN32");
		parserConfigSet("propath", "abc,def");
		parserConfigSet("proversion", "10.2A01");
		parserConfigSet("window-system", "MS-WINDOWS");

		File f = new File("data/hello.p");
		ParseUnit pu = new ParseUnit(f);
		pu.treeParser01();
		int numDisplay = pu.getTopNode().query("DISPLAY").length;
		assertEquals(1, numDisplay);

		f = new File("data/no-undo.p");
		pu = new ParseUnit(f);
		pu.treeParser01();
		node = pu.getTopNode().findDirectChild(NodeTypes.DEFINE);
		assertEquals("VARIABLE", node.attrGetS("state2"));
}

	private void parserConfigSet(String key, String val) {
		env.configSet(key, val);
	}

}
