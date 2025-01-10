/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

&SCOPED-DEFINE Test 42

&SCOPED-DEFINE Test-42

&IF DEFINED (Test-42) NE 0 &THEN
MESSAGE "A"
    VIEW-AS ALERT-BOX.
&ENDIF


&IF DEFINED (Test-{&Test}) NE 0 &THEN
MESSAGE "B"
    VIEW-AS ALERT-BOX.
&ENDIF

MESSAGE "C"
    VIEW-AS ALERT-BOX.