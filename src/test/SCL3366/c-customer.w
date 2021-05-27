&ANALYZE-SUSPEND _VERSION-NUMBER AB_v10r12 GUI
&ANALYZE-RESUME
/* Connected Databases
          sports2000       PROGRESS
*/
&Scoped-define WINDOW-NAME C-Win
&ANALYZE-SUSPEND _UIB-CODE-BLOCK _CUSTOM _DEFINITIONS C-Win
/*------------------------------------------------------------------------

  File:

  Description:

  Input Parameters:
      <none>

  Output Parameters:
      <none>

  Author:

  Created:

------------------------------------------------------------------------*/
/*          This .W file was created with the Progress AppBuilder.      */
/*----------------------------------------------------------------------*/

/* Create an unnamed pool to store all the widgets created
     by this procedure. This is a good default which assures
     that this procedure's triggers and internal procedures
     will execute in this procedure's storage, and that proper
     cleanup will occur on deletion of the procedure. */

CREATE WIDGET-POOL.

/* ***************************  Definitions  ************************** */

/* Parameters Definitions ---                                           */

/* Local Variable Definitions ---                                       */

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME


&ANALYZE-SUSPEND _UIB-PREPROCESSOR-BLOCK

/* ********************  Preprocessor Definitions  ******************** */

&Scoped-define PROCEDURE-TYPE Window
&Scoped-define DB-AWARE no

/* Name of designated FRAME-NAME and/or first browse and/or first query */
&Scoped-define FRAME-NAME DEFAULT-FRAME
&Scoped-define BROWSE-NAME BROWSE-2

/* Internal Tables (found by Frame, Query & Browse Queries)             */
&Scoped-define INTERNAL-TABLES Customer

/* Definitions for BROWSE BROWSE-2                                      */
&Scoped-define FIELDS-IN-QUERY-BROWSE-2 Customer.CustNum Customer.Name Customer.Address Customer.Address2 Customer.City Customer.PostalCode Customer.Country Customer.State Customer.SalesRep Customer.Balance Customer.CreditLimit Customer.Discount Customer.Comments Customer.Contact Customer.EmailAddress Customer.Fax Customer.Phone Customer.Terms
&Scoped-define ENABLED-FIELDS-IN-QUERY-BROWSE-2
&Scoped-define QUERY-STRING-BROWSE-2 FOR EACH Customer NO-LOCK INDEXED-REPOSITION
&Scoped-define OPEN-QUERY-BROWSE-2 OPEN QUERY BROWSE-2 FOR EACH Customer NO-LOCK INDEXED-REPOSITION.
&Scoped-define TABLES-IN-QUERY-BROWSE-2 Customer
&Scoped-define FIRST-TABLE-IN-QUERY-BROWSE-2 Customer


/* Definitions for FRAME DEFAULT-FRAME                                  */
&Scoped-define OPEN-BROWSERS-IN-QUERY-DEFAULT-FRAME {&OPEN-QUERY-BROWSE-2}

/* Standard List Definitions                                            */
&Scoped-Define ENABLED-FIELDS Customer.CustNum Customer.Balance Customer.Name Customer.CreditLimit Customer.Address Customer.Discount Customer.Address2 Customer.Terms Customer.City Customer.PostalCode Customer.Country Customer.State Customer.EmailAddress Customer.Fax Customer.Phone Customer.SalesRep Customer.Comments
&Scoped-define ENABLED-TABLES Customer
&Scoped-define FIRST-ENABLED-TABLE Customer
&Scoped-Define ENABLED-OBJECTS RECT-1 BROWSE-2
&Scoped-Define DISPLAYED-FIELDS Customer.CustNum Customer.Balance Customer.Name Customer.CreditLimit Customer.Address Customer.Discount Customer.Address2 Customer.Terms Customer.City Customer.PostalCode Customer.Country Customer.State Customer.EmailAddress Customer.Fax Customer.Phone Customer.SalesRep Customer.Comments
&Scoped-define DISPLAYED-TABLES Customer
&Scoped-define FIRST-DISPLAYED-TABLE Customer


