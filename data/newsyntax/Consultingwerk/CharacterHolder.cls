ROUTINE-LEVEL ON ERROR UNDO, THROW.

USING Consultingwerk.* FROM PROPATH .
USING Progress.Lang.*  FROM PROPATH .

CLASS Consultingwerk.CharacterHolder:

    {Consultingwerk/JsonSerializableProperty.i Value CHARACTER} .

END CLASS.