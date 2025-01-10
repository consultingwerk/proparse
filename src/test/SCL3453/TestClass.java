package test.SCL3453;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import com.joanju.proparse.NodeTypes;

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
		
		try (FileWriter fw = new FileWriter(new File ("C:\\Temp\\debug.txt"))) {
			fw.write(pu.getTopNode().toStringSourceText());
		} catch (IOException e) {
			throw new RefactorException(e);
		}
	}

	private void change (JPNode node)
	{
		if (node.getType() == NodeTypes.MESSAGE)
			node.setToken(new RefactoredToken (node.getToken(), "DISPLAY"));
		else if (node.getType() == NodeTypes.VIEWAS || node.getType() == NodeTypes.ALERTBOX)
			node.setToken(new RefactoredToken(node.getToken(), ""));
		else if (node.getType() == NodeTypes.QSTRING && node.getText().charAt(node.getText().length() - 1) == '"')
			node.setText(String.format("%s:u", node.getText()));
		else
			node.setText(node.getText().toLowerCase());
		
		if (node.firstChild() != null)
			this.change(node.firstChild());
		if (node.nextSibling() != null)
			this.change(node.nextSibling());
	}
}
