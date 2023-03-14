package test.SCL3987;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import junit.framework.Assert;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

public class TestOperatorFullText extends ProparseTestCase {

	public void setUp () throws Exception {
		super.setUp();
	}
	
	public void test () throws RefactorException, IOException {
		File original;
		ParseUnit pu;
		byte[] originalText;
		byte[] nodeText;
		
		original = new File ("src/test/SCL3987/testCode.p");
		
		originalText = Files.readAllBytes(original.toPath());
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		
		nodeText = pu.getTopNode().toStringSourceText().replaceAll("(?!<\r)\n", "\r\n").getBytes();

		Assert.assertEquals("Wrong length", originalText.length, nodeText.length);
		for (int i = 0; i < originalText.length; i++)
			Assert.assertEquals("Error at position " + i, originalText[i], nodeText[i]);
	}
}
