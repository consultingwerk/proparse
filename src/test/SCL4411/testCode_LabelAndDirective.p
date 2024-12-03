
define temp-table ttTest no-undo
	field TestField as character.
	
TestLabel:
{&_proparse_ prolint-nowarn(sortaccess-wholeindex)}
for each ttTest on error undo, throw:
end.