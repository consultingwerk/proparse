block-level on error undo, throw.

@Test (some="text", display="test").
class test.SCL3722.test_code: 

	@Test (message="single attribute").
	method public void test_method_1 ():
	end method.
	
	@Test. // no attribute
	method public void test_method_2 ():
	end method.

end class.