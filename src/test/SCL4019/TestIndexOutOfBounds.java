package test.SCL4019;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.Assert;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.ParseUnit;

import test.ProparseTestCase;

public class TestIndexOutOfBounds 
	extends ProparseTestCase {

	public void setUp () throws Exception {
		super.setUp();
	}
	
	@Override
	protected String getProversion ()
	{
		return "12.8";
	}
	
	@Override
	protected String getPropathFileName ()
	{
		return "src/test/propath_128.txt";
	}
	
	public void test_00 () throws RefactorException {
		File src;
		File expected;
		ParseUnit pu;

		src = new File ("src/test/SCL4019/TestCode/wWin.w");
		expected = new File ("src/test/SCL4019/ExpectedResults/test_00.txt");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());	
		this.compare(expected, pu.getTopNode().toStringSourceText());
	}
	
	public void test_01 () throws RefactorException {
		File src;
		File expected;
		ParseUnit pu;
		
		src = new File ("src/test/SCL4019/TestCode/testCode.p");
		expected = new File ("src/test/SCL4019/ExpectedResults/test_01.txt");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());	
		this.compare(expected, pu.getTopNode().toStringSourceText());
	}
	
	public void test_02 () throws RefactorException {
		File src;
		File expected;
		ParseUnit pu;
		
		src = new File ("src/test/SCL4019/TestCode/nestedInclude.p");
		expected = new File ("src/test/SCL4019/ExpectedResults/test_02.txt");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());	
		this.compare(expected, pu.getTopNode().toStringSourceText());
	}
	
	public void test_03 () throws RefactorException {
		File src;
		File expected;
		ParseUnit pu;
		
		src = new File ("src/test/SCL4019/TestCode/causeException.p");
		expected = new File ("src/test/SCL4019/ExpectedResults/test_03.txt");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		this.compare(expected, pu.getTopNode().toStringSourceText());
	}
	
	public void test_04 () throws RefactorException {
		File src;
		ParseUnit pu;
		
		src = new File ("src/test/SCL4019/TestCode/RenameMoveReference.txt");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		Assert.assertNotNull(pu.getTopNode().toStringSourceText());
		
		try (FileWriter fw = new FileWriter (new File ("C:\\Temp\\debug.txt")))
		{
			fw.write(pu.getTopNode().toStringSourceText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test_05 () throws RefactorException {
		File src;
		ParseUnit pu;
		
		src = new File ("src/test/SCL4019/TestCode/wWin2.w");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		
		try (FileWriter fw = new FileWriter (new File ("C:\\Temp\\debug.txt")))
		{
			fw.write(pu.getTopNode().toStringSourceText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test_06 () throws RefactorException {
		File src;
		ParseUnit pu;
		
		src = new File ("src/test/SCL4019/TestCode/c-customer.w");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		
		try (FileWriter fw = new FileWriter (new File ("C:\\Temp\\debug.txt")))
		{
			fw.write(pu.getTopNode().toStringSourceText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test_07 () throws RefactorException {
		File src;
		ParseUnit pu;
		
		src = new File ("C:\\Work_STREAM\\SmartComponentLibrary\\Develop128\\UnitTests\\Consultingwerk\\SimpleCustomerBusinessEntity\\CustomerBusinessEntity.cls");
		
		pu = new ParseUnit (src, "ISO8859-1");
		Assert.assertNotNull(pu);
		pu.treeParser01();
		Assert.assertNotNull(pu.getTopNode());
		
		try (FileWriter fw = new FileWriter (new File ("C:\\Temp\\debug.txt")))
		{
			fw.write(pu.getTopNode().toStringSourceText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void compare(File orig, String copy)
	{
		BufferedReader brOrig;
		BufferedReader brCopy;
		
		String lineOrig;
		String lineCopy;
		
		Assert.assertNotNull(orig);
		Assert.assertTrue(orig.canRead());
		Assert.assertNotNull(copy);
		
		int i = 0;
		
		try 
		{
			brOrig = new BufferedReader(new FileReader(orig));
			brCopy = new BufferedReader(new StringReader(copy));
			
			Assert.assertNotNull(brOrig);
			Assert.assertNotNull(brCopy);
			
			lineOrig = brOrig.readLine();
			lineCopy = brCopy.readLine();

			while(lineOrig != null && lineCopy != null)
			{
	
				Assert.assertEquals("Error in Line " + i , lineOrig, lineCopy);

				lineOrig = brOrig.readLine();
				lineCopy = brCopy.readLine();
				i++;
			}
			Assert.assertNull(lineOrig);
			Assert.assertNull(lineCopy);
		} 
		catch (IOException e) 
		{
			Assert.fail("File not found: " + e.getMessage());
		}
	}
}
