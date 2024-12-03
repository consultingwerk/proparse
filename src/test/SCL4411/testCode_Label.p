
define temp-table ttTest no-undo
	field TestField as character.
	
TestLabel:
for each ttTest on error undo, throw:
end.