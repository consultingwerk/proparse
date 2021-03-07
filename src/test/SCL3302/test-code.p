DEFINE VARIABLE lTemp AS LOGICAL     NO-UNDO.
DEFINE VARIABLE iTemp AS INTEGER     NO-UNDO.
DEFINE VARIABLE iAscii AS INTEGER     NO-UNDO.
DEFINE VARIABLE icEmailAddress AS CHARACTER   NO-UNDO.

DO iTemp = 1 TO LENGTH (icEmailAddress):
    ASSIGN iAscii = ASC (SUBSTRING (icEmailaddress, iTemp, 1)) . 
    
    IF iAscii EQ 123 // {
    OR iAscii EQ 125 // }
    OR iAscii EQ 126 // ~
    THEN ASSIGN lTemp = TRUE . 
    ELSE ASSIGN lTemp = FALSE . 
END.
