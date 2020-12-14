package test;

import java.io.*;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.prorefactor.core.JPNode;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.*;

import com.joanju.proparse.NodeTypes;

import junit.framework.TestCase;

public class UnitTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
/*
	public void testParseUnit() {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\test.cls");
	
		ParseUnit pu = new ParseUnit(file);
	
		try {
			pu.treeParser01();
		} catch (RefactorException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
*/
	public void testParseUnit2() {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\TestClass122.cls");
	
		ParseUnit pu = new ParseUnit(file);
	
		try {
			pu.treeParser01();
		} catch (RefactorException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/* SCL-3087 */
	public void testParseUnit3() {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\Test_SCL-3087.cls");

		
		try {
			ParseUnit pu = new ParseUnit(file, "ISO8859_1");
			
			try {
				
				pu.treeParser01();
				
			} catch (RefactorException e) {
				System.out.println(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/* SCL-3087 */
	public void testIso8859_1() {
		java.io.File file = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL\\Consultingwerk\\SmartComponentsDemo\\OERA\\Sports2000\\CountryDataAccess.cls");

		com.joanju.proparse.Environment proparseEnv = null;		
		org.prorefactor.core.schema.Schema proparseSchema = null;		
		
		try {
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\schema.txt");
			
			proparseEnv.configSet ("propath", "C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL");			

			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", "12.2");
			proparseEnv.configSet ("window-system", "MS-WINXP");						
			
			ParseUnit pu = new ParseUnit(file, "ISO8859-1");
			
			try {
				
				pu.treeParser01();
				
			} catch (RefactorException e) {
				System.out.println(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}	

	/* SCL-3087 */
	public void testUtf_8() {
		java.io.File file = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL\\Consultingwerk\\SmartComponentsDemo\\OERA\\Sports2000\\CountryDataAccess.cls");

		com.joanju.proparse.Environment proparseEnv = null;
		org.prorefactor.core.schema.Schema proparseSchema = null;		
		
		try {
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\schema.txt");
			
			proparseEnv.configSet ("propath", "C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL");			
			
			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", "12.2");
			proparseEnv.configSet ("window-system", "MS-WINXP");						
			
			ParseUnit pu = new ParseUnit(file, "utf-8");
			
			try {
				
				pu.treeParser01();
				
			} catch (RefactorException e) {
				System.out.println(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}	

	/* SCL-3087 */
	public void test_pu_IllegalCharsetName() {
		java.io.File file = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL\\Consultingwerk\\SmartComponentsDemo\\OERA\\Sports2000\\CountryDataAccess.cls");

		com.joanju.proparse.Environment proparseEnv = null;
		org.prorefactor.core.schema.Schema proparseSchema = null;		
		
		try {
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\schema.txt");
			
			proparseEnv.configSet ("propath", "C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL");			
			
			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", "12.2");
			proparseEnv.configSet ("window-system", "MS-WINXP");						

			try {
				
				ParseUnit pu = new ParseUnit(file, "iso8859;1");
				pu.treeParser01();
				
			} catch (IllegalCharsetNameException e) {
				System.out.println("\nExpected Exception!\n");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			} catch (Exception e) {
				System.out.println("\nUnexpected Exception!\n");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/* SCL-3087 */
	public void test_pu_UnsupportedCharset() {
		java.io.File file = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL\\Consultingwerk\\SmartComponentsDemo\\OERA\\Sports2000\\CountryDataAccess.cls");

		com.joanju.proparse.Environment proparseEnv = null;
		org.prorefactor.core.schema.Schema proparseSchema = null;		
		
		try {
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\schema.txt");
			
			proparseEnv.configSet ("propath", "C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL");			
			
			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", "12.2");
			proparseEnv.configSet ("window-system", "MS-WINXP");						

			try {
				
				ParseUnit pu = new ParseUnit(file, "xxxiso8859-1");
				pu.treeParser01();
				
			} catch (UnsupportedCharsetException e) {
				System.out.println("\nExpected Exception!\n");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			} catch (Exception e) {
				System.out.println("\nUnexpected Exception!\n");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/* SCL-3087 */
	public void test_pu_IllegalArgument() {
		java.io.File file = new File("C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL\\Consultingwerk\\SmartComponentsDemo\\OERA\\Sports2000\\CountryDataAccess.cls");

		com.joanju.proparse.Environment proparseEnv = null;
		org.prorefactor.core.schema.Schema proparseSchema = null;		
		
		try {
			proparseEnv = com.joanju.proparse.Environment.instance();
			proparseSchema = org.prorefactor.core.schema.Schema.getInstance();			
			
			proparseSchema.clear();
			proparseSchema.loadSchema("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\schema.txt");
			
			proparseEnv.configSet ("propath", "C:\\Work_STREAM\\SmartComponentLibrary\\Develop122\\ABL");			
			
			proparseEnv.configSet ("batch-mode", "false");
			proparseEnv.configSet ("opsys", "WIN32");
			proparseEnv.configSet ("proversion", "12.2");
			proparseEnv.configSet ("window-system", "MS-WINXP");						

			try {
				
				ParseUnit pu = new ParseUnit(file, null);
				pu.treeParser01();
				
			} catch (IllegalArgumentException e) {
				System.out.println("\nExpected Exception!\n");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			} catch (Exception e) {
				System.out.println("\nUnexpected Exception!\n");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void test_run_procedurebased_event_handler() {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\run-procedure-based-handler.p");
	
		ParseUnit pu = new ParseUnit(file);
		
		// assert 
		// find RUN statement
		// test for EVENT-HANDLER and EVENT-HANDLER-CONTEXT as children
						
		try {
			pu.treeParser01();
			
			JPNode top = pu.getTopNode();
			JPNode run = top.findDirectChild(NodeTypes.RUN);															
			JPNode async = run.findDirectChild(NodeTypes.ASYNCHRONOUS);
			
			assertNotNull(async.findDirectChild(NodeTypes.EVENTPROCEDURE));
			assertNotNull(async.findDirectChild(NodeTypes.IN_KW));						
			
		} catch (RefactorException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}
	
	public void test_run_classbased_event_handler() {
		java.io.File file = new File("C:\\Work\\Proparse\\Github\\proparse\\src\\test\\run-class-based-handler.p");
	
		ParseUnit pu = new ParseUnit(file);
		
		// assert 
		// find RUN statement
		// test for EVENT-HANDLER and EVENT-HANDLER-CONTEXT as children
						
		try {
			pu.treeParser01();
			
			JPNode top = pu.getTopNode();
			JPNode run = top.findDirectChild(NodeTypes.RUN);															
			assertNotNull(run.findDirectChild(NodeTypes.ASYNCHRONOUS));
			
			assertNotNull(run.findDirectChild(NodeTypes.EVENT_HANDLER));
			assertNotNull(run.findDirectChild(NodeTypes.EVENT_HANDLER_CONTEXT));						
			
		} catch (RefactorException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}
	
	
}
