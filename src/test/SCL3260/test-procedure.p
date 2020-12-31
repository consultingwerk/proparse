/*------------------------------------------------------------------------
    File        : test-procedure.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : mikef
    Created     : Thu Dec 31 08:27:13 CET 2020
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

PROCEDURE test-internal:

    DEFINE VARIABLE i AS INTEGER NO-UNDO .
    
    VAR INTEGER n, m .

    ASSIGN i = n .

END PROCEDURE .
