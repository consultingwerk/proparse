/* SCL-6029 fixture - a bound generic type with more than one type argument
   in every position of a class file where a type name can occur. The
   comparisons in the method keep the expression use of < and > covered. */

USING SCL6029.Types.* FROM PROPATH.

CLASS SCL6029.Positions INHERITS Base<Alpha, Beta> IMPLEMENTS IFirst<Alpha,Beta>, ISecond<Gamma>:

    DEFINE VARIABLE oPair   AS Pair<Alpha, Beta>                 NO-UNDO.
    DEFINE VARIABLE oNested AS Outer<Alpha, Inner<Beta, Gamma>>  NO-UNDO.
    DEFINE VARIABLE oSingle AS Single<Alpha>                     NO-UNDO.
    DEFINE VARIABLE oPlain  AS Alpha                             NO-UNDO.
    DEFINE VARIABLE oArray  AS System.Collections.Generic.List<Alpha, Beta>[] NO-UNDO.
    DEFINE VARIABLE i       AS INTEGER                           NO-UNDO.

    DEFINE PUBLIC PROPERTY Prop AS Pair<Alpha, Beta> NO-UNDO
    GET.
    SET.

    DEFINE PUBLIC EVENT Changed SIGNATURE VOID (poSender AS Pair<Alpha, Beta>).

    CONSTRUCTOR PUBLIC Positions (poPair AS Pair<Alpha, Beta>):
        SUPER ().
    END CONSTRUCTOR.

    METHOD PUBLIC Pair<Alpha, Beta> Create (poInput AS Pair<Alpha,Beta>, poOther AS Alpha):

        DEFINE VARIABLE oResult AS Pair<Alpha, Beta> NO-UNDO.

        oResult = NEW Pair<Alpha, Beta> (poOther, poOther).
        oResult = CAST (poInput, Pair<Alpha, Beta>).

        IF TYPE-OF (poInput, Pair<Alpha, Beta>) AND i < 2 THEN
            i = i + 1.

        IF i > 1 THEN
            RETURN oResult.

        MESSAGE GET-CLASS (Pair<Alpha, Beta>):TypeName.

        DYNAMIC-INVOKE (Pair<Alpha, Beta>, "StaticMethod":U).

        RETURN oResult.

    END METHOD.

END CLASS.
