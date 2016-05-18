# proparse

Proparse, forked from http://www.oehive.org/proparse/

The grammar files uses ANTLR 2x syntax (compiler in lib/antlr-2.7.7.jar), use the ANTLR plugin for Eclipse
from sourceforge - http://antlreclipse.sourceforge.net/updates the newest ANTLR IDE only works with ANTLR version 3+.

As far as I can tell there are two grammar files used by proparse 

	- com/joanju/proparse/proparse.g
	- com/joanju/proparse/proeval.g
	
Last one is only used if code chunk or preprocess evaluation is requested. 
	
However, prorefactor has a number of additional grammars all looking more or less same with some extensions to the proparse one	
	
	- org/prorefactor/treeparserbase/JPTreeParser.g 
	- org/prorefactor/treeparser01/JPTreeParser.g
	- org/prorefactor/treeparser01/expandedtreeparser01.g

Out of those only the first one seems to be used in unit test scripts.	

Adding new keyword usually require:
	1. add the keyword to com/joanju/proparse/BaseTokenTypes.txt (also increment Last_Token_Number)
	2. add the keyword in com/joanju/proparse/NodeTypes.java (there is a long list in the static constructor)
	3. depending on keyword type the grammar file might need to be updated
		- SYSTEM HANDLE (SYSHDL), add the keyword on 'systemhandlename' rule
		- FUNCTIONS (MAY_BE_REGULAR_FUNC or MAY_BE_NO_ARG_FUNC), add it to one of the following rules: 'builtinfunc', 'argfunc', 'recordfunc', 'noargfunc'

# Parsing Proparse from the ABL

Parsing rules form the ABL are derived from the original Prolint sources ... It's basically creating an instance of a java.io.File and passing it to the treeParser01 method of the org.prorefactor.treeparser.ParseUnit class.

So it looks, that the ABL uses the org/prorefactor/treeparser01/expandedtreeparser01.g grammar.

```
DEFINE VARIABLE javafile AS java.io.File                         NO-UNDO .
DEFINE VARIABLE pu       AS org.prorefactor.treeparser.ParseUnit NO-UNDO .

javafile = NEW java.io.File (pcFilename).

pu = NEW ParseUnit(javafile).
pu:treeParser01().

DELETE  OBJECT javafile .		
```