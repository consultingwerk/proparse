/*------------------------------------------------------------------------
    File        : testCode.p
    Purpose     :

    Syntax      :

    Description :

    Author(s)   : Daniel
    Created     : Mon Oct 04 11:57:10 CET 2021
    Notes       :
  ----------------------------------------------------------------------*/

/* ***************************  Definitions  ************************** */

block-level on error undo, throw.

/* ********************  Preprocessor Definitions  ******************** */

/* preprocessor in middle and at the end of checked preprocessor variable*/
&if defined(i_tt{&prefix}companydata{&suffix}) = 0 &then 
&global-define i_tt{&prefix}companydata{&suffix} yes            
define {&scope} temp-table tt{&prefix}CompanyData{&suffix} no-undo {&reference-only} before-table bt{&prefix}CompanyData{&suffix}
  field Company as character /* bedrijf */.
&ENDIF


/* preprocessor at begining of checked preprocessor variable */
&IF DEFINED({&PREFIX}TTEqSearchModel-I) = 0 &THEN
&GLOBAL-DEFINE {&PREFIX}TTEqSearchModel-I 1

def {&SCOPE} temp-table {&PREFIX}ttEqSearchModel no-undo {&REFERENCE-ONLY} 
  field SortOrder as int.

&ENDIF

/* ***************************  Main Block  *************************** */
&SCOP QUERY-NAME Query-Main
&Scoped-define OPEN-QUERY-Query-Main OPEN QUERY Query-Main FOR EACH baktype NO-LOCK INDEXED-REPOSITION.  

&IF DEFINED(OPEN-QUERY-{&QUERY-NAME}) NE 0 &THEN
  define variable hQuery                  as handle     no-undo.
  define variable hBuffer                 as handle     no-undo.
&ENDIF