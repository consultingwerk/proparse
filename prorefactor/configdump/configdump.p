/* configdump.p

Dumps the PROGRESS environment settings to a text file.

Input parameter is "outfile". Input ? to just use
"progress.properties" in your working directory.

*/

DEFINE INPUT PARAMETER outFile AS CHARACTER NO-UNDO.

DEFINE VARIABLE thePropath AS CHARACTER NO-UNDO.
DEFINE VARIABLE i          AS INTEGER   NO-UNDO.

IF outFile = ? THEN ASSIGN outFile = "progress.properties":U.

IF SEARCH(outFile) <> ? THEN DO:
  DEFINE VARIABLE isOk AS LOGICAL INITIAL NO.
  MESSAGE outFile "already exists. Overwrite?"
    VIEW-AS ALERT-BOX BUTTONS YES-NO UPDATE isOk.
  IF NOT isOk THEN RETURN.
END.

OUTPUT TO VALUE (outFile).

IF SESSION:BATCH-MODE THEN
  PUT UNFORMATTED "batch_mode=true":U SKIP.
ELSE
  PUT UNFORMATTED "batch_mode=false":U SKIP.

PUT UNFORMATTED "opsys=":U + OPSYS SKIP.
PUT UNFORMATTED "proversion=":U + PROVERSION SKIP.
PUT UNFORMATTED "window_system=":U + SESSION:WINDOW-SYSTEM SKIP.

/* In the properties file, colons and backslashes are escaped. */
ASSIGN thePropath = PROPATH.
ASSIGN thePropath = REPLACE(thePropath, "~\":U, "~\~\":U).
ASSIGN thePropath = REPLACE(thePropath, ":":U, "~\:":U).
PUT UNFORMATTED "propath=":U + thePropath SKIP.

PUT UNFORMATTED "database_aliases=":U.
DEF VAR separator AS CHAR NO-UNDO.
REPEAT i=1 TO NUM-ALIASES:
  IF ALIAS(i) = "DICTDB":U THEN NEXT.
  PUT UNFORMATTED separator.
  PUT UNFORMATTED ALIAS(i) + ",":U + LDBNAME(ALIAS(i)).
  separator = ",".
END.
PUT UNFORMATTED SKIP.


OUTPUT CLOSE.