/* Custom List Definitions                                              */
/* List-1,List-2,List-3,List-4,List-5,List-6                            */

/* _UIB-PREPROCESSOR-BLOCK-END */
&ANALYZE-RESUME



/* ***********************  Control Definitions  ********************** */

/* Define the widget handle for the window                              */
DEFINE VAR C-Win AS WIDGET-HANDLE NO-UNDO.

/* Definitions of the field level widgets                               */
DEFINE RECTANGLE RECT-1
     EDGE-PIXELS 2 GRAPHIC-EDGE  NO-FILL
     SIZE 106 BY 19.29.

/* Query definitions                                                    */
&ANALYZE-SUSPEND
DEFINE QUERY BROWSE-2 FOR
      Customer SCROLLING.
&ANALYZE-RESUME

/* Browse definitions                                                   */
DEFINE BROWSE BROWSE-2
&ANALYZE-SUSPEND _UIB-CODE-BLOCK _DISPLAY-FIELDS BROWSE-2 C-Win _STRUCTURED
  QUERY BROWSE-2 NO-LOCK DISPLAY
      Customer.CustNum FORMAT ">>>>9":U
      Customer.Name FORMAT "x(30)":U
      Customer.Address FORMAT "x(35)":U
      Customer.Address2 FORMAT "x(35)":U
      Customer.City FORMAT "x(25)":U
      Customer.PostalCode FORMAT "x(10)":U
      Customer.Country FORMAT "x(20)":U
      Customer.State FORMAT "x(20)":U
      Customer.SalesRep FORMAT "x(4)":U
      Customer.Balance FORMAT "->,>>>,>>9.99":U
      Customer.CreditLimit FORMAT "->,>>>,>>9":U
      Customer.Discount FORMAT ">>9%":U
      Customer.Comments FORMAT "x(80)":U
      Customer.Contact FORMAT "x(30)":U
      Customer.EmailAddress FORMAT "x(50)":U
      Customer.Fax FORMAT "x(20)":U
      Customer.Phone FORMAT "x(20)":U
      Customer.Terms FORMAT "x(20)":U
/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME
    WITH NO-ROW-MARKERS SEPARATORS SIZE 78 BY 19.29 FIT-LAST-COLUMN.


/* ************************  Frame Definitions  *********************** */

