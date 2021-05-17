
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

USING Consultingwerk.Framework.Collections.* FROM PROPATH.

/* ********************  Preprocessor Definitions  ******************** */


/* ***************************  Main Block  *************************** */

DEFINE VARIABLE oList AS CharacterList NO-UNDO .


{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\incEmpty.i}
MESSAGE "Some Code":U
    VIEW-AS ALERT-BOX.


{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\inc1.i &GREETING="'Hello'" &NAME="'World'"}
 
{C:\Work\Proparse\GitHub\proparse\src\test\SCL3366\inc2.i "'Hello'" "'World'"}
 

MESSAGE "Some more code":U
    VIEW-AS ALERT-BOX.



MESSAGE "TRUE":U
    VIEW-AS ALERT-BOX.



MESSAGE "Even more code":U
    VIEW-AS ALERT-BOX.

