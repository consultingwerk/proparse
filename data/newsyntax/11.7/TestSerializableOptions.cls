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
    DEFINE PUBLIC NON-SERIALIZABLE PROPERTY TestProperty AS CHARACTER NO-UNDO
    PUBLIC GET.
    PUBLIC SET.
 
 
    DEFINE PUBLIC SERIALIZABLE PROPERTY TestProperty2 AS CHARACTER NO-UNDO SERIALIZE-NAME "Bam":U
    PUBLIC GET.
    PUBLIC SET.
 
 
END CLASS.