DEFINE FRAME DEFAULT-FRAME
     BROWSE-2 AT ROW 1.71 COL 6 WIDGET-ID 200
     Customer.CustNum AT ROW 1.71 COL 109 COLON-ALIGNED WIDGET-ID 18
          VIEW-AS FILL-IN
          SIZE 9 BY 1
     Customer.Balance AT ROW 1.71 COL 169 COLON-ALIGNED WIDGET-ID 6
          VIEW-AS FILL-IN
          SIZE 20.2 BY 1
     Customer.Name AT ROW 2.71 COL 109 COLON-ALIGNED WIDGET-ID 26
          VIEW-AS FILL-IN
          SIZE 32 BY 1
     Customer.CreditLimit AT ROW 2.71 COL 169 COLON-ALIGNED WIDGET-ID 16
          VIEW-AS FILL-IN
          SIZE 16 BY 1
     Customer.Address AT ROW 3.71 COL 109 COLON-ALIGNED WIDGET-ID 2
          VIEW-AS FILL-IN
          SIZE 37 BY 1
     Customer.Discount AT ROW 3.71 COL 169 COLON-ALIGNED WIDGET-ID 20
          VIEW-AS FILL-IN
          SIZE 7.6 BY 1
     Customer.Address2 AT ROW 4.71 COL 109 COLON-ALIGNED WIDGET-ID 4
          VIEW-AS FILL-IN
          SIZE 37 BY 1
     Customer.Terms AT ROW 4.71 COL 169 COLON-ALIGNED WIDGET-ID 36
          VIEW-AS FILL-IN
          SIZE 22 BY 1
     Customer.City AT ROW 5.71 COL 109 COLON-ALIGNED WIDGET-ID 8
          VIEW-AS FILL-IN
          SIZE 27 BY 1
     Customer.PostalCode AT ROW 6.71 COL 109 COLON-ALIGNED WIDGET-ID 30
          VIEW-AS FILL-IN
          SIZE 15.6 BY 1
     Customer.Country AT ROW 7.71 COL 109 COLON-ALIGNED WIDGET-ID 14
          VIEW-AS FILL-IN
          SIZE 22 BY 1
     Customer.State AT ROW 8.62 COL 109 COLON-ALIGNED WIDGET-ID 34
          VIEW-AS FILL-IN
          SIZE 22 BY 1
     Customer.EmailAddress AT ROW 12.48 COL 109 COLON-ALIGNED WIDGET-ID 22
          VIEW-AS FILL-IN
          SIZE 52 BY 1
     Customer.Fax AT ROW 13.48 COL 109 COLON-ALIGNED WIDGET-ID 24
          VIEW-AS FILL-IN
          SIZE 22 BY 1
     Customer.Phone AT ROW 14.48 COL 109 COLON-ALIGNED WIDGET-ID 28
          VIEW-AS FILL-IN
          SIZE 22 BY 1
     Customer.SalesRep AT ROW 15.48 COL 109 COLON-ALIGNED WIDGET-ID 32
          VIEW-AS FILL-IN
          SIZE 9.6 BY 1
     Customer.Comments AT ROW 17.19 COL 109 COLON-ALIGNED WIDGET-ID 10
          VIEW-AS FILL-IN
          SIZE 82 BY 1
     RECT-1 AT ROW 1.24 COL 96 WIDGET-ID 38
    WITH 1 DOWN NO-BOX KEEP-TAB-ORDER OVERLAY
         SIDE-LABELS NO-UNDERLINE THREE-D
         AT COL 1 ROW 1
         SIZE 237.2 BY 23.48 WIDGET-ID 100.


/* *********************** Procedure Settings ************************ */

&ANALYZE-SUSPEND _PROCEDURE-SETTINGS
/* Settings for THIS-PROCEDURE
   Type: Window
   Allow: Basic,Browse,DB-Fields,Window,Query
   Other Settings: COMPILE
 */
&ANALYZE-RESUME _END-PROCEDURE-SETTINGS

/* *************************  Create Window  ************************** */

&ANALYZE-SUSPEND _CREATE-WINDOW
IF SESSION:DISPLAY-TYPE = "GUI":U THEN
  CREATE WINDOW C-Win ASSIGN
         HIDDEN             = YES
         TITLE              = "<insert window title>"
         HEIGHT             = 23.48
         WIDTH              = 237.2
         MAX-HEIGHT         = 23.48
         MAX-WIDTH          = 237.2
         VIRTUAL-HEIGHT     = 23.48
         VIRTUAL-WIDTH      = 237.2
         RESIZE             = yes
         SCROLL-BARS        = no
         STATUS-AREA        = no
         BGCOLOR            = ?
         FGCOLOR            = ?
         KEEP-FRAME-Z-ORDER = yes
         THREE-D            = yes
         MESSAGE-AREA       = no
         SENSITIVE          = yes.
ELSE {&WINDOW-NAME} = CURRENT-WINDOW.
/* END WINDOW DEFINITION                                                */
&ANALYZE-RESUME



/* ***********  Runtime Attributes and AppBuilder Settings  *********** */

&ANALYZE-SUSPEND _RUN-TIME-ATTRIBUTES
/* SETTINGS FOR WINDOW C-Win
  VISIBLE,,RUN-PERSISTENT                                               */
/* SETTINGS FOR FRAME DEFAULT-FRAME
   FRAME-NAME                                                           */
