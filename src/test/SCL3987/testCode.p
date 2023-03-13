/*------------------------------------------------------------------------
    File        : testCode.p
    Purpose     : TestCode for operator-node-order when writing to file
    Syntax      :
    Description :
    Author(s)   : Sebastian
    Created     : Mon Mar 13 10:48:45 CET 2023
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

DEFINE VARIABLE oFlags AS Progress.Reflect.Flags NO-UNDO.
DEFINE VARIABLE iNum   AS INTEGER                NO-UNDO.

/* ***************************  Main Block  *************************** */

// XOR
oFlags = Progress.Reflect.Flags:DeclaredOnly XOR Progress.Reflect.Flags:Instance.

// OR
IF TRUE OR FALSE THEN
    RETURN.

// AND
IF TRUE AND FALSE THEN
    RETURN.

// NOT
IF NOT FALSE THEN
    RETURN.

// MATCHES
IF "Hello World" MATCHES "Hello *" THEN
    RETURN.

// BEGINS
IF "Hello World" BEGINS "Hello" THEN
    RETURN.

// CONTAINS
FOR EACH Customer WHERE Customer.Comments CONTAINS "CREDIT HOLD":
END.

// EQ
IF TRUE EQ FALSE THEN
    RETURN.

IF TRUE = FALSE THEN
    RETURN.

// NE
IF TRUE NE FALSE THEN
    RETURN.

IF TRUE <> FALSE THEN
    RETURN.

// GT
IF 4711 GT 42 THEN
    RETURN.

IF 4711 > 42 THEN
    RETURN.

// GE
IF 4711 GE 42 THEN
    RETURN.

IF 4711 >= 42 THEN
    RETURN.

// LT
IF 4711 LT 42 THEN
    RETURN.

IF 4711 < 42 THEN
    RETURN.

// LE
IF 4711 LE 42 THEN
    RETURN.

IF 4711 <= 42 THEN
    RETURN.

// PLUS
iNum = 4711 + 42.

// MINUS
iNum = 4711 - 42.

// MULTIPLY
iNum = 4711 * 42.

// DIVIDE
iNum = 4711 / 42.

// MODULO
iNum = 4711 MODULO 42.

// UNARY_MINUS
iNum = - 42.

// UNARY_PLUS
iNum = + 4711.

// PLUS_EQUAL
iNum += 42.

// MINUS_EQUAL
iNum -= 42.

// MULTIPLY_EQUAL
iNum *= 42.

// DIVIDE_EQUAL
iNum /= 42.

{Consultingwerk/Windows/ui-catch.i}
