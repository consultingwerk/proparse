package test.SCL4868;

import java.io.File;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

import junit.framework.Assert;

public class TestNewTriggerError 
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
	
	public void test_issue () throws RefactorException {
		File original;
		ParseUnit pu;
		
		original = new File ("src/test/SCL4868/proparse-issue.p");
		
		pu = new ParseUnit (original, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
	}
}