/* BROWSE-TAB BROWSE-2 RECT-1 DEFAULT-FRAME */
IF SESSION:DISPLAY-TYPE = "GUI":U AND VALID-HANDLE(C-Win)
THEN C-Win:HIDDEN = no.

/* _RUN-TIME-ATTRIBUTES-END */
&ANALYZE-RESUME


/* Setting information for Queries and Browse Widgets fields            */

&ANALYZE-SUSPEND _QUERY-BLOCK BROWSE BROWSE-2
/* Query rebuild information for BROWSE BROWSE-2
     _TblList          = "sports2000.Customer"
     _Options          = "NO-LOCK INDEXED-REPOSITION"
     _FldNameList[1]   = sports2000.Customer.CustNum
     _FldNameList[2]   = sports2000.Customer.Name
     _FldNameList[3]   = sports2000.Customer.Address
     _FldNameList[4]   = sports2000.Customer.Address2
     _FldNameList[5]   = sports2000.Customer.City
     _FldNameList[6]   = sports2000.Customer.PostalCode
     _FldNameList[7]   = sports2000.Customer.Country
     _FldNameList[8]   = sports2000.Customer.State
     _FldNameList[9]   = sports2000.Customer.SalesRep
     _FldNameList[10]   = sports2000.Customer.Balance
     _FldNameList[11]   = sports2000.Customer.CreditLimit
     _FldNameList[12]   = sports2000.Customer.Discount
     _FldNameList[13]   = sports2000.Customer.Comments
     _FldNameList[14]   = sports2000.Customer.Contact
     _FldNameList[15]   = sports2000.Customer.EmailAddress
     _FldNameList[16]   = sports2000.Customer.Fax
     _FldNameList[17]   = sports2000.Customer.Phone
     _FldNameList[18]   = sports2000.Customer.Terms
     _Query            is OPENED
*/  /* BROWSE BROWSE-2 */
&ANALYZE-RESUME





/* ************************  Control Triggers  ************************ */

&Scoped-define SELF-NAME C-Win
&ANALYZE-SUSPEND _UIB-CODE-BLOCK _CONTROL C-Win C-Win
ON END-ERROR OF C-Win /* <insert window title> */
OR ENDKEY OF {&WINDOW-NAME} ANYWHERE DO:
  /* This case occurs when the user presses the "Esc" key.
     In a persistently run window, just ignore this.  If we did not, the
     application would exit. */
  IF THIS-PROCEDURE:PERSISTENT THEN RETURN NO-APPLY.
END.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME


&ANALYZE-SUSPEND _UIB-CODE-BLOCK _CONTROL C-Win C-Win
ON WINDOW-CLOSE OF C-Win /* <insert window title> */
DO:
  /* This event will close the window and terminate the procedure.  */
  APPLY "CLOSE":U TO THIS-PROCEDURE.
  RETURN NO-APPLY.
END.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME


&Scoped-define BROWSE-NAME BROWSE-2
&Scoped-define SELF-NAME BROWSE-2
&ANALYZE-SUSPEND _UIB-CODE-BLOCK _CONTROL BROWSE-2 C-Win
ON VALUE-CHANGED OF BROWSE-2 IN FRAME DEFAULT-FRAME
DO:

    IF AVAILABLE (Customer) THEN

        DISPLAY {&DISPLAYED-FIELDS} WITH FRAME {&FRAME-NAME} .

END.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME


&Scoped-define SELF-NAME Customer.Country
&ANALYZE-SUSPEND _UIB-CODE-BLOCK _CONTROL Customer.Country C-Win
ON VALUE-CHANGED OF Customer.Country IN FRAME DEFAULT-FRAME /* Country */
DO:
    DO WITH FRAME {&frame-name}:

        IF Customer.Country:SCREEN-VALUE = "USA" THEN
            Customer.State:VISIBLE = TRUE .
        ELSE
            Customer.State:VISIBLE = FALSE .


    END.
END.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME


&UNDEFINE SELF-NAME

