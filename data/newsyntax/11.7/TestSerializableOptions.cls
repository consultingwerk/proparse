/*------------------------------------------------------------------------
    File        : TestSerializableOptions
    Purpose     :
    Syntax      :
    Description :
    Author(s)   : mikef
    Created     : Wed May 24 15:08:57 CEST 2017
    Notes       :
  ----------------------------------------------------------------------*/
 
USING Progress.Lang.*.
 
ROUTINE-LEVEL ON ERROR UNDO, THROW.
 
CLASS Test.TestSerializableOptions SERIALIZABLE:
 
    /**
     * Purpose:
     * Notes:
     *
     * @return CHARACTER
     */
    
    define public event testEvt signature void (amr as character).
     
    DEFINE PRIVATE SERIALIZABLE TEMP-TABLE ttTest NO-UNDO FIELD c AS CHARACTER .

    DEFINE PRIVATE NON-SERIALIZABLE TEMP-TABLE ttTest2 NO-UNDO FIELD c AS CHARACTER .
    
    DEFINE PRIVATE SERIALIZABLE DATASET dsTest SERIALIZE-NAME 'myData' FOR ttTest.
    
    DEFINE PRIVATE NON-SERIALIZABLE DATASET dsTest2 FOR ttTest2.
    
    DEFINE PUBLIC NON-SERIALIZABLE PROPERTY TestProperty AS CHARACTER NO-UNDO
    PUBLIC GET.
    PUBLIC SET.
 
    DEFINE PROTECTED SERIALIZABLE PROPERTY TestProperty2 AS CHARACTER NO-UNDO SERIALIZE-NAME "Bam":U
    PUBLIC GET.
    PUBLIC SET.

	DEFINE PUBLIC NON-SERIALIZABLE VARIABLE TestVariable AS CHARACTER NO-UNDO.
	
	DEFINE PRIVATE SERIALIZABLE VARIABLE TestVariable2 AS CHARACTER NO-UNDO SERIALIZE-NAME "BamBam":U. 
 
  METHOD PROTECTED VOID InternalAdd (poItem AS Progress.Lang.Object EXTENT):

        DEFINE VARIABLE i AS INTEGER NO-UNDO.

        DO i = 1 TO EXTENT (poItem):
            THIS-OBJECT:InternalAdd (poItem[i]) .
        END.

    END METHOD.
    
END CLASS.