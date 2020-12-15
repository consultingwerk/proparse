BLOCK-LEVEL ON ERROR UNDO, THROW.

DEFINE VARIABLE hServer AS HANDLE NO-UNDO.
DEFINE VARIABLE o AS Progress.Lang.Object NO-UNDO .
define variable hProcedure as handle no-undo .


RUN test.p
    ON hServer
    ASYNCHRONOUS
    EVENT-HANDLER "test"
    EVENT-HANDLER-CONTEXT o .
