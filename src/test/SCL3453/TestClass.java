package test.SCL3453;

import java.io.File;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.IntegerIndex;
import com.joanju.proparse.NodeTypes;
import com.joanju.proparse.ProToken;

import de.consultingwerk.proparse.refactor.RefactoredToken;

import test.ProparseTestCase;

public class TestClass 
	extends ProparseTestCase 
{
	public void test_01 () throws RefactorException
	{
		File file;
		ParseUnit pu;
		JPNode node;
		
		file = new File ("src/test/SCL3453/testCode.p");
		pu = new ParseUnit (file, "ISO8859-1");
		
		pu.treeParser01();
		
		assertNotNull(pu);
		assertNotNull(pu.getTopNode());
		
		node = pu.getTopNode();

		System.out.println ("\n##### Before #####\n");
		System.out.println(node.toStringSourceText());
		
		this.change(node);
		
		System.out.println ("\n##### After #####\n");
		System.out.println(node.toStringSourceText());
		
		System.out.println ("\n#####  End  #####\n");
	}

	private void change (JPNode node)
	{
		IntegerIndex<String> idx;
		
		if (node.getType() == NodeTypes.MESSAGE)
		{
			node.setToken(new RefactoredToken (node.getToken(), "DISPLAY"));
		}
		node.setText(node.getText().toLowerCase());
		
		if (node.firstChild() != null)
			this.change(node.firstChild());
		if (node.nextSibling() != null)
			this.change(node.nextSibling());
	}
}
