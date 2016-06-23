DEFINE VARIABLE code-page-list AS CHARACTER NO-UNDO.
DEFINE VARIABLE collation-list AS CHARACTER NO-UNDO.
DEFINE VARIABLE ix             AS INTEGER   NO-UNDO.
DEFINE VARIABLE jx             AS INTEGER   NO-UNDO. 
DEFINE VARIABLE lchar		   AS LONGCHAR  NO-UNDO.

MESSAGE GET-CODEPAGE(lchar).

code-page-list = GET-CODEPAGES. 
REPEAT ix = 1 TO NUM-ENTRIES(code-page-list):  
	DISPLAY ENTRY(ix, code-page-list) FORMAT "x(19)" 
		COLUMN-LABEL "Code Page"    WITH DOWN FRAME a.  
	
	collation-list = GET-COLLATIONS(ENTRY(ix, code-page-list)).
	
	REPEAT jx = 1 TO NUM-ENTRIES(collation-list):
		DISPLAY ENTRY(jx, collation-list) FORMAT "x(19)"       
			COLUMN-LABEL "Collation"      WITH DOWN FRAME a.    
			DOWN WITH FRAME a.  
	END.
END.