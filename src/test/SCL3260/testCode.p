
/*------------------------------------------------------------------------
    File        : testCode.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : Sebastian
    Created     : Tue Dec 15 09:46:10 CET 2020
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

/* ********************  Preprocessor Definitions  ******************** */


/* ***************************  Main Block  *************************** */

PROCEDURE SingleVariable:

	VAR CHAR cVar.
	
END PROCEDURE.

PROCEDURE MultiVariable:

	VAR INT iVar1, iVar2, iVar3.
	
END PROCEDURE.

PROCEDURE InitVariable:

	VAR CHAR cVar = "Test":U.
	
END PROCEDURE.

PROCEDURE UndefArray:

	VAR CHAR[] cVar.
	
END PROCEDURE.

PROCEDURE UndefMultiArray:

	VAR CHAR[] cVar1, cVar2, cVar3.

END PROCEDURE.

PROCEDURE DefArray:

	VAR INT[5] iVar.
	
END PROCEDURE.

PROCEDURE DefMultiArray:

	VAR INT[5] iVar1, iVar2, iVar3.

END PROCEDURE.

PROCEDURE InitArray:

	VAR INT[] iVar = [1, 2, 3].
	
END PROCEDURE.

PROCEDURE InitMultiArray:

	VAR INT[] iVar1, iVar2 = [1, 2], iVar3 = [1, 2, 3], iVar4.

END PROCEDURE.

PROCEDURE InitMultiVar:

	VAR CHAR cVar1, cVar2 = "Hello":U, cVar3 = "World":U, cVar4.

END PROCEDURE.

PROCEDURE DefVar:

	DEF VAR i AS INT NO-UNDO.

END PROCEDURE.

PROCEDURE SerializableVar:

	VAR SERIALIZABLE INT iVar.

END PROCEDURE.

PROCEDURE NonSerializable:

	VAR NON-SERIALIZABLE INT iVar.

END PROCEDURE.
