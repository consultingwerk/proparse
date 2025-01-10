package test.SCL4054;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import junit.framework.Assert;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

public class TestParseIfDefined extends ProparseTestCase {

	public void setUp () throws Exception {
		super.setUp();
	}
	
	public void test () throws RefactorException, IOException {
		File original;
		ParseUnit pu;
		byte[] originalText;
		byte[] nodeText;
		
		original = new File ("src/test/SCL4054/testCode.p");
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		
		try (FileWriter fw = new FileWriter(new File ("C:\\Temp\\debug.txt"))) {
			fw.write(pu.getTopNode().toStringSourceText());
		}
	}
}
