/* SCL-6310 fixture - several type parameters, spread over lines with
   blanks around the brackets and commas, and a blank in front of "<". */

CLASS SCL6310.GenericMulti
    < K AS Progress.Lang.Object ,
      V   AS   Progress.Lang.Object,W AS Progress.Lang.Object >
    FINAL SERIALIZABLE:

    DEFINE PRIVATE VARIABLE oKey   AS K NO-UNDO.
    DEFINE PRIVATE VARIABLE oValue AS V NO-UNDO.

END CLASS.
