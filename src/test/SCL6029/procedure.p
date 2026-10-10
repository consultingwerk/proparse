/* SCL-6029 fixture - parameters and return types in a procedure file. */

DEFINE INPUT  PARAMETER poInput  AS Pair<Alpha, Beta> NO-UNDO.
DEFINE OUTPUT PARAMETER poOutput AS Pair<Alpha, Beta> NO-UNDO.

DEFINE VARIABLE oPair AS Pair<Alpha,Beta> NO-UNDO.

FUNCTION GetPair RETURNS Pair<Alpha, Beta> (poPair AS Pair<Alpha, Beta>):
    RETURN poPair.
END FUNCTION.

PROCEDURE DoIt:
    DEFINE INPUT PARAMETER poPair AS Pair<Alpha, Beta> NO-UNDO.
END PROCEDURE.

poOutput = GetPair (poInput).
