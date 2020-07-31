/*------------------------------------------------------------------------
    File        : TestSCL-3087
    Purpose     :
    Syntax      :
    Description :
    Author(s)   : SebastianWandel
    Created     : Wed July 29 16:01:07 CEST 2020
    Notes       :
  ----------------------------------------------------------------------*/

USING Progress.Lang.*.

BLOCK-LEVEL ON ERROR UNDO, THROW.

CLASS Test.SCL3087.TestSCL-3087:

	/* ‰¸ˆƒ‹÷ﬂ */
    DEFINE PACKAGE-PROTECTED PROPERTY TestProperty1 AS CHARACTER NO-UNDO
    GET.
    SET.

    DEFINE PACKAGE-PRIVATE PROPERTY TestProperty2 AS CHARACTER NO-UNDO
    GET.
    PACKAGE-PRIVATE SET.

    METHOD PACKAGE-PROTECTED VOID TestMehod1():

    END METHOD.

    METHOD PACKAGE-PRIVATE VOID TestMehod2():

    END METHOD.

END CLASS.
