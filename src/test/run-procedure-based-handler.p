BLOCK-LEVEL ON ERROR UNDO, THROW.

DEFINE VARIABLE hServer AS HANDLE NO-UNDO.
DEFINE VARIABLE o AS Progress.Lang.Object NO-UNDO .
define variable hProcedure as handle no-undo .

run test.p on hServer
	asynchronous 
	event-procedure "test" in hProcedure . 


