block-level on error undo, throw.

@openapi.openedge.export FILE(type="REST", executionMode="singleton", useReturnValue="false", writeDataSetBeforeImage="false", display="test").
class test.SCL3722.test_code: 

	@Test (message="single attribute").
	method public void test_method_1 ():
	end method.
	
	@Test. // no attribute
	method public void test_method_2 ():
	end method.

	@Test.testy.test.
	method public void test_method_3 ():
	end method.
	
	@Test.testy.test (att1="value1", att2="value.value2").
	method public void test_method_4 ():
	end method.

	@Test (stringattribute="attribute", integerattribute=12345).
	method public void test_method_5 ():
	end method.

end class.