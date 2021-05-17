
/*------------------------------------------------------------------------
    File        : test.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : Sebastian
    Created     : Tue May 11 16:32:56 CEST 2021
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

USING Consultingwerk.Studio.LegacyGuiMigration.* FROM PROPATH.
USING Consultingwerk.Util.* FROM PROPATH.
USING Consultingwerk.Studio.Proparse.* FROM PROPATH.
USING Test.SCL3366.* FROM PROPATH.
USING org.prorefactor.core.* FROM ASSEMBLY.
USING org.prorefactor.treeparser.* FROM ASSEMBLY.
USING com.joanju.proparse.* FROM ASSEMBLY.

BLOCK-LEVEL ON ERROR UNDO, THROW.

/* ********************  Preprocessor Definitions  ******************** */


/* ***************************  Main Block  *************************** */

DEFINE VARIABLE oTextTest AS NodeFullTextTest NO-UNDO.

SESSION:ERROR-STACK-TRACE = TRUE.

oTextTest = NEW NodeFullTextTest().

OUTPUT TO /Temp/fullTextTestDebug.txt.
MESSAGE NOW SKIP(2).
OUTPUT CLOSE.

RUN PrintAll(FileHelper:FindFile("Test/SCL3366/testCode.p":U), "/Temp/fullTextTest.txt":U).

{Consultingwerk/Windows/ui-catch.i}

PROCEDURE PrintAll:

    DEFINE INPUT PARAMETER pcSourceFile AS CHARACTER NO-UNDO.
    DEFINE INPUT PARAMETER pcTargetFile AS CHARACTER NO-UNDO.

    DEFINE VARIABLE oParseUnit AS ParseUnit NO-UNDO.
    DEFINE VARIABLE oNode      AS JPNode    NO-UNDO.

    oParseUnit = ProparseHelper:ParseFile(pcSourceFile).
DEFINE VARIABLE lcCode AS LONGCHAR NO-UNDO.
lcCode = oParseUnit:getTopNode():toStringSourceText().
COPY-LOB FROM lcCode TO FILE FileHelper:FindFile("Test/SCL3366/testCode_copy.p":U).
RETURN.
    oNode = oParseUnit:getTopNode():findDirectChild(NodeTypes:getTypeNum("MESSAGE":U)).
    LegacyGuiMigrationHelper:SetTokenText(oNode, "NACHRICHT":U).

    OUTPUT TO VALUE(pcTargetFile).
    MESSAGE "":U.
    OUTPUT CLOSE.

    RUN Print (oParseUnit:getTopNode(), pcTargetFile).

END PROCEDURE.

PROCEDURE Print:

    DEFINE INPUT PARAMETER poNode       AS JPNode    NO-UNDO.
    DEFINE INPUT PARAMETER pcTargetFile AS CHARACTER NO-UNDO.

    DEFINE VARIABLE lcText AS LONGCHAR NO-UNDO.

    lcText = oTextTest:GetNodeFullText(poNode).

    IF lcText > "":U THEN
        COPY-LOB FROM lcText TO FILE pcTargetFile APPEND.

    IF VALID-OBJECT(poNode:firstChild()) THEN
        RUN Print(poNode:firstChild(), pcTargetFile).
    IF VALID-OBJECT(poNode:nextSibling()) THEN
        RUN Print(poNode:nextSibling(), pcTargetFile).

END PROCEDURE.

