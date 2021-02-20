/*------------------------------------------------------------------------
    File        : test-code.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : mikef
    Created     : Sat Feb 20 05:47:38 CET 2021
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

DEFINE TEMP-TABLE ttCustomer NO-UNDO LIKE Customer .

DEFINE BUFFER b_ttCustomer FOR TEMP-TABLE ttCustomer .

DEFINE TEMP-TABLE Salesrep NO-UNDO LIKE Salesrep .

DEFINE BUFFER b_Salesrep FOR TEMP-TABLE Salesrep .

FIND FIRST b_Salesrep.

DISPLAY b_Salesrep.RepName .
