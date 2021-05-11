BLOCK-LEVEL ON ERROR UNDO, THROW.

DEFINE VARIABLE xx AS OpenEdge.Core.System.ErrorSeverityEnum.
DEFINE VARIABLE zz AS OpenEdge.Core.System.ErrorSeverityEnum.
xx = OpenEdge.Core.System.ErrorSeverityEnum:Critical.
zz = xx XOR OpenEdge.Core.System.ErrorSeverityEnum:Critical XOR OpenEdge.Core.System.ErrorSeverityEnum:MESSAGE.
