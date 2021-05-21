
/*------------------------------------------------------------------------
    File        : testCode.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : Sebastian
    Created     : Tue May 11 16:10:50 CEST 2021
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

BLOCK-LEVEL ON ERROR UNDO, THROW.

{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\include.i}

/* ********************  Preprocessor Definitions  ******************** */

&GLOBAL-DEFINE GLOBAL_MAKRO "GlobalMakro":U
&SCOPED-DEFINE SCOPED_MAKRO "ScopedMakro":U

/* ***************************  Main Block  *************************** */

MESSAGE "Test":U
	VIEW-AS ALERT-BOX.

{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\include.i}

MESSAGE "Test":U
	VIEW-AS ALERT-BOX.

{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\inc1.i &GREETING="'Hello'" &NAME="'World'"}
/*
&IF TRUE &THEN
MESSAGE "Some more code":U
    VIEW-AS ALERT-BOX.
&ENDIF
*/
{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\inc2.i "'Hello'" "'World'"}

MESSAGE "Even more code":U
    VIEW-AS ALERT-BOX.
    
MESSAGE {&GLOBAL_MAKRO} {&SCOPED_MAKRO}
	VIEW-AS ALERT-BOX.
{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\incEmpty.i}
