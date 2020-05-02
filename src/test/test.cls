
CLASS test.test:

/*o =*/ 
	CONSTRUCTOR test ():
	
		DEFINE VARIABLE o AS Progress.Lang.Object NO-UNDO.
	
		assign o = active-form. 	


&IF DBTYPE("sports2000") EQ "PROGRESS" &THEN

MESSAGE 42 VIEW-AS ALERT-BOX . 

&ENDIF

	END CONSTRUCTOR.
END CLASS .
