DEFINE VARIABLE vtenant-name AS CHARACTER NO-UNDO.
DEFINE VARIABLE vtenant-id 	AS CHARACTER NO-UNDO.
DEFINE VARIABLE vtenant-hdl AS HANDLE    NO-UNDO.

define temp-table customer field test as character.

DEFINE QUERY qryCust FOR customer.

reposition qryCust to rowid 0 for tenant 'walle'.

CREATE CUSTOMER FOR TENANT 'walle' NO-ERROR.

for each customer tenant-where tenant-name() = 'walle'.
for each customer tenant-where 0 skip-group-duplicates.

for each customer where true = false tenant-where 0.
for each customer where true = false tenant-where 0 skip-group-duplicates.

for each customer tenant-where 0 where true = false.
for each customer tenant-where 0 skip-group-duplicates where true = false.


current-value(nextCust, sports, 0) = 10.
dynamic-current-value('nextCust', 'sports', 0).

next-value(nextCust, sports, 0).
dynamic-next-value('nextCust', 'sports', 0).

IS-DB-MULTI-TENANT() .
IS-DB-MULTI-TENANT('sports') .
BUFFER-GROUP-ID(sports).
BUFFER-GROUP-NAME(sports).
BUFFER-TENANT-ID(sports).
BUFFER-TENANT-NAME(sports).
BUFFER-PARTITION-ID(sports).

// no arguments function
MESSAGE  IS-DB-MULTI-TENANT('sports') SKIP TENANT-ID() SKIP TENANT-NAME() SKIP 
	vtenant-hdl:TENANT-ID() skip vtenant-hdl:TENANT-NAME() SKIP 
	vtenant-hdl:BUFFER-TENANT-ID() skip vtenant-hdl:BUFFER-TENANT-NAME().

vtenant-name = TENANT-NAME('sports').
vtenant-id = TENANT-ID('sports').

SET-EFFECTIVE-TENANT(1, 'sports').
SET-EFFECTIVE-TENANT('walle').
	
message GET-EFFECTIVE-TENANT-NAME() skip GET-EFFECTIVE-TENANT-NAME('sports') skip 
GET-EFFECTIVE-TENANT-ID() skip GET-EFFECTIVE-TENANT-ID('sports') view-as alert-box.	
 
IF TENANT-NAME-TO-ID(vtenant-name) = vtenant-id THEN
	MESSAGE 'OK'.
	
IF TENANT-NAME-TO-ID(vtenant-name, 'sports') = vtenant-id THEN
	MESSAGE 'OK'.