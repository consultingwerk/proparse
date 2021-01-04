/*------------------------------------------------------------------------
    File        : testCode.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : Sebastian
    Created     : Mon Jan 04 11:57:10 CET 2021
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

/* ********************  Preprocessor Definitions  ******************** */


/* ***************************  Main Block  *************************** */

PROCEDURE IntegerAddEq:

	DEFINE VARIABLE i AS INTEGER NO-UNDO INITIAL 10 .

	i += 4 .
	
	MESSAGE i
	    VIEW-AS ALERT-BOX.

END PROCEDURE.

PROCEDURE IntegerSubEq:

	DEFINE VARIABLE i AS INTEGER NO-UNDO INITIAL 10 .

	i -= 4 .
	
	MESSAGE i
	    VIEW-AS ALERT-BOX.

END PROCEDURE.

PROCEDURE IntegerMulEq:

	DEFINE VARIABLE i AS INTEGER NO-UNDO INITIAL 10 .

	i *= 10 .
	
	MESSAGE i
	    VIEW-AS ALERT-BOX.

END PROCEDURE.

PROCEDURE IntegerDivEq:

	DEFINE VARIABLE i AS INTEGER NO-UNDO INITIAL 10 .

	i /= 10 .
	
	MESSAGE i
	    VIEW-AS ALERT-BOX.

END PROCEDURE.

PROCEDURE CharacterAddEq:

	DEFINE VARIABLE c AS CHARACTER NO-UNDO INITIAL "abc" .
	
	c += "def" .
	
	MESSAGE c
	    VIEW-AS ALERT-BOX.
    
END PROCEDURE.
    
PROCEDURE DateAddEq:

	DEFINE VARIABLE dt AS DATE NO-UNDO INITIAL TODAY .
	
	dt += 2 .
	
	MESSAGE dt
	    VIEW-AS ALERT-BOX.

END PROCEDURE.

PROCEDURE DateSubEq:

	DEFINE VARIABLE dt AS DATE NO-UNDO INITIAL TODAY .

	dt -= 3 .
	
	MESSAGE dt
	    VIEW-AS ALERT-BOX.
	 
END PROCEDURE.	

PROCEDURE AssignState:

	DEFINE VARIABLE i1 AS INTEGER NO-UNDO INITIAL 1.
	DEFINE VARIABLE i2 AS INTEGER NO-UNDO INITIAL 2.
	DEFINE VARIABLE i3 AS INTEGER NO-UNDO INITIAL 3.
	DEFINE VARIABLE i4 AS INTEGER NO-UNDO INITIAL 4.
	
	ASSIGN 
		i1 += 1
		i2 -= 1
		i3 *= i1
		i4 /= i2
		.

END PROCEDURE. 