&SCOPED-DEFINE WAHR true
&SCOPED-DEFINE FALSCH false
&GLOBAL-DEFINE TRAN :U

{src/test/SCL4019/TestCode/multiLine.i}
{src/test/SCL4019/TestCode/multiLine.i}
{src/test/SCL4019/TestCode/multiLine.i}

{src/test/SCL4019/TestCode/nestedInclude.i}
{src/test/SCL4019/TestCode/nestedInclude.i}
{src/test/SCL4019/TestCode/nestedInclude.i}
{src/test/SCL4019/TestCode/nestedInclude.i}

if {&WAHR} then 
	{src/test/SCL4019/TestCode/noText.i}
	message "Some text here"{&TRAN}
		view-as alert-box.
else if {&FALSCH} then
	{src/test/SCL4019/TestCode/noText.i}
	message "Some more text here"{&TRAN}
		view-as alert-box.
else
	{src/test/SCL4019/TestCode/noText.i}
	message "No more text"{&TRAN}
		view-as alert-box.

{src/test/SCL4019/TestCode/singleLine.i}