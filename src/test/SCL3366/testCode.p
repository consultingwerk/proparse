
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

&SCOPED-DEFINE HELLOWORLD MESSAGE "Hello World":U VIEW-AS ALERT-BOX.

&GLOBAL-DEFINE MACRO_A "Text1":U
&GLOBAL-DEFINE MACRO_B "Text2":U {&MACRO_A}

@Description (description="Test code") .

/* ***************************  Main Block  *************************** */

DEFINE VARIABLE oList AS CharacterList NO-UNDO .
DEFINE VARIABLE oConfig AS Consultingwerk.Framework.IConfigurationProvider NO-UNDO.
DEFINE VARIABLE cText AS CHARACTER NO-UNDO INITIAL "Hello ":U.

oConfig = {Consultingwerk/get-service.i Consultingwerk.Framework.IConfigurationProvider "NEW Consultingwerk.Framework.ConfigurationProvider ('.applicationsettings':U)"}.

ASSIGN cText = cText + "World".

{Consultingwerk/foreachPrimitiveList.i Character c in oList} MESSAGE c . END.

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
	
{&HELLOWORLD}	
	
MESSAGE {&GLOBAL_MAKRO} {&SCOPED_MAKRO}
	VIEW-AS ALERT-BOX.
{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\incEmpty.i}
