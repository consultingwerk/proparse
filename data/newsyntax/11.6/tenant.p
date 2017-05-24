DEFINE VARIABLE vtenant-name AS CHARACTER NO-UNDO.
DEFINE VARIABLE vtenant-id 	AS CHARACTER NO-UNDO.

// no arguments function
MESSAGE  TENANT-ID() SKIP TENANT-NAME().

vtenant-name = TENANT-NAME('sports').
vtenant-id = TENANT-ID('sports').
	
message GET-EFFECTIVE-TENANT-NAME() skip GET-EFFECTIVE-TENANT-NAME('sports') skip 
GET-EFFECTIVE-TENANT-ID() skip GET-EFFECTIVE-TENANT-ID('sports') view-as alert-box.	
 
IF TENANT-NAME-TO-ID(vtenant-name) = vtenant-id THEN
	MESSAGE 'OK'.
	
IF TENANT-NAME-TO-ID(vtenant-name, 'sports') = vtenant-id THEN
	MESSAGE 'OK'.