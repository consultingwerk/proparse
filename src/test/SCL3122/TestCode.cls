
 /*------------------------------------------------------------------------
    File        : TestCode
    Purpose     :
    Syntax      :
    Description :
    Author(s)   : sebastian
    Created     : Thu Sep 10 09:55:00 CEST 2020
    Notes       :
  ----------------------------------------------------------------------*/

USING Progress.Lang.*.

BLOCK-LEVEL ON ERROR UNDO, THROW.

CLASS Consultingwerk.StudioTest.SCL3122.TestCode:

    METHOD PUBLIC VOID MethodOne():

        DEFINE VARIABLE hSetOnce  AS HANDLE    NO-UNDO.
        DEFINE VARIABLE cText     AS CHARACTER NO-UNDO.
        DEFINE VARIABLE hSetTwice AS HANDLE    NO-UNDO.

        RUN TestProcedure.p PERSISTENT SET hSetOnce.

        RUN InternalProcedure IN hSetOnce.

        cText = "Hello World!":U.

        MESSAGE cText
            VIEW-AS ALERT-BOX.

        RUN TestProcedure1.p PERSISTENT SET hSetTwice.

        RUN InternalProcedure IN hSetTwice.

        RUN TestProcedure2.p PERSISTENT SET hSetTwice.

        RUN InternalProcedure IN hSetTwice.

    END METHOD.

END CLASS.