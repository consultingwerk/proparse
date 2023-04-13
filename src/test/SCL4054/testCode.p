/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

&SCOPED-DEFINE Test 42

&SCOPED-DEFINE Test-42

&IF DEFINED (Test-42) NE 0 &THEN
MESSAGE "bam"
    VIEW-AS ALERT-BOX.
&ENDIF


&IF DEFINED (Test-{&Test}) NE 0 &THEN
MESSAGE "yeah"
    VIEW-AS ALERT-BOX.
&ENDIF

MESSAGE "boah"
    VIEW-AS ALERT-BOX.