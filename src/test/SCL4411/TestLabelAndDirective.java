package test.SCL4411;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

public class TestLabelAndDirective 
	extends ProparseTestCase {

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

	public void test_LabelAndDirective () throws RefactorException, IOException {
		File original;
		ParseUnit pu;
		
		original = new File ("src/test/SCL4411/testCode_LabelAndDirective.p");
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
	}

	public void test_Label () throws RefactorException, IOException {
		File original;
		ParseUnit pu;
		
		original = new File ("src/test/SCL4411/testCode_Label.p");
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
	}

	public void test_Directive () throws RefactorException, IOException {
		File original;
		ParseUnit pu;
		
		original = new File ("src/test/SCL4411/testCode_Directive.p");
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
	}

	public void test_Neither () throws RefactorException, IOException {
		File original;
		ParseUnit pu;
		
		original = new File ("src/test/SCL4411/testCode_Neither.p");
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
	}
	
}