&ANALYZE-SUSPEND _UIB-CODE-BLOCK _CUSTOM _MAIN-BLOCK C-Win


/* ***************************  Main Block  *************************** */

/* Set CURRENT-WINDOW: this will parent dialog-boxes and frames.        */
ASSIGN CURRENT-WINDOW                = {&WINDOW-NAME}
       THIS-PROCEDURE:CURRENT-WINDOW = {&WINDOW-NAME}.

/* The CLOSE event can be used from inside or outside the procedure to  */
/* terminate it.                                                        */
ON CLOSE OF THIS-PROCEDURE
   RUN disable_UI.

/* Best default for GUI applications is...                              */
PAUSE 0 BEFORE-HIDE.

/* Now enable the interface and wait for the exit condition.            */
/* (NOTE: handle ERROR and END-KEY so cleanup code will always fire.    */
MAIN-BLOCK:
DO ON ERROR   UNDO MAIN-BLOCK, LEAVE MAIN-BLOCK
   ON END-KEY UNDO MAIN-BLOCK, LEAVE MAIN-BLOCK:
  RUN enable_UI.
  IF NOT THIS-PROCEDURE:PERSISTENT THEN
    WAIT-FOR CLOSE OF THIS-PROCEDURE.
END.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME


/* **********************  Internal Procedures  *********************** */

&ANALYZE-SUSPEND _UIB-CODE-BLOCK _PROCEDURE disable_UI C-Win  _DEFAULT-DISABLE
PROCEDURE disable_UI :
/*------------------------------------------------------------------------------
  Purpose:     DISABLE the User Interface
  Parameters:  <none>
  Notes:       Here we clean-up the user-interface by deleting
               dynamic widgets we have created and/or hide
               frames.  This procedure is usually called when
               we are ready to "clean-up" after running.
------------------------------------------------------------------------------*/
  /* Delete the WINDOW we created */
  IF SESSION:DISPLAY-TYPE = "GUI":U AND VALID-HANDLE(C-Win)
  THEN DELETE WIDGET C-Win.
  IF THIS-PROCEDURE:PERSISTENT THEN DELETE PROCEDURE THIS-PROCEDURE.
END PROCEDURE.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME

&ANALYZE-SUSPEND _UIB-CODE-BLOCK _PROCEDURE enable_UI C-Win  _DEFAULT-ENABLE
PROCEDURE enable_UI :
/*------------------------------------------------------------------------------
  Purpose:     ENABLE the User Interface
  Parameters:  <none>
  Notes:       Here we display/view/enable the widgets in the
               user-interface.  In addition, OPEN all queries
               associated with each FRAME and BROWSE.
               These statements here are based on the "Other
               Settings" section of the widget Property Sheets.
------------------------------------------------------------------------------*/
  IF AVAILABLE Customer THEN
    DISPLAY Customer.CustNum Customer.Balance Customer.Name Customer.CreditLimit
          Customer.Address Customer.Discount Customer.Address2 Customer.Terms
          Customer.City Customer.PostalCode Customer.Country Customer.State
          Customer.EmailAddress Customer.Fax Customer.Phone Customer.SalesRep
          Customer.Comments
      WITH FRAME DEFAULT-FRAME IN WINDOW C-Win.
  ENABLE RECT-1 BROWSE-2 Customer.CustNum Customer.Balance Customer.Name
         Customer.CreditLimit Customer.Address Customer.Discount
         Customer.Address2 Customer.Terms Customer.City Customer.PostalCode
         Customer.Country Customer.State Customer.EmailAddress Customer.Fax
         Customer.Phone Customer.SalesRep Customer.Comments
      WITH FRAME DEFAULT-FRAME IN WINDOW C-Win.
  {&OPEN-BROWSERS-IN-QUERY-DEFAULT-FRAME}
  VIEW C-Win.
END PROCEDURE.

/* _UIB-CODE-BLOCK-END */
&ANALYZE-RESUME

