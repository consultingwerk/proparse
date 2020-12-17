header {
  package org.prorefactor.treeparser01;

  import org.prorefactor.core.IJPNode;
  import org.prorefactor.treeparser.CQ;
  import org.prorefactor.treeparser.IJPTreeParser;
  
  import java.util.ArrayList;
}

options {
        language = "Java";
}

// Class preamble - anything here gets inserted in front of the class definition.
{
} // Class preamble


// class definition options
class TreeParser01 extends TreeParser;

options {
  importVocab= ProParser;
  defaultErrorHandler= false;
  classHeaderSuffix= IJPTreeParser;
}

// This is added to top of the class definitions
{

  // --- The following are required in all tree parsers ---

  // Where did the tree parser leave off parsing -- might give us at least a bit
  // of an idea where things left off if an exception was thrown.
  // See antlr/TreeParser and the generated code.
  public AST get_retTree() {
    return _retTree;
  }

  // Func for grabbing the "state2" attribute from the node at LT(1) 
  private boolean state2(AST node, int match) {
    return ((IJPNode)node).getState2() == match;
  }


  // --- The above are for all tree parsers, below are for TreeParser01 ---


  /** Create a tree parser with a specific action object. */
  public TreeParser01(TP01Action actionObject) {
    action = actionObject;
  }
  
  /** By default, the action object is a new TP01Support. */
  TP01Action action = null; // See initialization block, below.

  // Initialization block
  {
    if (action==null) action = new TP01Support();
  }

  /** Get the action object. getActionObject and getTpSupport are identical. */
  public TP01Action getActionObject() { return action; }

  /** Set the action object.
   * By default, the support object is a new TP01Support,
   * but you can configure this to be any TP01Action object.
   * setTpSupport and setActionObject are identical.
   */
  public void setActionObject(TP01Action action) { this.action = action; }


  /** This tree parser's stack. I think it is best to keep the stack
   * in the tree parser grammar for visibility sake, rather than hide
   * it in the support class. If we move grammar and actions around
   * within this .g, the effect on the stack should be highly visible.
   */ 
  private ArrayList stack = new ArrayList();
  private void push(Object o) { stack.add(o); }
  private Object pop() { return stack.remove(stack.size()-1); }


}
program :#( p:Program_root {action.programRoot(#p);}
      (blockorstate)*
      Program_tail
      {action.programTail();}
    )
  ;

block_for :#( FOR rn1:tbl[CQ.BUFFERSYMBOL] {action.strongScope(#rn1);}
      (COMMA rn2:tbl[CQ.BUFFERSYMBOL] {action.strongScope(#rn2);} )*
    )
  ;

block_opt :#(Block_iterator fld[CQ.UPDATING] EQUAL expression TO expression (BY constant)? )
  | querytuningphrase 
  | #(WHILE expression )
  | TRANSACTION 
  | #(STOPAFTER expression )
  | on___phrase 
  | framephrase 
  | BREAK
  | #(BY expression (DESCENDING)? )
  | collatephrase
  | #(GROUP ( #(BY expression (DESCENDING)? ) )+ )
  ;

block_preselect :#(PRESELECT for_record_spec[CQ.INITWEAK] )
  ;

functioncall :#(ACCUMULATE accum_what (#(BY expression (DESCENDING)?))? expression )
  | #(ADDINTERVAL LEFTPAREN expression COMMA expression COMMA expression RIGHTPAREN )
  | #(AUDITENABLED LEFTPAREN (expression)? RIGHTPAREN )
  | #(BUFFER_GROUP_ID LEFTPAREN ID RIGHTPAREN )
  | #(BUFFER_GROUP_NAME LEFTPAREN ID RIGHTPAREN )
  | #(BUFFER_PARTITION_ID LEFTPAREN ID RIGHTPAREN )
  | #(BUFFER_TENANT_ID LEFTPAREN ID RIGHTPAREN )
  | #(BUFFER_TENANT_NAME LEFTPAREN ID RIGHTPAREN )
  | canfindfunc // has extra "action." support handling in this tree parser.
  | #(CAST LEFTPAREN expression COMMA TYPE_NAME RIGHTPAREN )
  | currentvaluefunc // is also a pseudfn.
  | dynamiccurrentvaluefunc // is also a pseudfn.
  | #(  df:DYNAMICFUNCTION
      {action.callBegin(#df);}
      LEFTPAREN expression (#(IN_KW expression))? (COMMA parameter)* RIGHTPAREN (NOERROR_KW)?
      {action.callEnd();}
    )
  | #(  di:DYNAMICINVOKE
      {action.callBegin(#di);}
      LEFTPAREN
      (TYPE_NAME|exprt)
      COMMA expression
      (COMMA parameter)*
      RIGHTPAREN
      {action.callEnd();}
    )
  // ENTERED and NOTENTERED are only dealt with as part of an expression term. See: exprt.
  | entryfunc // is also a pseudfn.
  | #(ETIME_KW (funargs)? )
  | #(EXTENT LEFTPAREN fld[CQ.SYMBOL] RIGHTPAREN )
  | #(FRAMECOL (LEFTPAREN ID RIGHTPAREN)? )
  | #(FRAMEDOWN (LEFTPAREN ID RIGHTPAREN)? )
  | #(FRAMELINE (LEFTPAREN ID RIGHTPAREN)? )
  | #(FRAMEROW (LEFTPAREN ID RIGHTPAREN)? )
  | #(GETCODEPAGE funargs )
  | #(GUID LEFTPAREN (expression)? RIGHTPAREN )
  | #(IF expression THEN expression ELSE expression )
  | ldbnamefunc 
  | lengthfunc // is also a pseudfn.
  | #(LINECOUNTER (LEFTPAREN ID RIGHTPAREN)? )
  | #(MTIME (funargs)? )
  | nextvaluefunc // is also a pseudfn.
    // ENTERED and NOTENTERED are only dealt with as part of an expression term. See: exprt.
  | #(PAGENUMBER (LEFTPAREN ID RIGHTPAREN)? )
  | #(PAGESIZE_KW (LEFTPAREN ID RIGHTPAREN)? )
  | #(PROVERSION (funargs)? )
  | rawfunc // is also a pseudfn.
  | #(SEEK LEFTPAREN (INPUT|OUTPUT|ID|STREAMHANDLE expression) RIGHTPAREN )
  | substringfunc // is also a pseudfn.
  | #(sr:SUPER {action.callBegin(#sr);} (parameterlist)? {action.callEnd();} )
  | #(TENANT_ID LEFTPAREN (expression)? RIGHTPAREN )
  | #(TENANT_NAME LEFTPAREN (expression)? RIGHTPAREN )
  | #(GET_EFFECTIVE_TENANT_ID LEFTPAREN (expression)? RIGHTPAREN )
  | #(GET_EFFECTIVE_TENANT_NAME LEFTPAREN (expression)? RIGHTPAREN )
  | #(IS_DB_MULTI_TENANT LEFTPAREN (expression)? RIGHTPAREN ) 
  | #(SET_EFFECTIVE_TENANT LEFTPAREN expression (COMMA expression)? RIGHTPAREN )
  | #(TIMEZONE (funargs)? )
  | #(TYPEOF LEFTPAREN expression COMMA TYPE_NAME RIGHTPAREN )
  | #(GETCLASS LEFTPAREN TYPE_NAME RIGHTPAREN )
  | #(USERID (funargs)? )
  | #(USER (funargs)? )
  | sqlaggregatefunc  
  | argfunc
  | noargfunc
  | recordfunc
  ;

recordfunargs :(LEFTPAREN tbl[CQ.REF] RIGHTPAREN | tbl[CQ.REF])
  ;

parameter {action.paramForCall(_t);}
:(  #(  BUFFER bt:tbl[CQ.INIT]
        { action.paramProgressType(BUFFER);
          action.paramSymbol(#bt);
        }
      )
    | #(OUTPUT parameter_arg )
    | #(INPUTOUTPUT parameter_arg )
    | #(INPUT parameter_arg )
    )
{action.paramEnd();}
  ;

parameter_arg :(  TABLEHANDLE thf:fld[CQ.INIT] parameter_dataset_options
      {action.paramSymbol(#thf);}
    | TABLE (FOR)? tt:tbl[CQ.TEMPTABLESYMBOL] parameter_dataset_options
      { action.paramProgressType(TEMPTABLE);
        action.paramSymbol(#tt);
      }
    | DATASET ds:ID parameter_dataset_options
      { action.setSymbol(DATASET, #ds);
        action.paramProgressType(DATASET);
        action.paramSymbol(#ds);
      }
    | DATASETHANDLE dsh:fld[CQ.INIT] parameter_dataset_options
      {action.paramSymbol(#dsh);}
    | PARAMETER expression EQUAL expression // for RUN STORED-PROCEDURE.
      {action.paramProgressType(PARAMETER);}
    | ID AS {action.paramNoName(_t);} (CLASS TYPE_NAME | datatype_com_native | datatype_var )
    | ex:expression (AS datatype_com)? {action.paramExpression(#ex);}
    )
    (BYPOINTER|BYVARIANTPOINTER)?
  ;

parameter_dataset_options :(APPEND)? (BYVALUE|BYREFERENCE| BIND {action.paramBind();} )?
  ;

filenameorvalue :#(VALUE LEFTPAREN exp:expression RIGHTPAREN ) { action.fnvExpression(#exp); }
  | fn:FILENAME { action.fnvFilename(#fn); }
  ;

exprt :#(LEFTPAREN expression RIGHTPAREN )
  | constant
  | widattr
  | #(uf:USER_FUNC {action.callBegin(#uf);} parameterlist_noroot {action.callEnd();} )
  | #(lm:LOCAL_METHOD_REF {action.callBegin(#lm);} parameterlist_noroot {action.callEnd();} )
  | ( #(NEW TYPE_NAME) )=> #(NEW tn:TYPE_NAME {action.callBegin(#tn);} parameterlist {action.callEnd();} )
  | // SUPER is amibiguous between functioncall and systemhandlename
    ( options{generateAmbigWarnings=false;}
    : functioncall
    | systemhandlename
    )
  | fld[CQ.REF]
  | #(Entered_func fld[CQ.SYMBOL] (NOT)? ENTERED )
  | tbl[CQ.REF] // for DISPLAY buffername, etc.
  ;

widattr :#( Widget_ref
      (NORETURNVALUE)?
      ( (widname)=> widname
      | exprt
      )
      ( (OBJCOLON|DOUBLECOLON) aname:. (array_subscript)?
        ( {action.callBegin(#aname);}
          method_param_list
          {action.callEnd();}
        )? 
      )+
      (#(IN_KW (MENU|FRAME|BROWSE|SUBMENU|BUFFER) ID ))? (AS .)?
    )
  ;

gwidget :#( Widget_ref s_widget
      ( #(  IN_KW
          ( MENU ID
          | FRAME f:ID { action.frameRef(#f); }
          | BROWSE b:ID { action.browseRef(#b); }
          | SUBMENU ID
          | BUFFER ID
          )
        )
      )?
    )
  ;

s_widget :widname | fld[CQ.REF]
  ;

widname :systemhandlename
  | DATASET ID
  | DATASOURCE ID
  | FIELD fld[CQ.REF]
  | FRAME f:ID { action.frameRef(#f); }
  | MENU ID
  | SUBMENU ID
  | MENUITEM ID
  | BROWSE b:ID  { action.browseRef(#b); }
  | QUERY ID
  | TEMPTABLE ID
  | BUFFER ID
  | XDOCUMENT ID
  | XNODEREF ID
  | SOCKET ID
  | STREAM ID
  ;

tbl[int contextQualifier] :id:RECORD_NAME {action.recordNameNode(#id, contextQualifier);}
  ;

fld[int contextQualifier] :#(ref:Field_ref (INPUT)? (frame_ref|browse_ref)? id:ID (array_subscript)? )
    // Note that sequence is important. This must be called after the full Field_ref branch has
    // been walked, because any frame or browse ID must be resolved before trying to resolve Field_ref.
    // (For example, this is required for resolving if the INPUT function was used.)
    {action.field(#ref, #id, contextQualifier, 0);}
  ;

fld1[int contextQualifier] :#(ref:Field_ref (INPUT)? (frame_ref|browse_ref)? id:ID (array_subscript)? )
    {action.field(#ref, #id, contextQualifier, 1);}
  ;

fld2[int contextQualifier] :#(ref:Field_ref (INPUT)? (frame_ref|browse_ref)? id:ID (array_subscript)? )
    {action.field(#ref, #id, contextQualifier, 2);}
  ;

aggregate_opt :#(id1:AVERAGE (label_constant)? {action.addToSymbolScope(action.defineVariable(#id1, #id1, DECIMAL));} )
  | #(id2:COUNT (label_constant)? {action.addToSymbolScope(action.defineVariable(#id2, #id2, INTEGER));} )
  | #(id3:MAXIMUM (label_constant)? {action.addToSymbolScope(action.defineVariable(#id3, #id3, DECIMAL));} )
  | #(id4:MINIMUM (label_constant)? {action.addToSymbolScope(action.defineVariable(#id4, #id4, DECIMAL));} )
  | #(id5:TOTAL (label_constant)? {action.addToSymbolScope(action.defineVariable(#id5, #id5, DECIMAL));} )
  | #(id6:SUBAVERAGE (label_constant)? {action.addToSymbolScope(action.defineVariable(#id6, #id6, DECIMAL));} )
  | #(id7:SUBCOUNT (label_constant)? {action.addToSymbolScope(action.defineVariable(#id7, #id7, DECIMAL));} )
  | #(id8:SUBMAXIMUM (label_constant)? {action.addToSymbolScope(action.defineVariable(#id8, #id8, DECIMAL));} )
  | #(id9:SUBMINIMUM (label_constant)? {action.addToSymbolScope(action.defineVariable(#id9, #id9, DECIMAL));} )
  | #(id10:SUBTOTAL (label_constant)? {action.addToSymbolScope(action.defineVariable(#id10, #id10, DECIMAL));} )
  ;

assignment_list :tbl[CQ.UPDATING] (#(EXCEPT (fld1[CQ.SYMBOL])*))?
  | ( assign_equal (#(WHEN expression))?
    | #(  Assign_from_buffer fld[CQ.UPDATING] )
      (#(WHEN expression))?
    )*
  ;

assign_equal :#(EQUAL
      ( pseudfn
      | fld[CQ.UPDATING]
      )
      expression
    )
  ;

referencepoint :fld[CQ.SYMBOL] ((PLUS|MINUS) expression)?
  ;

browse_ref :#(BROWSE i:ID) { action.browseRef(#i); }
  ;

buffercomparestate :#(  BUFFERCOMPARE
      tbl[CQ.REF]
      ( #(EXCEPT (fld1[CQ.SYMBOL])*)
      | #(USING (fld1[CQ.REF])+)
      )?
      TO tbl[CQ.REF]
      (CASESENSITIVE|BINARY)?
      ( #(SAVE ( #(RESULT IN_KW) )? fld[CQ.UPDATING] ) )?
      (EXPLICIT)?
      ( COMPARES
        (NOERROR_KW)?
        block_colon
        #(Code_block ( #(WHEN expression THEN blockorstate ) )* )
        #(END (COMPARES)? )
      )?
      (NOLOBS)?
      (NOERROR_KW)?
      state_end
    )
  ;

buffercopystate :#( BUFFERCOPY tbl[CQ.REF]
      ( #(EXCEPT (fld1[CQ.SYMBOL])*)
      | #(USING (fld1[CQ.REF])+)
      )?
      TO tbl[CQ.UPDATING]
      ( #(ASSIGN assignment_list ) )?
      (NOLOBS)?
      (NOERROR_KW)?
      state_end 
    )
  ;

canfindfunc :#( cf:CANFIND LEFTPAREN (findwhich)?
      #(  r:RECORD_NAME
        { action.canFindBegin(#cf, #r);
          action.recordNameNode(#r, CQ.INIT);
        }
        recordphrase
        {action.canFindEnd(#cf);}
      )
      RIGHTPAREN
    )
  ;

choosestate :#( head:CHOOSE (ROW|FIELD)  { action.frameInitializingStatement(#head); }
      ( #(fi:Form_item fld[CQ.UPDATING] {action.formItem(#fi);} (#(HELP constant))? ) )+
      ( AUTORETURN 
      | #(COLOR anyorvalue) 
      | goonphrase
      | #(KEYS fld[CQ.UPDATING] )
      | NOERROR_KW 
      | #(PAUSE expression)
      )*
      (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

enumstate
  :  #(  ENUM TYPE_NAME (FLAGS)? block_colon
      defenumstate
      #(END (ENUM)? )
      state_end
     )
  ;

defenumstate
  :  #( DEFINE ENUM (enum_member)+ state_end )
  ;

enum_member
  : TYPE_NAME ( EQUAL ( NUMBER | TYPE_NAME (COMMA TYPE_NAME)*))?
  ;

classstate :#(  c:CLASS {action.classState(#c);}
      TYPE_NAME
      ( #(INHERITS TYPE_NAME)
      | #(IMPLEMENTS TYPE_NAME (COMMA TYPE_NAME)* )
      | USEWIDGETPOOL
      | ABSTRACT
      | FINAL
      |   SERIALIZABLE
      )*
      block_colon
      code_block
      #(END (CLASS)? )
      state_end
    )
  ;

clearstate :#(c:CLEAR (frame_ref)? (ALL)? (NOPAUSE)? state_end {action.clearState(#c);} )
  ;

closestoredprocedurestate :#( CLOSE
      STOREDPROCEDURE ID
      ( #(EQUAL fld[CQ.REF] PROCSTATUS ) )?
      ( #(WHERE PROCHANDLE EQ fld[CQ.REF] ) )?
      state_end
    )
  ;

colorstate :#(  head:COLOR  { action.frameInitializingStatement(#head); }
      ( ( #(DISPLAY anyorvalue) | #(PROMPT anyorvalue) )
        ( #(DISPLAY anyorvalue) | #(PROMPT anyorvalue) )?
      )?
      ( #(  fi:Form_item fld[CQ.SYMBOL]
          // formItem() must be called after fld[], but before formatphrase.
          {action.formItem(#fi);}  (formatphrase)?
        )
      )*
      (framephrase)? state_end  { action.frameStatementEnd(); }
    )
  ;

columnformat :#(  Format_phrase
      ( #(FORMAT expression)
      | label_constant
      | NOLABELS
      | #(COLUMNFONT expression )
      | #(COLUMNDCOLOR expression )
      | #(COLUMNBGCOLOR expression )
      | #(COLUMNFGCOLOR expression )
      | #(COLUMNPFCOLOR expression )
      | #(LABELFONT expression )
      | #(LABELDCOLOR expression )
      | #(LABELBGCOLOR expression )
      | #(LABELFGCOLOR expression )
      | #(LEXAT af:fld[CQ.SYMBOL] {action.lexat(#af);} (columnformat)? )
      | #(HEIGHT NUMBER )
      | #(HEIGHTPIXELS NUMBER )
      | #(HEIGHTCHARS NUMBER )
      | #(WIDTH NUMBER )
      | #(WIDTHPIXELS NUMBER )
      | #(WIDTHCHARS NUMBER )
      )+ 
    )
  ;

constructorstate :#(  c:CONSTRUCTOR
      {action.structorBegin(#c);}
      def_modifiers TYPE_NAME function_params
      block_colon code_block #(END (CONSTRUCTOR|METHOD)? ) state_end
      {action.structorEnd(#c);}
    )
  ;

createstate :#(CREATE tbl[CQ.UPDATING] (#(FOR TENANT expression))? (#(USING (ROWID|RECID) expression))? (NOERROR_KW)? state_end )
  ;

create_whatever_args :fld[CQ.UPDATING] (#(IN_KW WIDGETPOOL expression))? (NOERROR_KW)?
  ;

createautomationobjectstate :#(CREATE QSTRING fld[CQ.UPDATING] (#(CONNECT (#(TO expression))?))? (NOERROR_KW)? state_end )
  ;

createbrowsestate :#(CREATE BROWSE fld[CQ.UPDATING] (#(IN_KW WIDGETPOOL expression))? (NOERROR_KW)? (assign_opt)? (triggerphrase)? state_end )
  ;

createbufferstate :#( CREATE BUFFER fld[CQ.UPDATING] FOR TABLE expression
      ( #(BUFFERNAME expression) )?
      (#(IN_KW WIDGETPOOL expression))?
      (NOERROR_KW)? state_end
    )
  ;

createserverstate :#(CREATE SERVER fld[CQ.UPDATING] (assign_opt)? state_end )
  ;

createserversocketstate :#(CREATE SERVERSOCKET fld[CQ.UPDATING] (NOERROR_KW)? state_end )
  ;

createsocketstate :#(CREATE SOCKET fld[CQ.UPDATING] (NOERROR_KW)? state_end )
  ;

createtemptablestate :#(CREATE TEMPTABLE fld[CQ.UPDATING] (#(IN_KW WIDGETPOOL expression))? (NOERROR_KW)? state_end )
  ;

createwidgetstate :#( CREATE
      ( valueexpression
      | BUTTON | COMBOBOX | CONTROLFRAME | DIALOGBOX | EDITOR | FILLIN | FRAME | IMAGE
      | MENU | MENUITEM | RADIOSET | RECTANGLE | SAXATTRIBUTES | SELECTIONLIST | SLIDER
      | SUBMENU | TEXT | TOGGLEBOX | WINDOW
      )
      fld[CQ.UPDATING]
      (#(IN_KW WIDGETPOOL expression))? (NOERROR_KW)? (assign_opt)? (triggerphrase)? state_end
    )
  ;

ddegetstate :#(DDE GET expression TARGET fld[CQ.UPDATING] ITEM expression (#(TIME expression))? (NOERROR_KW)? state_end )
  ;

ddeinitiatestate :#(DDE INITIATE fld[CQ.UPDATING] FRAME expression APPLICATION expression TOPIC expression (NOERROR_KW)? state_end )
  ;

dderequeststate :#(DDE REQUEST expression TARGET fld[CQ.UPDATING] ITEM expression (#(TIME expression))? (NOERROR_KW)? state_end )
  ;

definebrowsestate :#( def:DEFINE (def_shared)? def_modifiers BROWSE
      id:ID { push(action.defineBrowse(#def, #id)); }
      (#(QUERY ID))? (lockhow|NOWAIT)*
      ( #(  DISPLAY
          ( #(  fi1:Form_item
              ( (tbl[CQ.INIT])=> tbl[CQ.INIT]
              | expression (columnformat)? (viewasphrase)?
              | spacephrase
              )
              // Note for DISPLAY, formItem() is called *after* any potential format '@' phrase.
              { action.formItem(#fi1); }
            )
          )*
          (#(EXCEPT (fld1[CQ.SYMBOL])*))?
        )
        ( #(  ENABLE
            ( #(ALL (#(EXCEPT (fld[CQ.SYMBOL])*))? )
            | ( #(  fi2:Form_item fld[CQ.SYMBOL]  { action.formItem(#fi2); }
                  ( #(HELP constant)
                  | #(VALIDATE funargs)
                  | AUTORETURN
                  | DISABLEAUTOZAP
                  )*
                )
              )*
            )
          )
        )?
      )?
      (display_with)*
      (tooltip_expr)?
      (#(CONTEXTHELPID expression))?
      state_end
      { action.addToSymbolScope(pop()); }
    )
  ;

definebufferstate :#( def:DEFINE (def_shared)? def_modifiers BUFFER id:ID
      FOR (TEMPTABLE)? rec:tbl[CQ.SYMBOL]
      { action.defineBuffer(#def, #id, #rec, false); }
      (PRESELECT)? (label_constant)?
      (namespace_uri)? (namespace_prefix)? (xml_node_name)?
      (#(FIELDS (fld1[CQ.SYMBOL])* ))? state_end
    )
  ;

definebuttonstate :#( def:DEFINE (def_shared)? def_modifiers BUTTON
      id:ID { push(action.defineSymbol(BUTTON, #def, #id)); }
      ( AUTOGO
      | AUTOENDKEY
      | DEFAULT
      | color_expr
      | #(CONTEXTHELPID expression)
      | DROPTARGET
      | #(FONT expression)
      | #(IMAGEDOWN (imagephrase_opt)+ )
      | #(IMAGE (imagephrase_opt)+ )
      | #(IMAGEUP (imagephrase_opt)+ )
      | #(IMAGEINSENSITIVE (imagephrase_opt)+ )
      | #(MOUSEPOINTER expression )
      | label_constant
      | #(LIKE fld[CQ.SYMBOL] (VALIDATE)?)
      | FLATBUTTON
      | #(NOFOCUS (FLATBUTTON)? )
      | NOCONVERT3DCOLORS
      | tooltip_expr
      | sizephrase (MARGINEXTRA)?
      )*
      (triggerphrase)?
      state_end
      { action.addToSymbolScope(pop()); }
    )
  ;

definedatasetstate :#(  def:DEFINE (def_shared)? def_modifiers DATASET
      id:ID { push(action.defineSymbol(DATASET, #def, #id)); }
      (namespace_uri)? (namespace_prefix)? (xml_node_name)?
      ( #(SERIALIZENAME QSTRING) )?
      (xml_node_type)? (SERIALIZEHIDDEN)?
      (REFERENCEONLY)?
      FOR tb1:tbl[CQ.INIT] {action.datasetTable(#tb1);}
      (COMMA tb2:tbl[CQ.INIT] {action.datasetTable(#tb2);} )*
      ( data_relation ( (COMMA)? data_relation)* )?
      ( parent_id_relation ( (COMMA)? parent_id_relation)* )?
      state_end
      { action.addToSymbolScope(pop()); }
    )
  ;

data_relation :#( DATARELATION (ID)?
      FOR tbl[CQ.INIT] COMMA tbl[CQ.INIT]
      ( field_mapping_phrase
      | REPOSITION
      | #(NESTED (FOREIGNKEYHIDDEN)?)
      | NOTACTIVE
      | RECURSIVE
      )*
    )
  ;
  
field_mapping_phrase :#(RELATIONFIELDS LEFTPAREN fld2[CQ.SYMBOL] COMMA fld1[CQ.SYMBOL]
    ( COMMA fld2[CQ.SYMBOL] COMMA fld1[CQ.SYMBOL] )* RIGHTPAREN )
  ;

definedatasourcestate :#( def:DEFINE (def_shared)? def_modifiers DATASOURCE
      id:ID { push(action.defineSymbol(DATASOURCE, #def, #id)); }
      FOR (#(QUERY ID))?
      (source_buffer_phrase)? (COMMA source_buffer_phrase)*
      state_end
      { action.addToSymbolScope(pop()); }
    )
  ;

source_buffer_phrase :#(  r:RECORD_NAME {action.recordNameNode(#r, CQ.INIT);}
      ( KEYS LEFTPAREN ( ROWID | fld[CQ.SYMBOL] (COMMA fld[CQ.SYMBOL])* ) RIGHTPAREN )?
    )
  ;

defineeventstate :#(  def:DEFINE def_modifiers EVENT
      id:ID { push(action.defineEvent(#def, #id)); }
      ( #(SIGNATURE VOID function_params)
      | #(DELEGATE (CLASS)? TYPE_NAME)
      )
      state_end
    )
    { action.addToSymbolScope(pop()); }
  ;

defineframestate :#(  def:DEFINE (def_shared)?
      // Note that frames cannot be inherited. If that ever changes, then things will get tricky
      // when creating the symbol tables for inheritance caching. See Frame.copyBare(), and the
      // attributes of Frame that it does not deal with.
      (PRIVATE)?  // important: see note above.
      FRAME
      id:ID { action.frameDef(#def, #id); }
      (form_item[CQ.SYMBOL])*
      ( #(HEADER (display_item)+ )
      | #(BACKGROUND (display_item)+ )
      )?
      (#(EXCEPT (fld1[CQ.SYMBOL])*))?  (framephrase)?  state_end  { action.frameStatementEnd(); }
      // Frames are automatically and immediately added to the SymbolScope. No need to do it here.
    )
  ;

defineimagestate :#(  def:DEFINE (def_shared)? def_modifiers IMAGE
      id:ID { push(action.defineSymbol(IMAGE, #def, #id)); }
      ( #(LIKE fld[CQ.SYMBOL] (VALIDATE)?)
      | imagephrase_opt 
      | sizephrase
      | color_expr
      | CONVERT3DCOLORS
      | tooltip_expr
      | #(STRETCHTOFIT (RETAINSHAPE)? )
      | TRANSPARENT
      )*
      (triggerphrase)?
      state_end
      { action.addToSymbolScope(pop()); }
    )
  ;

definemenustate :#( def:DEFINE (def_shared)? def_modifiers MENU
      id:ID { push(action.defineSymbol(MENU, #def, #id)); }
      (menu_opt)* (menu_list_item)* state_end
      { action.addToSymbolScope(pop()); }
    )
  ;

menu_opt :color_expr
  | #(FONT expression)
  | #(LIKE fld[CQ.SYMBOL] (VALIDATE)?)
  | #(TITLE expression)
  | MENUBAR
  | PINNABLE
  | SUBMENUHELP
  ;

menu_list_item :( #(  MENUITEM
        id:ID { push(action.defineSymbol(MENUITEM, #id, #id)); }
        ( #(ACCELERATOR expression )
        | color_expr
        | DISABLED
        | #(FONT expression)
        | label_constant
        | READONLY
        | TOGGLEBOX
        )*
        (triggerphrase)? 
        { action.addToSymbolScope(pop()); }
      )
    | #(  SUBMENU
        id2:ID { push(action.defineSymbol(SUBMENU, #id2, #id2)); }
        (DISABLED | label_constant | #(FONT expression) | color_expr)*
        { action.addToSymbolScope(pop()); }
      )
    | #(RULE (#(FONT expression) | color_expr)* )
    | SKIP
    )
    // You can have PERIOD between menu items.
    ((PERIOD (RULE|SKIP|SUBMENU|MENUITEM))=> PERIOD)?
  ;

defineparameterstate :#(  def:DEFINE (def_shared)? def_modifiers
      ( PARAMETER buff:BUFFER bid:ID FOR (TEMPTABLE)? brec:tbl[CQ.SYMBOL]
        { action.paramForRoutine(#buff);
          action.defineBuffer(#def, #bid, #brec, true);
          action.paramSymbol(#bid);
          action.paramProgressType(BUFFER);
        }
        (PRESELECT)? (label_constant)? (#(FIELDS (fld1[CQ.SYMBOL])* ))?
      | {action.paramForRoutine(_t);}
        (INPUT|OUTPUT|INPUTOUTPUT|RETURN) PARAMETER
        ( TABLE FOR tb1:tbl[CQ.TEMPTABLESYMBOL] defineparam_ab
          { action.paramProgressType(TEMPTABLE);
            action.paramSymbol(#tb1);
          }
        | TABLEHANDLE (FOR)? id:ID defineparam_ab
          { action.addToSymbolScope(action.defineVariable(#def, #id, HANDLE));
            action.paramSymbol(#id);
          }
        | DATASET FOR ds:ID defineparam_ab
          { action.setSymbol(DATASET, #ds);
            action.paramProgressType(DATASET);
            action.paramSymbol(#ds);
          }
        | DATASETHANDLE id3:ID defineparam_ab
          { action.addToSymbolScope(action.defineVariable(#def, #id3, HANDLE));
            action.paramSymbol(#id3);
          }
        | id2:ID
          { push(action.defineVariable(#def, #id2));
            action.paramSymbol(#id2);
          }
          defineparam_var (triggerphrase)?
          { action.addToSymbolScope(pop()); }
        )
      )
      state_end
    )
    {action.paramEnd();}
  ;

defineparam_ab :( APPEND | BYVALUE | BIND {action.paramBind();} )*
  ;

defineparam_var :(  #(  as:AS
        ( (HANDLE (TO)? datatype_dll)=> HANDLE (TO)? datatype_dll
        | CLASS TYPE_NAME
        | datatype_param
        )
      )
      {action.defAs(#as);}
    )?
    ( options{greedy=true;}
    : casesens_or_not | #(FORMAT expression) | #(DECIMALS expression )
    | #(li:LIKE fld[CQ.SYMBOL] (VALIDATE)? {action.defLike(#li);} )
    | initial_constant | label_constant | NOUNDO | extentphrase_def_symbol
    )*
  ;

definepropertystate :#( def:DEFINE def_modifiers PROPERTY
      id:ID {push(action.defineVariable(#def, #id));}
      as:AS datatype {action.defAs(#as);} (extentphrase_def_symbol|initial_constant|NOUNDO|#(SERIALIZENAME QSTRING))*
      {action.addToSymbolScope(pop());}
      defineproperty_accessor (defineproperty_accessor)?
    )
  ;

definequerystate :#(  def:DEFINE (def_shared)? def_modifiers QUERY
      id:ID { push(action.defineSymbol(QUERY, #def, #id)); }
      FOR tbl[CQ.INIT] (record_fields)?
      (COMMA tbl[CQ.INIT] (record_fields)?)*
      ( #(CACHE expression) | SCROLLING | RCODEINFORMATION)*
      state_end
    )
    { action.addToSymbolScope(pop()); }
  ;

definerectanglestate :#(  def:DEFINE (def_shared)? def_modifiers RECTANGLE
      id:ID { push(action.defineSymbol(RECTANGLE, #def, #id)); }
      ( NOFILL
      | #(EDGECHARS expression )
      | #(EDGEPIXELS expression )
      | color_expr
      | GRAPHICEDGE
      | #(LIKE fld[CQ.SYMBOL] (VALIDATE)?)
      | sizephrase
      | tooltip_expr
      | ROUNDED
      | GROUPBOX
      )*
      (triggerphrase)?
      state_end
    )
    { action.addToSymbolScope(pop()); }
  ;

definestreamstate :#( def:DEFINE (def_shared)? def_modifiers STREAM id:ID state_end )
    { action.addToSymbolScope(action.defineSymbol(STREAM, #def, #id)); }
  ;

definesubmenustate :#(  def:DEFINE (def_shared)? def_modifiers SUBMENU
      id:ID { push(action.defineSymbol(SUBMENU, #def, #id)); }
      (menu_opt)* (menu_list_item)* state_end
    )
    { action.addToSymbolScope(pop()); }
  ;

definetemptablestate :#(  def:DEFINE (def_shared)? def_modifiers TEMPTABLE id:ID
      { action.defineTemptable(#def, #id); }
      (UNDO|NOUNDO)?
      (namespace_uri)? (namespace_prefix)? (xml_node_name)?
      ( #(SERIALIZENAME QSTRING) )?
      (REFERENCEONLY)?
      (def_table_like)?
      (label_constant)?
      ( #(  BEFORETABLE bt:ID
          { action.defineBuffer(#bt, #bt, #id, false); }
        )
      )?
      (RCODEINFORMATION)?
      (def_table_field)*
      ( #(  INDEX ID ( (AS|IS)? (UNIQUE|PRIMARY|WORDINDEX) )*
          ( ID (ASCENDING|DESCENDING|CASESENSITIVE)* )+
        )
      )*
      state_end
    )
  ;

def_table_like :#(LIKE def_table_like_sub)
  | #(LIKESEQUENTIAL def_table_like_sub)
  ;

def_table_like_sub :rec:tbl[CQ.SYMBOL] (VALIDATE)?
    ( #(USEINDEX ID ((AS|IS) PRIMARY)? ) )*
    { action.defineTableLike(#rec); }
  ;

def_table_field :#( FIELD id:ID
      { push(action.defineTableFieldInitialize(#id)); }
      (fieldoption)*
      { action.defineTableFieldFinalize(pop()); }
    )
  ;

defineworktablestate :#(  def:DEFINE (def_shared)? def_modifiers WORKTABLE id:ID
      { action.defineWorktable(#def, #id); }
      (NOUNDO)? (def_table_like)? (label_constant)? (def_table_field)* state_end
    )
  ;

definevariablestate :#( def:DEFINE (def_shared)? def_modifiers VARIABLE
      id:ID { push(action.defineVariable(#def, #id)); }
      (fieldoption)* (triggerphrase)? state_end
    )
    { action.addToSymbolScope(pop()); }
  ;

varstate
	:	#(VARIABLE (varStateAccessMode)? (varStateOptions)? vardatatype 
		(varStatementSub2)? varStatementSub (COMMA varStatementSub)* state_end )
	;
	
varStateAccessMode
	:	PRIVATE
	|	PUBLIC
	|	PROTECTED
	|	PACKAGEPRIVATE
	|	PACKAGEPROTECTED
	;

varStateOptions
	:	STATIC
	|	SERIALIZABLE
	|	NON_SERIALIZABLE
	;
	
varStatementSub
	:	id:ID (varStatementEqualSub)?
  	;

varStatementEqualSub
	:	#(EQUAL varStatementInitialValue)
  	;

varStatementSub2
	:    LEFTBRACE (NUMBER)? RIGHTBRACE
  	;

varStatementInitialValue
	:   varStatementInitialValueArray 
	|	varStatementInitialValueSub
  	;

varStatementInitialValueArray
	:    LEFTBRACE varStatementInitialValueSub ( COMMA varStatementInitialValueSub )* RIGHTBRACE
  	;
  	
varStatementInitialValueSub:
    TODAY | NOW | TRUE | FALSE | YES | NO | UNKNOWNVALUE | QSTRING | LEXDATE | NUMBER | NULL
  ;
  
vardatatype
	:	CLASS TYPE_NAME 
	| datatype_var 
  ;

deletestate :#(DELETE_KW tbl[CQ.UPDATING] (#(VALIDATE funargs))? (NOERROR_KW)? state_end )
  ;

destructorstate :#( d:DESTRUCTOR 
      {action.structorBegin(#d);}
      (PUBLIC)? TYPE_NAME LEFTPAREN RIGHTPAREN block_colon
      code_block #(END (DESTRUCTOR|METHOD)? ) state_end
      {action.structorEnd(#d);}
    )
  ;

disablestate :#(  head:DISABLE  { action.frameInitializingStatement(#head); }
      (UNLESSHIDDEN)? (#(ALL (#(EXCEPT (fld[CQ.SYMBOL])*))?) | (form_item[CQ.SYMBOL])+)? (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

disabletriggersstate :#(DISABLE TRIGGERS FOR (DUMP|LOAD) OF tbl[CQ.SYMBOL] (ALLOWREPLICATION)? state_end )
  ;

disconnectstate :#(DISCONNECT filenameorvalue (NOERROR_KW)? state_end )
  ;

displaystate :#(  head:DISPLAY  { action.frameInitializingStatement(#head); }
      (stream_name_or_handle)? (UNLESSHIDDEN)? (displaystate_item)*
      (#(EXCEPT (fld1[CQ.SYMBOL])*))? (#(IN_KW WINDOW expression))?
      (display_with)*
      (NOERROR_KW)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

displaystate_item :#( fi:Form_item
      ( skipphrase
      | spacephrase
      | (expression|ID) (aggregatephrase|formatphrase)*
        // Note for the DISPLAY statement, formItem() is called *after* any potential formatphrase '@' phrase.
        { action.formItem(#fi); }
      )
    )
  ;

display_item :#(  fi:Form_item
      ( skipphrase
      | spacephrase
      | (expression|ID)
        // For everything except DISPLAY, the call to formItem() must happen *before* formatphrase (but after fld[]).
        {action.formItem(#fi);}  (aggregatephrase|formatphrase)*
      )
    )
  ;

dynamicnewstate :#( Assign_dynamic_new
      #(  EQUAL
        (widattr | fld[CQ.UPDATING])
        #(dn:DYNAMICNEW expression {action.callBegin(#dn);} parameterlist {action.callEnd();})
      )
      (NOERROR_KW)?
      state_end
    )
  ;

dostate :#( d:DO
      { action.blockBegin(#d);
        action.frameBlockCheck(#d);
      }
      (block_for)? (block_preselect)? (block_opt)* block_colon {action.frameStatementEnd();}
      code_block block_end {action.blockEnd();}
    )
  ;

downstate :#( head:DOWN  { action.frameInitializingStatement(#head); }
      ((stream_name_or_handle (expression)?) | (expression (stream_name_or_handle)?))? (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

emptytemptablestate :#(EMPTY TEMPTABLE tbl[CQ.TEMPTABLESYMBOL] (NOERROR_KW)? state_end )
  ;

enablestate :#( head:ENABLE  { action.frameEnablingStatement(#head); }
      (UNLESSHIDDEN)? (#(ALL (#(EXCEPT (fld[CQ.SYMBOL])*))?) | (form_item[CQ.SYMBOL])+)?
      (#(IN_KW WINDOW expression))? (framephrase)? state_end  { action.frameStatementEnd(); }
    )
  ;

exportstate :#(EXPORT (stream_name_or_handle)? (#(DELIMITER constant))? (display_item)* (#(EXCEPT (fld1[CQ.SYMBOL])*))? (NOLOBS)? state_end )
  ;

extentphrase :#(ex:EXTENT (expression)?)
  ;

extentphrase_def_symbol :#(ex:EXTENT (expression)? {action.defExtent(#ex);} )
  ;

fieldoption :#(as:AS
      ( CLASS TYPE_NAME
      | datatype_field
      )
    )
    {action.defAs(#as);}
  | casesens_or_not
  | color_expr
  | #(COLUMNCODEPAGE expression )
  | #(CONTEXTHELPID expression)
  | #(DECIMALS expression )
  | DROPTARGET
  | extentphrase_def_symbol
  | #(FONT expression)
  | #(FORMAT expression)
  | #(HELP constant)
  | initial_constant
  | label_constant
  | #(li:LIKE fld[CQ.SYMBOL] (VALIDATE)? ) {action.defLike(#li);}
  | #(MOUSEPOINTER expression )
  | NOUNDO
  | viewasphrase
  | TTCODEPAGE
  | xml_data_type
  | xml_node_name
  | xml_node_type
  | #(SERIALIZENAME QSTRING)
  | SERIALIZEHIDDEN
  ;

findstate :#( FIND (findwhich)?
      #(  r:RECORD_NAME
        {action.recordNameNode(#r, CQ.INIT);}
        recordphrase
      )
      (NOWAIT|NOPREFETCH|NOERROR_KW)* state_end
    )
  ;

fixcodepage_pseudfn :#(FIXCODEPAGE LEFTPAREN fld[CQ.SYMBOL] RIGHTPAREN )
  ;

forstate :#(  f:FOR 
      { action.blockBegin(#f); 
        action.frameBlockCheck(#f);
      }
      for_record_spec[CQ.INITWEAK] (block_opt)* block_colon {action.frameStatementEnd();}
      code_block block_end {action.blockEnd();}
    )
  ;

for_record_spec[int contextQualifier] :(findwhich)?
    #(  rp1:RECORD_NAME
      {action.recordNameNode(#rp1, contextQualifier);}
      recordphrase
    )
    ( COMMA (findwhich)?
      #(  rp2:RECORD_NAME
        {action.recordNameNode(#rp2, contextQualifier);}
        recordphrase
      )
    )*
  ;

form_item[int contextQualifier] { int tblQualifier = contextQualifier;
  if (contextQualifier==CQ.SYMBOL) tblQualifier = CQ.BUFFERSYMBOL;
}
:#( fi:Form_item
      ( tbl[tblQualifier]  {action.formItem(#fi);}
      | #(TEXT LEFTPAREN (form_item[contextQualifier])* RIGHTPAREN )
      | constant (formatphrase)?
      | spacephrase
      | skipphrase
      | widget_id
      | CARET
      | // formItem() must be called after fld[], but before formatphrase.
        fld[contextQualifier] {action.formItem(#fi);} (aggregatephrase|formatphrase)*
      | assign_equal
      )
    )
  ;

formstate :#( head:FORMAT  { action.frameInitializingStatement(#head); }
      (form_item[CQ.SYMBOL])*
      ( #(HEADER (display_item)+ )
      | #(BACKGROUND (display_item)+ )
      )?
      (#(EXCEPT (fld1[CQ.SYMBOL])*))?
      (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

formatphrase :#(  Format_phrase
      ( #(AS datatype_var )
      | atphrase
      | ATTRSPACE
      | NOATTRSPACE
      | AUTORETURN
      | color_expr
      | #(CONTEXTHELPID expression)
      | BLANK 
      | #(COLON expression )
      | #(TO expression)
      | DEBLANK 
      | DISABLEAUTOZAP 
      | #(FONT expression ) 
      | #(FORMAT expression)
      | #(HELP constant)
      | label_constant
      | #(LEXAT af:fld[CQ.SYMBOL] {action.lexat(#af);} (formatphrase)? )
      | #(LIKE fld[CQ.SYMBOL] )
      | NOLABELS
      | NOTABSTOP 
      | PASSWORDFIELD
      | #(VALIDATE funargs)
      | #(WHEN expression)
      | viewasphrase 
      )+
    )
  ;

frame_ref :#(FRAME f:ID) { action.frameRef(#f); }
  ;

framephrase :#( WITH
      ( #(ACCUMULATE (expression)? )
      | ATTRSPACE | NOATTRSPACE
      | #(CANCELBUTTON fld[CQ.SYMBOL] )
      | CENTERED 
      | #(COLUMN expression )
      | CONTEXTHELP | CONTEXTHELPFILE expression
      | #(DEFAULTBUTTON fld[CQ.SYMBOL] )
      | EXPORT
      | FITLASTCOLUMN
      | #(FONT expression )
      | FONTBASEDLAYOUT
      | frame_ref
      | INHERITBGCOLOR | NOINHERITBGCOLOR | INHERITFGCOLOR | NOINHERITFGCOLOR
      | #(LABELFONT expression )
      | #(LABELDCOLOR expression )
      | #(LABELFGCOLOR expression )
      | #(LABELBGCOLOR expression )
      | MULTIPLE | SINGLE | SEPARATORS | NOSEPARATORS | NOASSIGN| NOROWMARKERS
      | NOSCROLLBARVERTICAL | SCROLLBARVERTICAL
      | #(ROWHEIGHTCHARS expression )
      | #(ROWHEIGHTPIXELS expression )
      | EXPANDABLE | DROPTARGET | NOAUTOVALIDATE | NOCOLUMNSCROLLING
      | KEEPTABORDER | NOBOX | NOEMPTYSPACE | NOHIDE | NOLABELS | USEDICTEXPS | NOVALIDATE 
      | NOHELP | NOUNDERLINE | OVERLAY | PAGEBOTTOM | PAGETOP | NOTABSTOP
      | #(RETAIN expression  )
      | #(ROW expression )
      | SCREENIO | STREAMIO
      | #(SCROLL expression )
      | SCROLLABLE | SIDELABELS 
      | stream_name_or_handle | THREED
      | tooltip_expr
      | TOPONLY | USETEXT
      | V6FRAME | USEREVVIDEO | USEUNDERLINE
      | #(  VIEWAS
          ( #(DIALOGBOX (DIALOGHELP (expression)?)? )
          | MESSAGELINE
          | STATUSBAR
          | #(TOOLBAR (ATTACHMENT (TOP|BOTTOM|LEFT|RIGHT))? )
          )
        )
      | #(WIDTH expression )
      | #(IN_KW WINDOW expression)
      | colorspecification | atphrase | sizephrase | titlephrase 
      | #(With_columns expression COLUMNS )
      | #(With_down expression DOWN )
      | DOWN
      | widget_id
      | WITH
      )*
    )
  ;

functionstate :#( f:FUNCTION id:ID {action.funcBegin(#f, #id);}
      (RETURNS|RETURN)?
      {action.routineReturnDatatype(_t);}
      ( CLASS TYPE_NAME | datatype_var ) (extentphrase)?
      (PRIVATE)?
      ( function_params )?
      // A function can be FORWARD declared and then later defined IN SUPER.
      ( FORWARDS (LEXCOLON|PERIOD|EOF) {action.funcForward(#id);}
      | (IN_KW SUPER)=> IN_KW SUPER (LEXCOLON|PERIOD|EOF)  {action.funcDef(#f, #id);}
      | (MAP (TO)? ID)? IN_KW expression (LEXCOLON|PERIOD|EOF)  {action.funcDef(#f, #id);}
      | block_colon {action.funcDef(#f, #id);} code_block
        ( EOF
        | #(END (FUNCTION)? ) state_end
        )
      )
    )
    { action.funcEnd(#f); }
  ;

function_param {action.paramForRoutine(_t);}
:(
      #(  b:BUFFER (id:ID)? FOR rec:tbl[CQ.SYMBOL] (PRESELECT)?
        { if (#id!=null) {
            action.defineBuffer(#id, #id, #rec, true);
            action.paramSymbol(#id);
          } else {
            action.paramSymbol(#rec);
          }
          action.paramProgressType(BUFFER);
        }
      )
    | #(INPUT function_param_arg )
    | #(OUTPUT function_param_arg )
    | #(INPUTOUTPUT function_param_arg )
    )
{action.paramEnd();}
  ;

function_param_arg :TABLE (FOR)? tb1:tbl[CQ.TEMPTABLESYMBOL] (APPEND)? (BIND {action.paramBind();})?
    { action.paramProgressType(TEMPTABLE);
      action.paramSymbol(#tb1);
    }
  | TABLEHANDLE (FOR)? id2:ID (APPEND)? (BIND {action.paramBind();})?
    { action.addToSymbolScope(action.defineVariable(#id2, #id2, HANDLE));
      action.paramSymbol(#id2);
    }
  | DATASET (FOR)? ds:ID (APPEND)? (BIND {action.paramBind();})?
    { action.setSymbol(DATASET, #ds);
      action.paramProgressType(DATASET);
      action.paramSymbol(#ds);
    }
  | DATASETHANDLE (FOR)? dsh:ID (APPEND)? (BIND {action.paramBind();})?
    { action.addToSymbolScope(action.defineVariable(#dsh, #dsh, HANDLE));
      action.paramSymbol(#dsh);
    }
  | { action.paramNoName(_t); }
    (CLASS TYPE_NAME | datatype_var) (extentphrase_def_symbol)?
  | // ID AS is optional - you are allowed to list just the datatype.
    id:ID 
    ((as:AS 
    {
    	action.addToSymbolScope(action.defineVariable(#id, #id));
    	action.defAs(#as);
        action.paramSymbol(#id);
    }
    (CLASS TYPE_NAME | datatype_var) (extentphrase_def_symbol)?) 
    
    |
    #(li:LIKE fld[CQ.SYMBOL]) 
    {
        action.addToSymbolScope(action.defineVariable(#id, #id));
        action.defLike(#li);
        action.paramSymbol(#id);
    }
    )
    
  ;

getkeyvaluestate :#(GETKEYVALUE SECTION expression KEY (DEFAULT|expression) VALUE fld[CQ.UPDATING] state_end )
  ;

importstate :#( IMPORT (stream_name_or_handle)?
      ( #(DELIMITER constant) | UNFORMATTED )?
      ( tbl[CQ.UPDATING] (#(EXCEPT (fld1[CQ.SYMBOL])*))?
      | ( fld[CQ.UPDATING] | CARET )+
      )?
      (NOLOBS)? (NOERROR_KW)? state_end
    )
  ;

insertstate :#( head:INSERT  { action.frameInitializingStatement(#head); }
      tbl[CQ.UPDATING] (#(EXCEPT (fld1[CQ.SYMBOL])*))? (#(USING (ROWID|RECID) expression))?
      (framephrase)? (NOERROR_KW)? state_end  { action.frameStatementEnd(); }
    )
  ;

ldbnamefunc :#(LDBNAME LEFTPAREN (#(BUFFER tbl[CQ.BUFFERSYMBOL]) | expression) RIGHTPAREN )
  ;

messagestate :#(  MESSAGE
      ( #(COLOR anyorvalue) )?
      ( #(Form_item (skipphrase | expression) ) )* // No call to formItem() for MESSAGE.
      ( #(  VIEWAS ALERTBOX
          (MESSAGE|QUESTION|INFORMATION|ERROR|WARNING)?
          (BUTTONS (YESNO|YESNOCANCEL|OK|OKCANCEL|RETRYCANCEL) )?
          (#(TITLE expression))?
        )
      | #(SET fld[CQ.UPDATING] (formatphrase)? )
      | #(UPDATE fld[CQ.REFUP] (formatphrase)? )
      )*
      ( #(IN_KW WINDOW expression) )?
      state_end
    )
  ;

methodstate { AST returnTypeNode = null;
}
:#( m:METHOD def_modifiers
      {returnTypeNode = _t;}
      ( VOID
      | datatype ( (extentphrase)=> (extentphrase) | )
      )
      id:.
      { action.methodBegin(#m, #id);
        action.routineReturnDatatype(returnTypeNode);
      }
      function_params
      ( // Ambiguous on PERIOD, since a block_colon may be a period, and we may also
        // be at the end of the method declaration for an INTERFACE.
        // We predicate on the next node being Code_block.
        // (Upper/lowercase matters. Node: Code_block. Rule/branch: code_block.)
        (block_colon Code_block)=> block_colon code_block #(END (METHOD)? ) state_end
      | (PERIOD|LEXCOLON)
      )
      {action.methodEnd(#m);}
    )
  ;

nextpromptstate :#(NEXTPROMPT fld[CQ.SYMBOL] (framephrase)? state_end )
  ;

onstate :#( onNode:ON
      {action.scopeAdd(#onNode);}
      ( (ASSIGN|CREATE|DELETE_KW|FIND|WRITE)=>
        ( (CREATE|DELETE_KW|FIND) OF t1:tbl[CQ.SYMBOL] (label_constant)?
          {action.defineBufferForTrigger(#t1);}
        | WRITE OF rec:tbl[CQ.SYMBOL] (label_constant)?
          ( (NEW (BUFFER)? id1:ID) (label_constant)?
            {action.defineBuffer(#id1, #id1, #rec, true);}
          )?
          {if (#id1 == null) action.defineBufferForTrigger(#rec);}
          ( (OLD (BUFFER)? id2:ID) (label_constant)?
            {action.defineBuffer(#id2, #id2, #rec, true);}
          )? 
        | ASSIGN OF fld:fld[CQ.INIT]
          (#(TABLE LABEL constant))?
          ( OLD (VALUE)?
            id:ID { push(action.defineVariable(#id, #id, #fld)); }
            (options{greedy=true;}:defineparam_var)?
            { action.addToSymbolScope(pop()); }
          )?
        )
        (OVERRIDE)?
        ( REVERT state_end
        | PERSISTENT runstate
        | blockorstate
        )
      | // ON keylabel keyfunction.
        ( . . state_end )=>  . . state_end
      | eventlist
        ( ANYWHERE
        | OF widgetlist
          (OR eventlist OF widgetlist)*
          (ANYWHERE)?
        )
        ( REVERT state_end
        | PERSISTENT RUN filenameorvalue
          ( #(IN_KW expression) )?
          ( #(  Parameter_list
              LEFTPAREN (INPUT)? expression
              (COMMA (INPUT)? expression)*
              RIGHTPAREN
            )
          )?
          state_end
        | blockorstate
        )
      )
      {action.scopeClose(#onNode);}
    )
  ;

openquerystate :#(  OPEN QUERY ID (FOR|PRESELECT) for_record_spec[CQ.INIT]
      ( querytuningphrase
      | BREAK
      | #(BY expression (DESCENDING)? )
      | collatephrase
      | INDEXEDREPOSITION
      | #(MAXROWS expression )
      )*
      state_end
    )
  ;

procedurestate :#(  p:PROCEDURE id:ID
      { action.procedureBegin(#p, #id); }
      ( #(  EXTERNAL constant
          ( CDECL_KW
          | PASCAL_KW
          | STDCALL_KW
          | #(ORDINAL expression )
          | PERSISTENT
          )*
        )
      | PRIVATE
      | IN_KW SUPER
      )?
      block_colon code_block (EOF | #(END (PROCEDURE)?) state_end)
      { action.procedureEnd(#p); }
    )
  ;

promptforstate :#(  head:PROMPTFOR  { action.frameEnablingStatement(#head); }
      (stream_name_or_handle)? (UNLESSHIDDEN)? (form_item[CQ.SYMBOL])*
      (goonphrase)?  (#(EXCEPT (fld1[CQ.SYMBOL])*))?  (#(IN_KW WINDOW expression))?
      (framephrase)?  { action.frameStatementEnd(); }
      (editingphrase)? state_end
    )
  ;

publishstate :#(  pu:PUBLISH expression (#(FROM expression) )?
      {action.callBegin(#pu);}
      (parameterlist)?
      state_end
      {action.callEnd();}
    )
  ;

rawtransferstate :#(RAWTRANSFER (BUFFER|FIELD)? (tbl[CQ.REF]|fld[CQ.REF]) TO (BUFFER|FIELD)? (tbl[CQ.UPDATING]|fld[CQ.UPDATING]) (NOERROR_KW)? state_end )
  ;

record_fields :#(FIELDS (LEFTPAREN (fld1[CQ.SYMBOL] (#(WHEN expression))?)* RIGHTPAREN)? )
  | #(EXCEPT (LEFTPAREN (fld1[CQ.SYMBOL] (#(WHEN expression))?)* RIGHTPAREN)? )
  ;

recordphrase :(record_fields)? (options{greedy=true;}:TODAY|NOW|constant)?
    ( #(LEFT OUTERJOIN )
    | OUTERJOIN
    | #(OF tbl[CQ.REF] )
    | #(WHERE (expression)? )
    | #(TENANT_WHERE expression (SKIP_GROUP_DUPLICATES)?)
    | #(USEINDEX ID )
    | #(USING fld1[CQ.SYMBOL] (AND fld1[CQ.SYMBOL])* )
    | lockhow
    | NOWAIT
    | NOPREFETCH
    | NOERROR_KW
    | TABLESCAN
    )*
  ;

releasestate :#(RELEASE tbl[CQ.REF] (NOERROR_KW)? state_end )
  ;

repeatstate :#( r:REPEAT
      { action.blockBegin(#r);
        action.frameBlockCheck(#r);
      }
      (block_for)? (block_preselect)? (block_opt)* block_colon {action.frameStatementEnd();}
      code_block block_end {action.blockEnd();}
    )
  ;

runstate :#(  r:RUN filenameorvalue { action.runBegin(#r); } 
      (LEFTANGLE LEFTANGLE filenameorvalue RIGHTANGLE RIGHTANGLE)?
      ( #(PERSISTENT ( #(SET (hnd:fld[CQ.UPDATING] { action.runPersistentSet(#hnd); } )? ) )? )
      | #(SET (fld[CQ.UPDATING])? )
      | #(ON (SERVER)? expression (TRANSACTION (DISTINCT)?)? )
      | #(IN_KW hexp:expression) { action.runInHandle(#hexp); } 
      | #(  ASYNCHRONOUS ( #(SET (fld[CQ.UPDATING])? ) )?
          (#(EVENTPROCEDURE expression ) )?
          (#(IN_KW expression))?
          (#(EVENT_HANDLER expression ) )?
          (#(EVENT_HANDLER_CONTEXT expression))?
        )
      )*
      (parameterlist)?
      (NOERROR_KW|anyorvalue)*
      state_end
      { action.runEnd(#r); }
    )
  ;

runstoredprocedurestate :#( r:RUN STOREDPROCEDURE ID (assign_equal)? (NOERROR_KW)?
      {action.callBegin(#r);}
      (parameterlist)?
      state_end
      {action.callEnd();}
    )
  ;

runsuperstate :#(r:RUN {action.callBegin(#r);} SUPER (parameterlist)? (NOERROR_KW)? state_end {action.callEnd();} )
  ;

scrollstate :#( head:SCROLL  { action.frameInitializingStatement(#head); }
      (FROMCURRENT)? (UP)? (DOWN)? (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

setstate :#(  head:SET  { action.frameInitializingStatement(#head); }
      (stream_name_or_handle)? (UNLESSHIDDEN)?
      (form_item[CQ.UPDATING])*
      (goonphrase)?  (#(EXCEPT (fld1[CQ.SYMBOL])*))?  (#(IN_KW WINDOW expression))?
      (framephrase)?  { action.frameStatementEnd(); }
      (editingphrase)? (NOERROR_KW)? state_end  
    )
  ;

systemdialogcolorstate :#(SYSTEMDIALOG COLOR expression ( #(UPDATE fld[CQ.UPDATING]) )? (#(IN_KW WINDOW expression))? state_end )
  ;

systemdialogfontstate :#( SYSTEMDIALOG FONT expression
      ( ANSIONLY
      | FIXEDONLY
      | #(MAXSIZE expression )
      | #(MINSIZE expression )
      | #(UPDATE fld[CQ.UPDATING] )
      | #(IN_KW WINDOW expression)
      )*
      state_end
    )
  ;

systemdialoggetdirstate :#( SYSTEMDIALOG GETDIR fld[CQ.REFUP]
      ( #(INITIALDIR expression)
      | RETURNTOSTARTDIR
      | #(TITLE expression)
      | #(UPDATE fld[CQ.REFUP])
      )*
      state_end
    )
  ;

systemdialoggetfilestate :#(  SYSTEMDIALOG GETFILE fld[CQ.REFUP]
      ( #(  FILTERS expression expression (COMMA expression expression)*
          ( #(INITIALFILTER expression ) )?
        )
      | ASKOVERWRITE
      | CREATETESTFILE
      | #(DEFAULTEXTENSION expression )
      | #(INITIALDIR expression )
      | MUSTEXIST
      | RETURNTOSTARTDIR
      | SAVEAS
      | #(TITLE expression)
      | USEFILENAME
      | #(UPDATE fld[CQ.UPDATING] )
      | #(IN_KW WINDOW expression)
      )*
      state_end
    )
  ;

systemdialogprintersetupstate :#( SYSTEMDIALOG PRINTERSETUP
      ( #(NUMCOPIES expression) | #(UPDATE fld[CQ.UPDATING]) | LANDSCAPE | PORTRAIT | #(IN_KW WINDOW expression) )*
      state_end
    )
  ;

thisobjectstate :#(to:THISOBJECT {action.callBegin(#to);} parameterlist_noroot state_end {action.callEnd();} )
  ;

triggerphrase :#( TRIGGERS block_colon
      #(  Code_block
        ( #(  on:ON {action.scopeAdd(#on);}
            eventlist (ANYWHERE)?
            (PERSISTENT runstate | blockorstate)
            {action.scopeClose(#on);}
          ) 
        )*
      )
      #(END (TRIGGERS)? )
    )
  ;

triggerprocedurestate :#( TRIGGER PROCEDURE FOR
      ( (CREATE|DELETE_KW|FIND|REPLICATIONCREATE|REPLICATIONDELETE)
        OF t1:tbl[CQ.SYMBOL] (label_constant)?
        {action.defineBufferForTrigger(#t1);}
      | (WRITE|REPLICATIONWRITE) OF rec:tbl[CQ.SYMBOL] (label_constant)?
        ( NEW (BUFFER)? id4:ID (label_constant)?
          {action.defineBuffer(#id4, #id4, #rec, true);}
        )?
        {if (#id4 == null) action.defineBufferForTrigger(#rec);}
        ( OLD (BUFFER)? id3:ID (label_constant)? 
          {action.defineBuffer(#id3, #id3, #rec, true);}
        )?
      | ASSIGN
        ( #(OF fld[CQ.SYMBOL] (#(TABLE LABEL constant))? )
        | #(  NEW (VALUE)?
            id:ID { push(action.defineVariable(#id, #id)); }
            defineparam_var
            { action.addToSymbolScope(pop()); }
          )
          
        )? 
        ( #(  OLD (VALUE)?
            id2:ID { push(action.defineVariable(#id2, #id2)); }
            defineparam_var
          )
          { action.addToSymbolScope(pop()); }
        )?
      )
      state_end
    )
  ;

underlinestate :#(  head:UNDERLINE  { action.frameInitializingStatement(#head); }
      (stream_name_or_handle)? (#(fi:Form_item fld[CQ.SYMBOL] {action.formItem(#fi);} (formatphrase)? ))* (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

upstate :#( head:UP  { action.frameInitializingStatement(#head); }
      (options{greedy=true;}:stream_name_or_handle)? (expression)? (stream_name_or_handle)? (framephrase)?
      state_end  { action.frameStatementEnd(); }
    )
  ;

updatestatement :(#(UPDATE tbl[CQ.SYMBOL] SET))=> sqlupdatestate
  | updatestate
  ;

updatestate :#( head:UPDATE  { action.frameEnablingStatement(#head); }
      (UNLESSHIDDEN)? 
      (form_item[CQ.REFUP])*
      (goonphrase)?
      (#(EXCEPT (fld1[CQ.SYMBOL])*))?
      (#(IN_KW WINDOW expression))?
      (framephrase)?  { action.frameStatementEnd(); }
      (editingphrase)? (NOERROR_KW)? state_end
    )
  ;

validatestate :#(VALIDATE tbl[CQ.REF] (NOERROR_KW)? state_end )
  ;

viewstate :#(v:VIEW (stream_name_or_handle)? (gwidget)* (#(IN_KW WINDOW expression))? state_end {action.viewState(#v);} )
  ;

altertablestate :#( ALTER TABLE tbl[CQ.SCHEMATABLESYMBOL]
      ( ADD COLUMN sql_col_def
      | DROP COLUMN fld[CQ.SYMBOL]
      | ALTER COLUMN fld[CQ.SYMBOL]
        (   #(FORMAT expression)
        | label_constant
            | #(DEFAULT expression )
        |   casesens_or_not
          )*
      )
      state_end
    )
  ;

createindexstate :#(CREATE (UNIQUE)? INDEX ID ON tbl[CQ.SCHEMATABLESYMBOL] #(Field_list LEFTPAREN fld[CQ.SYMBOL] (COMMA fld[CQ.SYMBOL])* RIGHTPAREN ) state_end )
  ;

createviewstate :#(CREATE VIEW ID (#(Field_list LEFTPAREN fld[CQ.SYMBOL] (COMMA fld[CQ.SYMBOL])* RIGHTPAREN ))? AS selectstatea state_end )
  ;

deletefromstate :#( DELETE_KW FROM tbl[CQ.SCHEMATABLESYMBOL]
      ( #(WHERE (sqlexpression | #(CURRENT OF ID))? ) )?
      state_end
    )
  ;

droptablestate :#(DROP TABLE tbl[CQ.SCHEMATABLESYMBOL] state_end )
  ;

fetchstate :#(FETCH ID INTO fld[CQ.UPDATING] (fetch_indicator)? (COMMA fld[CQ.UPDATING] (fetch_indicator)? )* state_end )
  ;

fetch_indicator :#(INDICATOR fld[CQ.UPDATING] )
  | fld[CQ.UPDATING]
  ;

grantstate :#(GRANT (grant_rev_opt) ON (tbl[CQ.SCHEMATABLESYMBOL]|ID) grant_rev_to (WITH GRANT OPTION)? state_end )
  ;

grant_rev_opt :#(ALL (PRIVILEGES)? )
  | ( SELECT | INSERT | DELETE_KW
    | #(UPDATE (#(Field_list LEFTPAREN fld[CQ.UPDATING] (COMMA fld[CQ.UPDATING])* RIGHTPAREN ))? )
    | COMMA
    )+
  ;

insertintostate :#( INSERT INTO tbl[CQ.SCHEMATABLESYMBOL]
      (#(Field_list LEFTPAREN fld[CQ.UPDATING] (COMMA fld[CQ.UPDATING])* RIGHTPAREN ))?
      ( #(  VALUES LEFTPAREN sqlexpression (fetch_indicator)?
          (COMMA sqlexpression (fetch_indicator)?)* RIGHTPAREN
        )
      | selectstatea
      )
      state_end
    )
  ;

revokestate :#(REVOKE (grant_rev_opt) ON (tbl[CQ.SCHEMATABLESYMBOL]|ID) grant_rev_to state_end )
  ;

selectstate :{ action.frameInitializingStatement(selectstate_AST_in); }
    selectstatea state_end
    { action.frameStatementEnd(); }
  ;

selectstatea :#(  SELECT
      (ALL | DISTINCT)?
      ( STAR
      | #(  Sql_select_what
          ( (LEFTPAREN)=> LEFTPAREN sqlexpression (formatphrase)? RIGHTPAREN (formatphrase)?
          | sqlexpression (formatphrase)?
          )
          (COMMA sqlexpression (formatphrase)?)*
        )
      )
      ( #(INTO fld[CQ.UPDATING] (fetch_indicator)? (COMMA fld[CQ.UPDATING] (fetch_indicator)?)* ) )?
      #(FROM select_from_spec (COMMA select_from_spec)* )
      ( #(GROUP BY expression (COMMA expression)* ) )?
      ( #(HAVING sqlexpression) )?
      ( #(ORDER BY select_order_expr )
      | #(BY select_order_expr )
      )?
      // Ick. I had trouble convincing antlr not to check the syntactic predicate
      // if next token _t was null.
      ( {_t != null}? ( ( #(WITH CHECK OPTION ) )=>{_t != null}? #(WITH CHECK OPTION ) | )
      | // empty alt
      )
      (framephrase)?
      ( #(UNION (ALL)? selectstatea) )?
    )
  ;

select_sqltableref :(tbl[CQ.SCHEMATABLESYMBOL] | ID) (ID)?
  ;

sqlupdatestate :#(  UPDATE tbl[CQ.SCHEMATABLESYMBOL] SET sqlupdate_equal (COMMA sqlupdate_equal)*
      ( #(WHERE (sqlexpression | CURRENT OF ID) ) )?
      state_end
    )
  ;

sqlupdate_equal :#(EQUAL fld[CQ.REF] sqlexpression (fetch_indicator)? )
  ;

sqlaggregatefunc :#(AVG sqlaggregatefunc_arg )
  | #(COUNT sqlaggregatefunc_arg )
  | #(SUM sqlaggregatefunc_arg )
  ;

sqlaggregatefunc_arg :LEFTPAREN
    ( DISTINCT
      ( LEFTPAREN fld[CQ.REF] RIGHTPAREN
      | fld[CQ.REF]
      )
    | STAR
    | (ALL)? sqlscalar
    )
    RIGHTPAREN
  ;

sql_in_val :fld[CQ.REF] (fetch_indicator)? | constant | USERID
  ;

// inherited from grammar JPTreeParser
code_block :#(Code_block (blockorstate)* )
  ;

// inherited from grammar JPTreeParser
blockorstate :( labeled_block
    | statement
    | // Expr_statement has a "statehead" node attribute
      #(Expr_statement expression (NOERROR_KW)? state_end)
    | PROPARSEDIRECTIVE
    | PERIOD
    | DOT_COMMENT
    | #(ANNOTATION (.)* )
    )
  ;

// inherited from grammar JPTreeParser
labeled_block :#(BLOCK_LABEL LEXCOLON (dostate|forstate|repeatstate) )
  ;

// inherited from grammar JPTreeParser
block_colon :LEXCOLON | PERIOD
  ;

// inherited from grammar JPTreeParser
block_end :EOF
  | END state_end
  ;

// inherited from grammar JPTreeParser
statement :aatracestatement
  |           accumulatestate
  |           altertablestate
  |           analyzestate
  |           applystate
  |           assignstate
  |           bellstate
  |           buffercomparestate
  |           buffercopystate
  |           callstate
  |           casestate
  |           catchstate
  |           choosestate
  |           enumstate
  |           classstate
  |           clearstate
  | {state2(_t, 0)}?      closestate      // SQL
  | {state2(_t, QUERY)}?      closequerystate
  | {state2(_t, STOREDPROCEDURE)}?  closestoredprocedurestate
  |           colorstate
  |           compilestate
  |           connectstate
  |           constructorstate
  |           copylobstate
  | {state2(_t, 0)}?      createstate
  | {state2(_t, ALIAS)}?      createaliasstate
  | {state2(_t, Automationobject)}? createautomationobjectstate
  | {state2(_t, BROWSE)}?     createbrowsestate
  | {state2(_t, BUFFER)}?     createbufferstate
  | {state2(_t, CALL)}?     createcallstate
  | {state2(_t, CLIENTPRINCIPAL)}? createclientprincipalstate
  | {state2(_t, DATABASE)}?   createdatabasestate
  | {state2(_t, DATASET)}?      createdatasetstate
  | {state2(_t, DATASOURCE)}?   createdatasourcestate
  | {state2(_t, INDEX)}?      createindexstate    // SQL
  | {state2(_t, QUERY)}?      createquerystate   
  | {state2(_t, SAXREADER)}?    createsaxreaderstate
  | {state2(_t, SAXWRITER)}?    createsaxwriterstate
  | {state2(_t, SERVER)}?     createserverstate
  | {state2(_t, SERVERSOCKET)}?   createserversocketstate
  | {state2(_t, SOAPHEADER)}?   createsoapheaderstate
  | {state2(_t, SOAPHEADERENTRYREF)}? createsoapheaderentryrefstate
  | {state2(_t, SOCKET)}?     createsocketstate
  | {state2(_t, TABLE)}?      createtablestate    // SQL
  | {state2(_t, TEMPTABLE)}?    createtemptablestate
  | {state2(_t, VIEW)}?     createviewstate     // SQL
  | {state2(_t, WIDGET)}?     createwidgetstate
  | {state2(_t, WIDGETPOOL)}?   createwidgetpoolstate
  | {state2(_t, XDOCUMENT)}?    createxdocumentstate
  | {state2(_t, XNODEREF)}?   createxnoderefstate
  | {state2(_t, ADVISE)}?     ddeadvisestate
  | {state2(_t, EXECUTE)}?    ddeexecutestate
  | {state2(_t, GET)}?      ddegetstate
  | {state2(_t, INITIATE)}?   ddeinitiatestate
  | {state2(_t, REQUEST)}?    dderequeststate
  | {state2(_t, SEND)}?     ddesendstate
  | {state2(_t, TERMINATE)}?    ddeterminatestate 
  |           declarecursorstate
  | {state2(_t, BROWSE)}?     definebrowsestate
  | {state2(_t, BUFFER)}?     definebufferstate
  | {state2(_t, BUTTON)}?     definebuttonstate
  | {state2(_t, DATASET)}?      definedatasetstate
  | {state2(_t, DATASOURCE)}?   definedatasourcestate
  | {state2(_t, EVENT)}?      defineeventstate
  | {state2(_t, FRAME)}?      defineframestate
  | {state2(_t, IMAGE)}?      defineimagestate
  | {state2(_t, MENU)}?     definemenustate
  | {state2(_t, PARAMETER)}?    defineparameterstate
  | {state2(_t, PROPERTY)}?   definepropertystate
  | {state2(_t, QUERY)}?      definequerystate
  | {state2(_t, RECTANGLE)}?    definerectanglestate
  | {state2(_t, STREAM)}?     definestreamstate
  | {state2(_t, SUBMENU)}?    definesubmenustate
  | {state2(_t, TEMPTABLE)}?    definetemptablestate
  | {state2(_t, WORKTABLE)}?    defineworktablestate
  | {state2(_t, VARIABLE)}?   definevariablestate
  |           dictionarystate
  | {state2(_t, 0)}?      deletestate
  | {state2(_t, ALIAS)}?      deletealiasstate
  | {state2(_t, FROM)}?     deletefromstate
  | {state2(_t, OBJECT)}?     deleteobjectstate
  | {state2(_t, PROCEDURE)}?    deleteprocedurestate
  | {state2(_t, WIDGET)}?     deletewidgetstate
  | {state2(_t, WIDGETPOOL)}?   deletewidgetpoolstate
  |           destructorstate
  | {state2(_t, 0)}?      disablestate
  | {state2(_t, TRIGGERS)}?   disabletriggersstate
  |           disconnectstate
  |           displaystate
  |           dostate
  |           downstate
  | {state2(_t, INDEX)}?      dropindexstate      // SQL
  | {state2(_t, TABLE)}?      droptablestate      // SQL
  | {state2(_t, VIEW)}?     dropviewstate     // SQL
  |           dynamicnewstate
  |           emptytemptablestate  
  |           enablestate
  |           exportstate
  |           fetchstate
  |           finallystate
  |           findstate
  |           forstate
  |           formstate
  |           functionstate
  |           getstate
  |           getkeyvaluestate  
  |           grantstate
  |           hidestate
  |           ifstate
  |           importstate  
  | {state2(_t, CLEAR)}?      inputclearstate
  | {state2(_t, CLOSE)}?      inputclosestate
  | {state2(_t, FROM)}?     inputfromstate
  | {state2(_t, THROUGH)}?    inputthroughstate
  | {state2(_t, CLOSE)}?      inputoutputclosestate
  | {state2(_t, THROUGH)}?    inputoutputthroughstate
  | {state2(_t, INTO)}?     insertintostate     // SQL
  | {state2(_t, 0)}?      insertstate
  |           interfacestate
  |           leavestate
  |           loadstate  
  |           messagestate
  |           methodstate
  |           nextstate
  |           nextpromptstate
  |           onstate  
  | {state2(_t, 0)}?      openstate     // SQL
  | {state2(_t, QUERY)}?      openquerystate
  |           osappendstate
  |           oscommandstate
  |           oscopystate
  |           oscreatedirstate  
  |           osdeletestate
  |           osrenamestate
  | {state2(_t, CLOSE)}?      outputclosestate
  | {state2(_t, THROUGH)}?    outputthroughstate
  | {state2(_t, TO)}?     outputtostate
  |           pagestate  
  |           pausestate
  |           procedurestate
  |           processeventsstate
  |           promptforstate
  |           publishstate
  | {state2(_t, 0)}?      putstate
  | {state2(_t, CURSOR)}?     putcursorstate
  | {state2(_t, SCREEN)}?     putscreenstate
  |           putkeyvaluestate
  |           quitstate
  |           rawtransferstate
  |           readkeystate
  | {state2(_t, 0)}?      releasestate
  | {state2(_t, EXTERNAL)}?   releaseexternalstate
  | {state2(_t, OBJECT)}?     releaseobjectstate
  |           repeatstate
  |           repositionstate  
  |           returnstate
  |           revokestate
  |           routinelevelstate
    |                       blocklevelstate
  | {state2(_t, 0)}?      runstate
  | {state2(_t, STOREDPROCEDURE)}?  runstoredprocedurestate
  | {state2(_t, SUPER)}?      runsuperstate
  |           savecachestate
  |           scrollstate
  |           seekstate  
  |           selectstate
  |           setstate
  |           showstatsstate
  |           statusstate  
  |           stopstate
  |           subscribestate
  | {state2(_t, COLOR)}?      systemdialogcolorstate
  | {state2(_t, FONT)}?     systemdialogfontstate
  | {state2(_t, GETDIR)}?   systemdialoggetdirstate
  | {state2(_t, GETFILE)}?    systemdialoggetfilestate
  | {state2(_t, PRINTERSETUP)}?   systemdialogprintersetupstate
  |           systemhelpstate
  |           thisobjectstate
  |           transactionmodeautomaticstate
  |           triggerprocedurestate
  |           underlinestate  
  |           undostate
  |           unloadstate
  |           unsubscribestate
  |           upstate  
  |           updatestatement
  |           usestate
  |           usingstate
  |           validatestate
  |			  varstate
  |           viewstate
  |           waitforstate
  ;

// inherited from grammar JPTreeParser
pseudfn :#(EXTENT funargs )
  | #(FIXCODEPAGE funargs )
  | #(OVERLAY funargs )
  | #(PUTBITS funargs )
  | #(PUTBYTE funargs )
  | #(PUTBYTES funargs )
  | #(PUTDOUBLE funargs )
  | #(PUTFLOAT funargs )
  | #(PUTINT64 funargs )
  | #(PUTLONG funargs )
  | #(PUTSHORT funargs )
  | #(PUTSTRING funargs )
  | #(PUTUNSIGNEDLONG funargs )
  | #(PUTUNSIGNEDSHORT funargs )
  | #(SETBYTEORDER funargs )
  | #(SETPOINTERVALUE funargs )
  | #(SETSIZE funargs )
  | AAMSG // not the whole func - we don't want its arguments here
  | currentvaluefunc
  | CURRENTWINDOW
  | dynamiccurrentvaluefunc
  | entryfunc
  | lengthfunc
  | nextvaluefunc
  | rawfunc
  | substringfunc
  | widattr
  // Keywords from <optargfn> and <noargfn>. Assignments to those
  // are accepted by the compiler, however, assignment to them seems to have
  // no affect at runtime.
  // The following are from <optargfn>
  | PAGESIZE_KW | LINECOUNTER | PAGENUMBER | FRAMECOL
  | FRAMEDOWN | FRAMELINE | FRAMEROW | USERID | ETIME_KW
  // The following are from <noargfn>
  | DBNAME | TIME | OPSYS | RETRY | AASERIAL | AACONTROL
  | MESSAGELINES | TERMINAL | PROPATH | CURRENTLANGUAGE | PROMSGS
  | SCREENLINES | LASTKEY
  | FRAMEFIELD | FRAMEFILE | FRAMEVALUE | GOPENDING
  | PROGRESS | FRAMEINDEX | FRAMEDB | FRAMENAME | DATASERVERS
  | NUMDBS | NUMALIASES | ISATTRSPACE | PROCSTATUS
  | PROCHANDLE | CURSOR | OSERROR | RETURNVALUE | OSDRIVES
  | PROVERSION | TRANSACTION | MACHINECLASS 
  | AAPCONTROL | GETCODEPAGES | COMSELF
  ;

// inherited from grammar JPTreeParser
argfunc :#(AACBIT funargs )
  | #(AAMSG funargs )
  | #(ABSOLUTE funargs )
  | #(ALIAS funargs )
  | #(ASC funargs )
  | #(BASE64DECODE funargs )
  | #(BASE64ENCODE funargs )
  | #(BOX funargs )
  | #(CANDO funargs )
  | #(CANQUERY funargs )
  | #(CANSET funargs )
  | #(CAPS funargs )
  | #(CHR funargs )
  | #(CODEPAGECONVERT funargs )
  | #(COLLATE funargs ) // See docs for BY phrase in FOR, PRESELECT, etc.
  | #(COMPARE funargs )
  | #(CONNECTED funargs )
  | #(COUNTOF funargs )
  | #(CURRENTRESULTROW funargs )
  | #(DATE funargs )
  | #(DATETIME funargs )
  | #(DATETIMETZ funargs )
  | #(DAY funargs )
  | #(DBCODEPAGE funargs )
  | #(DBCOLLATION funargs )
  | #(DBPARAM funargs )
  | #(DBREMOTEHOST funargs )
  | #(DBRESTRICTIONS funargs )
  | #(DBTASKID funargs )
  | #(DBTYPE funargs )
  | #(DBVERSION funargs )
  | #(DECIMAL funargs )
  | #(DECRYPT funargs )
  | #(DYNAMICCAST funargs )
  | #(DYNAMICNEXTVALUE funargs )
  | #(ENCODE funargs )
  | #(ENCRYPT funargs )
  | #(EXP funargs )
  | #(FILL funargs )
  | #(FIRST funargs )
  | #(FIRSTOF funargs )
  | #(GENERATEPBEKEY funargs )
  | #(GETBITS funargs )
  | #(GETBYTE funargs )
  | #(GETBYTEORDER funargs )
  | #(GETBYTES funargs )
  | #(GETCOLLATIONS funargs )
  | #(GETDOUBLE funargs )
  | #(GETFLOAT funargs )
  | #(GETINT64 funargs )
  | #(GETLICENSE funargs )
  | #(GETLONG funargs )
  | #(GETPOINTERVALUE funargs )
  | #(GETSHORT funargs )
  | #(GETSIZE funargs )
  | #(GETSTRING funargs )
  | #(GETUNSIGNEDLONG funargs )
  | #(GETUNSIGNEDSHORT funargs )
  | #(HANDLE funargs )
  | #(HEXDECODE funargs )
  | #(HEXENCODE funargs )
  | #(INDEX funargs )
  | #(INT64 funargs )
  | #(INTEGER funargs )
  | #(INTERVAL funargs )
  | #(ISCODEPAGEFIXED funargs )
  | #(ISCOLUMNCODEPAGE funargs )
  | #(ISLEADBYTE funargs )
  | #(ISODATE funargs )
  | #(KBLABEL funargs )
  | #(KEYCODE funargs )
  | #(KEYFUNCTION funargs )
  | #(KEYLABEL funargs )
  | #(KEYWORD funargs )
  | #(KEYWORDALL funargs )
  | #(LAST funargs )
  | #(LASTOF funargs )
  | #(LC funargs )
  | #(LEFTTRIM funargs )
  | #(LIBRARY funargs )
  | #(LISTEVENTS funargs )
  | #(LISTQUERYATTRS funargs )
  | #(LISTSETATTRS funargs )
  | #(LISTWIDGETS funargs )
  | #(LOADPICTURE funargs )
  | #(LOG funargs )
  | #(LOGICAL funargs )
  | #(LOOKUP funargs )
  | #(MAXIMUM funargs )
  | #(MD5DIGEST funargs )
  | #(MEMBER funargs )
  | #(MESSAGEDIGEST funargs )
  | #(MINIMUM funargs )
  | #(MONTH funargs )
  | #(NORMALIZE funargs )
  | #(NUMENTRIES funargs )
  | #(NUMRESULTS funargs )
  | #(OSGETENV funargs )
  | #(PDBNAME funargs )
  | #(PROGRAMNAME funargs )
  | #(QUERYOFFEND funargs )
  | #(QUOTER funargs )
  | #(RINDEX funargs )
  | #(RANDOM funargs )
  | #(REPLACE funargs )
  | #(RGBVALUE funargs )
  | #(RIGHTTRIM funargs )
  | #(ROUND funargs )
  | #(SDBNAME funargs )
  | #(SEARCH funargs )
  | #(SETDBCLIENT funargs )
  | #(SETUSERID funargs )
  | #(SHA1DIGEST funargs )
  | #(SQRT funargs )
  | #(SSLSERVERNAME funargs )
  | #(STRING funargs )
  | #(SUBSTITUTE funargs )
  | #(TENANT_NAME_TO_ID funargs )
  | #(TOROWID funargs )
  | #(TRIM funargs )
  | #(TRUNCATE funargs )
  | #(UNBOX funargs )
  | #(VALIDEVENT funargs )
  | #(VALIDHANDLE funargs )
  | #(VALIDOBJECT funargs )
  | #(WEEKDAY funargs )
  | #(WIDGETHANDLE funargs )
  | #(YEAR funargs )

  ;

// inherited from grammar JPTreeParser
recordfunc :#(AMBIGUOUS recordfunargs )
  | #(AVAILABLE recordfunargs )
  | #(CURRENTCHANGED recordfunargs )
  | #(DATASOURCEMODIFIED recordfunargs )
  | #(ERROR recordfunargs )
  | #(LOCKED recordfunargs )
  | #(NEW recordfunargs )
  | #(RECID recordfunargs )
  | #(RECORDLENGTH recordfunargs )
  | #(REJECTED recordfunargs )
  | #(ROWID recordfunargs )
  | #(ROWSTATE recordfunargs )
  ;

// inherited from grammar JPTreeParser
noargfunc :AACONTROL
  | AAPCONTROL
  | AASERIAL
  | CURRENTLANGUAGE
  | CURSOR
  | DATASERVERS
  | DBNAME
  | FRAMEDB
  | FRAMEFIELD
  | FRAMEFILE
  | FRAMEINDEX
  | FRAMENAME
  | FRAMEVALUE
  | GENERATEPBESALT
  | GENERATERANDOMKEY
  | GENERATEUUID
  | GETCODEPAGES
  | GATEWAYS
  | GOPENDING
  | ISATTRSPACE
  | LASTKEY
  | MACHINECLASS
  | MESSAGELINES
  | NOW
  | NUMALIASES
  | NUMDBS
  | OPSYS
  | OSDRIVES
  | OSERROR
  | PROCHANDLE
  | PROCSTATUS
  | PROGRESS
  | PROMSGS
  | PROPATH
  | PROVERSION
  | RETRY
  | RETURNVALUE
  | SCREENLINES
  | TERMINAL
  | TIME
  | TODAY
  | TRANSACTION
  ;

// inherited from grammar JPTreeParser
parameterlist :#(Parameter_list parameterlist_noroot )
  ;

// inherited from grammar JPTreeParser
parameterlist_noroot :LEFTPAREN (parameter)? (COMMA parameter)* RIGHTPAREN
  ;

// inherited from grammar JPTreeParser
eventlist :#(Event_list . (COMMA .)* )
  ;

// inherited from grammar JPTreeParser
funargs :LEFTPAREN expression (COMMA expression)* RIGHTPAREN
  ;

// inherited from grammar JPTreeParser
anyorvalue :#(VALUE LEFTPAREN expression RIGHTPAREN )
  | TYPELESS_TOKEN
  ;

// inherited from grammar JPTreeParser
valueexpression :#(VALUE LEFTPAREN expression RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
expressionorvalue :valueexpression | expression
  ;

// inherited from grammar JPTreeParser
findwhich :CURRENT | EACH | FIRST | LAST | NEXT | PREV
  ;

// inherited from grammar JPTreeParser
lockhow :SHARELOCK | EXCLUSIVELOCK | NOLOCK
  ;

// inherited from grammar JPTreeParser
expression :#(OR expression expression )
  | #(AND expression expression )
  | #(NOT expression )
  | #(MATCHES expression expression )
  | #(BEGINS expression expression )
  | #(CONTAINS expression expression )
  | #(EQ expression expression )
  | #(NE expression expression )
  | #(GTHAN expression expression )
  | #(GE expression expression )
  | #(LTHAN expression expression )
  | #(LE expression expression )
  | #(PLUS expression expression )
  | #(MINUS expression expression )
  | #(MULTIPLY expression expression )
  | #(DIVIDE expression expression )
  | #(MODULO expression expression )
  | #(UNARY_MINUS exprt )
  | #(UNARY_PLUS exprt )
  | exprt
  ;

// inherited from grammar JPTreeParser
widgetlist :gwidget (COMMA gwidget)*
  ;

// inherited from grammar JPTreeParser
field :#(Field_ref (INPUT)? (#(FRAME ID) | #(BROWSE ID))? ID (array_subscript)? )
  ;

// inherited from grammar JPTreeParser
array_subscript :#(Array_subscript LEFTBRACE expression (FOR expression)? RIGHTBRACE )
  ;

// inherited from grammar JPTreeParser
method_param_list :#(Method_param_list LEFTPAREN (parameter)? (COMMA (parameter)?)* RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
constant :TRUE_KW | FALSE_KW | YES | NO | UNKNOWNVALUE | QSTRING | LEXDATE | NUMBER | NULL_KW
  | NOWAIT | SHARELOCK | EXCLUSIVELOCK | NOLOCK
  | BIGENDIAN
  | FINDCASESENSITIVE | FINDGLOBAL | FINDNEXTOCCURRENCE | FINDPREVOCCURRENCE | FINDSELECT | FINDWRAPAROUND
  | FUNCTIONCALLTYPE | GETATTRCALLTYPE | PROCEDURECALLTYPE | SETATTRCALLTYPE
  | HOSTBYTEORDER | LITTLEENDIAN
  | READAVAILABLE | READEXACTNUM
  | ROWUNMODIFIED | ROWDELETED | ROWMODIFIED | ROWCREATED
  | SAXCOMPLETE | SAXPARSERERROR | SAXRUNNING | SAXUNINITIALIZED
  | SEARCHSELF | SEARCHTARGET
  | WINDOWDELAYEDMINIMIZE | WINDOWMINIMIZED | WINDOWNORMAL | WINDOWMAXIMIZED
  ;

// inherited from grammar JPTreeParser
systemhandlename :AAMEMORY | ACTIVEFORM | ACTIVEWINDOW | AUDITCONTROL | AUDITPOLICY | CLIPBOARD | CODEBASELOCATOR | COLORTABLE | COMPILER 
  | COMSELF | CURRENTWINDOW | DEBUGGER | DEFAULTWINDOW
  | ERRORSTATUS | FILEINFORMATION | FOCUS | FONTTABLE | LASTEVENT | LOGMANAGER
  | MOUSE | PROFILER | RCODEINFORMATION | SECURITYPOLICY | SELF | SESSION
  | SOURCEPROCEDURE | SUPER | TARGETPROCEDURE | TEXTCURSOR | THISOBJECT | THISPROCEDURE | WEBCONTEXT | ACTIVEFORM
  ;

// inherited from grammar JPTreeParser
aatracestatement :#(  AATRACE
      ( OFF state_end
      | #(ON (AALIST)? ) state_end
      | (stream_name_or_handle)?
        ( (TO|FROM|THROUGH) io_phrase state_end
        | CLOSE state_end
        )
      )
    )
  ;

// inherited from grammar JPTreeParser
accum_what :AVERAGE|COUNT|MAXIMUM|MINIMUM|TOTAL|SUBAVERAGE|SUBCOUNT|SUBMAXIMUM|SUBMINIMUM|SUBTOTAL
  ;

// inherited from grammar JPTreeParser
accumulatestate :#(ACCUMULATE (display_item)* state_end )
  ;

// inherited from grammar JPTreeParser
aggregatephrase :#(Aggregate_phrase LEFTPAREN (aggregate_opt)+ ( #(BY expression (DESCENDING)? ) )* RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
analyzestate :#(  ANALYZE filenameorvalue filenameorvalue
      ( #(OUTPUT filenameorvalue ) )?
      (APPEND | ALL | NOERROR_KW)* state_end
    )
  ;

// inherited from grammar JPTreeParser
applystate :#(APPLY expression (#(TO gwidget ))? state_end )
  ;

// inherited from grammar JPTreeParser
assign_opt :#(ASSIGN ( #(EQUAL . expression ) )+ )
  ;

// inherited from grammar JPTreeParser
assignstate :#(ASSIGN assignment_list (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
atphrase :#(  AT
      ( atphraseab atphraseab
      | expression
      )
      (COLONALIGNED|LEFTALIGNED|RIGHTALIGNED)?
    )
  ;

// inherited from grammar JPTreeParser
atphraseab :#(COLUMN expression )
  | #(COLUMNOF referencepoint )
  | #(ROW expression )
  | #(ROWOF referencepoint )
  | #(X expression )
  | #(XOF referencepoint )
  | #(Y expression )
  | #(YOF referencepoint )
  ;

// inherited from grammar JPTreeParser
bellstate :#(BELL state_end )
  ;

// inherited from grammar JPTreeParser
callstate :#(CALL filenameorvalue (expressionorvalue)* state_end )
  ;

// inherited from grammar JPTreeParser
catchstate :#(  CATCH field AS (CLASS)? TYPE_NAME
      block_colon code_block (EOF | #(END (CATCH)?) state_end)
    )
  ;

// inherited from grammar JPTreeParser
casesens_or_not :#(Not_casesens NOT CASESENSITIVE )
  | CASESENSITIVE
  ;

// inherited from grammar JPTreeParser
casestate :#( CASE expression block_colon
      #(  Code_block
        ( #(WHEN case_expression THEN blockorstate )
        )*
      )
      ( #(OTHERWISE blockorstate ) )?
      (EOF | #(END (CASE)? ) state_end)
    )
  ;

// inherited from grammar JPTreeParser
case_expression :(#(OR .))=> #(OR case_expression case_expression )
  | #(WHEN expression)
  | expression
  ;

// inherited from grammar JPTreeParser
closequerystate :#(CLOSE QUERY ID state_end )
  ;

// inherited from grammar JPTreeParser
collatephrase :#(COLLATE funargs (DESCENDING)? )
  ;

// inherited from grammar JPTreeParser
color_expr :#(BGCOLOR expression )
  | #(DCOLOR expression )
  | #(FGCOLOR expression )
  | #(PFCOLOR expression )
  ;

// inherited from grammar JPTreeParser
colorspecification :(options{greedy=true;}:color_expr)+
  | #(  COLOR (DISPLAY)? anyorvalue
      ( #(PROMPT anyorvalue) )?
    )
  ;

// inherited from grammar JPTreeParser
comboboxphrase :#(  COMBOBOX
      ( #(LISTITEMS constant (COMMA constant)* )
      | #(LISTITEMPAIRS constant (COMMA constant)* )
      | #(INNERLINES expression )
      | SORT
      | tooltip_expr
      | SIMPLE
      | DROPDOWN
      | DROPDOWNLIST
      | #(MAXCHARS NUMBER )
      | #(AUTOCOMPLETION (UNIQUEMATCH)? )
      | sizephrase
      )*
    )
  ;

// inherited from grammar JPTreeParser
compilestate :#(  COMPILE filenameorvalue
      ( #(ATTRSPACE (#(EQUAL expression))? )
      | NOATTRSPACE
      | #(SAVE (#(EQUAL expression))? ( #(INTO filenameorvalue ) )? )
      | #(  LISTING filenameorvalue
          ( compile_append
          | #(PAGESIZE_KW expression)
          | #(PAGEWIDTH expression)
          )*
        )
      | #(XCODE expression )
      | #(XREF filenameorvalue (compile_append)? )
      | #(XREFXML filenameorvalue )
      | #(STRINGXREF filenameorvalue (compile_append)? )
      | #(STREAMIO (#(EQUAL expression))? )
      | #(MINSIZE (#(EQUAL expression))? )
      | #(LANGUAGES LEFTPAREN (compile_lang (COMMA compile_lang)*)? RIGHTPAREN )
      | #(TEXTSEGGROW #(EQUAL expression) )
      | #(DEBUGLIST filenameorvalue )
      | #(DEFAULTNOXLATE (#(EQUAL expression))? )
      | #(GENERATEMD5 (#(EQUAL expression))? )
      | #(PREPROCESS filenameorvalue )
      | #(USEREVVIDEO (#(EQUAL expression))? )
      | #(USEUNDERLINE (#(EQUAL expression))? )
      | #(V6FRAME (#(EQUAL expression))? )
      | NOERROR_KW
      )*
      state_end
    )
  ;

// inherited from grammar JPTreeParser
compile_lang :valueexpression | TYPELESS_TOKEN (LEXCOLON TYPELESS_TOKEN)*
  ;

// inherited from grammar JPTreeParser
compile_append :#(APPEND (#(EQUAL expression))? )
  ;

// inherited from grammar JPTreeParser
connectstate :#(CONNECT (NOERROR_KW|DDE|filenameorvalue)* state_end )
  ;

// inherited from grammar JPTreeParser
convertphrase :#( CONVERT 
      ( #(SOURCE (BASE64 | CODEPAGE expression (BASE64)?) ) )?
      ( #(TARGET (BASE64 | CODEPAGE expression (BASE64)?) ) )?
    )
  ;

// inherited from grammar JPTreeParser
copylobstate :#(  COPYLOB (FROM)?
      ( FILE expression | (OBJECT)? expression )
      ( #(STARTING AT expression) )?
      ( #(FOR expression) )?
      TO
      ( FILE expression (APPEND)?
      | (OBJECT)? expression (OVERLAY AT expression (TRIM)?)?
      )
      ( NOCONVERT | convertphrase )?
      ( NOERROR_KW )?
      state_end
    )
  ;

// inherited from grammar JPTreeParser
createaliasstate :#(CREATE ALIAS anyorvalue FOR DATABASE anyorvalue (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
createcallstate :#(CREATE CALL create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createclientprincipalstate :#(CREATE CLIENTPRINCIPAL create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createdatabasestate :#( CREATE DATABASE expression 
      ( #(FROM expression (NEWINSTANCE)? ) )?
      (REPLACE)? (NOERROR_KW)? state_end
    )
  ;

// inherited from grammar JPTreeParser
createdatasetstate :#(CREATE DATASET create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createdatasourcestate :#(CREATE DATASOURCE create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createquerystate :#(CREATE QUERY create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createsaxreaderstate :#(CREATE SAXREADER create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createsaxwriterstate :#(CREATE SAXWRITER create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createsoapheaderstate :#(CREATE SOAPHEADER create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createsoapheaderentryrefstate :#(CREATE SOAPHEADERENTRYREF create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createwidgetpoolstate :#(CREATE WIDGETPOOL (expression)? (PERSISTENT)? (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
createxdocumentstate :#(CREATE XDOCUMENT create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
createxnoderefstate :#(CREATE XNODEREF create_whatever_args state_end )
  ;

// inherited from grammar JPTreeParser
currentvaluefunc :#(CURRENTVALUE LEFTPAREN ID (COMMA ID)? (COMMA expression)? RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
datatype 
	:	CLASS TYPE_NAME 
	| datatype_var 
  ;

// inherited from grammar JPTreeParser
datatype_com :INT64 | datatype_com_native
  ;

// inherited from grammar JPTreeParser
datatype_com_native :SHORT | FLOAT | CURRENCY | UNSIGNEDBYTE | ERRORCODE | IUNKNOWN
  ;

// inherited from grammar JPTreeParser
datatype_dll :CHARACTER | INT64 | datatype_dll_native  
  ;

// inherited from grammar JPTreeParser
datatype_dll_native :BYTE | DOUBLE | FLOAT | LONG | SHORT | UNSIGNEDSHORT
  ;

// inherited from grammar JPTreeParser
datatype_field :BLOB | CLOB | datatype_var
  ;

// inherited from grammar JPTreeParser
datatype_param :datatype_dll_native | datatype_var
  ;

// inherited from grammar JPTreeParser
datatype_var :CHARACTER | COMHANDLE | DATE | DATETIME | DATETIMETZ
    | DECIMAL | HANDLE | INTEGER | INT64 | LOGICAL | LONGCHAR | MEMPTR
    | RAW | RECID | ROWID | TYPE_NAME | WIDGETHANDLE
  ;

// inherited from grammar JPTreeParser
ddeadvisestate :#(DDE ADVISE expression (START|STOP) ITEM expression (#(TIME expression))? (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
ddeexecutestate :#(DDE EXECUTE expression COMMAND expression (#(TIME expression))? (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
ddesendstate :#(DDE SEND expression SOURCE expression ITEM expression (#(TIME expression))? (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
ddeterminatestate :#(DDE TERMINATE expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
def_shared :SHARED
  | #(NEW (GLOBAL)? SHARED )
  ;

// inherited from grammar JPTreeParser
def_modifiers :( PRIVATE | PROTECTED | PACKAGEPRIVATE | PACKAGEPROTECTED | PUBLIC | STATIC | ABSTRACT | OVERRIDE | FINAL | SERIALIZABLE | NON_SERIALIZABLE )*
  ;

// inherited from grammar JPTreeParser
parent_id_relation :#(  PARENTIDRELATION (ID)?
      FOR RECORD_NAME COMMA RECORD_NAME
      PARENTIDFIELD field
      ( PARENTFIELDSBEFORE LEFTPAREN field (COMMA field)* RIGHTPAREN)?
      ( PARENTFIELDSAFTER  LEFTPAREN field (COMMA field)* RIGHTPAREN)?
    )
  ;

// inherited from grammar JPTreeParser
defineproperty_accessor :#( Property_getter def_modifiers GET
      ( (PERIOD)=> PERIOD
      | (function_params)? block_colon code_block END (GET)? PERIOD
      )
    )
  | #(  Property_setter def_modifiers SET
      ( PERIOD
      | function_params block_colon code_block END (SET)? PERIOD
      )
    )
  ;

// inherited from grammar JPTreeParser
deletealiasstate :#(DELETE_KW ALIAS (ID|QSTRING|valueexpression) state_end )
  ;

// inherited from grammar JPTreeParser
deleteobjectstate :#(DELETE_KW OBJECT expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
deleteprocedurestate :#(DELETE_KW PROCEDURE expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
deletewidgetstate :#(DELETE_KW WIDGET (gwidget)* state_end )
  ;

// inherited from grammar JPTreeParser
deletewidgetpoolstate :#(DELETE_KW WIDGETPOOL (expression)? (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
dictionarystate :#(DICTIONARY state_end )
  ;

// inherited from grammar JPTreeParser
display_with :(#(WITH BROWSE ID))=> #(WITH BROWSE ID )
  | framephrase
  ;

// inherited from grammar JPTreeParser
dynamiccurrentvaluefunc :#(DYNAMICCURRENTVALUE funargs)
  ;

// inherited from grammar JPTreeParser
editorphrase :#(  EDITOR
      ( #(INNERCHARS expression )
      | #(INNERLINES expression )
      | #(BUFFERCHARS expression )
      | #(BUFFERLINES expression )
      | LARGE
      | #(MAXCHARS expression )
      | NOBOX
      | NOWORDWRAP
      | SCROLLBARHORIZONTAL
      | SCROLLBARVERTICAL
      | tooltip_expr
      | sizephrase
      )*
    )
  ;

// inherited from grammar JPTreeParser
editingphrase :#(Editing_phrase (ID LEXCOLON)? EDITING block_colon (blockorstate)* END )
  ;

// inherited from grammar JPTreeParser
entryfunc :#(ENTRY funargs )
  ;

// inherited from grammar JPTreeParser
fillinphrase :#(FILLIN (NATIVE | sizephrase | tooltip_expr)* )
  ;

// inherited from grammar JPTreeParser
finallystate :#(FINALLY block_colon code_block (EOF | #(END (FINALLY)?) state_end) )
  ;

// inherited from grammar JPTreeParser
function_params :#(Parameter_list LEFTPAREN (function_param)? (COMMA function_param)* RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
getstate :#(GET findwhich ID (lockhow|NOWAIT)* state_end )
  ;

// inherited from grammar JPTreeParser
goonphrase :#(GOON LEFTPAREN goon_elem ((options{greedy=true;}:COMMA)? goon_elem)* RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
goon_elem :~(RIGHTPAREN) ( (OF)=> OF gwidget)?
  ;

// inherited from grammar JPTreeParser
hidestate :#(HIDE (stream_name_or_handle)? (MESSAGE|ALL|(gwidget)*) (NOPAUSE)? (#(IN_KW WINDOW expression))? state_end )
  ;

// inherited from grammar JPTreeParser
ifstate :#( IF expression THEN (blockorstate)?
      ( #(ELSE (blockorstate)? ) )?
    )
  ;

// inherited from grammar JPTreeParser
imagephrase_opt :#(FILE expression )
  | #(IMAGESIZE expression BY expression )
  | #(IMAGESIZECHARS expression BY expression )
  | #(IMAGESIZEPIXELS expression BY expression )
  | #(  FROM
      ( X expression | Y expression | ROW expression | COLUMN expression )
      ( X expression | Y expression | ROW expression | COLUMN expression )
    )
  ;

// inherited from grammar JPTreeParser
initial_constant :#(  INITIAL
      ( LEFTBRACE (TODAY|NOW|constant) (COMMA (TODAY|NOW|constant))* RIGHTBRACE
      | (TODAY|NOW|constant)
      )
    )
  ;

// inherited from grammar JPTreeParser
inputclearstate :#(INPUT CLEAR state_end )
  ;

// inherited from grammar JPTreeParser
inputclosestate :#(INPUT (stream_name_or_handle)? CLOSE state_end )
  ;

// inherited from grammar JPTreeParser
inputfromstate :#(INPUT (stream_name_or_handle)? FROM io_phrase state_end )
  ;

// inherited from grammar JPTreeParser
inputthroughstate :#(INPUT (stream_name_or_handle)? THROUGH io_phrase state_end )
  ;

// inherited from grammar JPTreeParser
inputoutputclosestate :#(INPUTOUTPUT (stream_name_or_handle)? CLOSE state_end )
  ;

// inherited from grammar JPTreeParser
inputoutputthroughstate :#(INPUTOUTPUT (stream_name_or_handle)? THROUGH io_phrase state_end )
  ;

// inherited from grammar JPTreeParser
interfacestate :#(INTERFACE TYPE_NAME (interface_inherits)? block_colon code_block #(END (INTERFACE)?) state_end )
  ;

// inherited from grammar JPTreeParser
interface_inherits :#(INHERITS TYPE_NAME (COMMA TYPE_NAME)*);

// inherited from grammar JPTreeParser
io_phrase :(  #(OSDIR LEFTPAREN expression RIGHTPAREN (NOATTRLIST)? )
    | #(PRINTER (valueexpression|.)? )
    | TERMINAL
    | (valueexpression | FILENAME) *
    )
    ( APPEND
    | BINARY
    | COLLATE
    | #(CONVERT ((SOURCE|TARGET) expression)* )
    | #(LOBDIR filenameorvalue )
    | NOCONVERT
    | ECHO | NOECHO
    | KEEPMESSAGES 
    | LANDSCAPE
    | #(MAP anyorvalue )
    | NOMAP
    | #(NUMCOPIES anyorvalue )
    | PAGED
    | #(PAGESIZE_KW anyorvalue )
    | PORTRAIT
    | UNBUFFERED 
    )*
  ;

// inherited from grammar JPTreeParser
label_constant :#(COLUMNLABEL constant (COMMA constant)* )
  | #(LABEL constant (COMMA constant)* )
  ;

// inherited from grammar JPTreeParser
leavestate :#(LEAVE (BLOCK_LABEL)? state_end )
  ;

// inherited from grammar JPTreeParser
lengthfunc :#(LENGTH funargs )
  ;

// inherited from grammar JPTreeParser
loadstate :#( LOAD expression
      ( #(DIR expression )
      | APPLICATION
      | DYNAMIC
      | NEW
      | #(BASEKEY expression )
      | NOERROR_KW
      )*
      state_end
    )
  ;

// inherited from grammar JPTreeParser
loadpicturefunc :#(LOADPICTURE (funargs)? )
  ;

// inherited from grammar JPTreeParser
namespace_prefix :#(NAMESPACEPREFIX constant )
  ;

// inherited from grammar JPTreeParser
namespace_uri :#(NAMESPACEURI constant )
  ;

// inherited from grammar JPTreeParser
nextstate :#(NEXT (BLOCK_LABEL)? state_end )
  ;

// inherited from grammar JPTreeParser
nextvaluefunc :#(NEXTVALUE LEFTPAREN ID (COMMA ID)? (COMMA expression)? RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
on___phrase :#( ON (ENDKEY|ERROR|STOP|QUIT)
      ( #(UNDO (BLOCK_LABEL)? ) )?
      ( COMMA
        ( #(LEAVE (BLOCK_LABEL)? )
        | #(NEXT (BLOCK_LABEL)? )
        | #(RETRY (BLOCK_LABEL)? )
        | #(RETURN (return_options)? )
        | THROW
        )
      )?
    )
  ;

// inherited from grammar JPTreeParser
osappendstate :#(OSAPPEND filenameorvalue filenameorvalue state_end )
  ;

// inherited from grammar JPTreeParser
oscommandstate :#(OS400   (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(BTOS    (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(DOS   (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(MPE   (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(OS2   (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(OSCOMMAND (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(UNIX    (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  | #(VMS   (SILENT|NOWAIT|NOCONSOLE)? (anyorvalue)* state_end )
  ;

// inherited from grammar JPTreeParser
oscopystate :#(OSCOPY filenameorvalue filenameorvalue state_end )
  ;

// inherited from grammar JPTreeParser
oscreatedirstate :#(OSCREATEDIR (anyorvalue)+ state_end )
  ;

// inherited from grammar JPTreeParser
osdeletestate :#(OSDELETE (valueexpression | ~(VALUE|RECURSIVE|PERIOD) )+ (RECURSIVE)? state_end )
  ;

// inherited from grammar JPTreeParser
osrenamestate :#(OSRENAME filenameorvalue filenameorvalue state_end )
  ;

// inherited from grammar JPTreeParser
outputclosestate :#(OUTPUT (stream_name_or_handle)? CLOSE state_end )
  ;

// inherited from grammar JPTreeParser
outputthroughstate :#(OUTPUT (stream_name_or_handle)? THROUGH io_phrase state_end )
  ;

// inherited from grammar JPTreeParser
outputtostate :#(OUTPUT (stream_name_or_handle)? TO io_phrase state_end )
  ;

// inherited from grammar JPTreeParser
pagestate :#(PAGE (stream_name_or_handle)? state_end )
  ;

// inherited from grammar JPTreeParser
pausestate :#(  PAUSE (expression)?
      ( BEFOREHIDE
      | #(MESSAGE constant )
      | NOMESSAGE
      | #(IN_KW WINDOW expression)
      )*
      state_end
    )
  ;

// inherited from grammar JPTreeParser
processeventsstate :#(PROCESS EVENTS state_end )
  ;

// inherited from grammar JPTreeParser
putstate :#(  PUT 
      (stream_name_or_handle)? (CONTROL|UNFORMATTED)?
      ( ( #(NULL_KW (LEFTPAREN)? ) )=> #(NULL_KW (funargs)? )
      | skipphrase
      | spacephrase
      | expression (#(FORMAT expression)|#(AT expression )|#(TO expression))*
      )*
      state_end
    )
  ;

// inherited from grammar JPTreeParser
putcursorstate :#(PUT CURSOR (OFF | (#(ROW expression)|#(COLUMN expression))* ) state_end )
  ;

// inherited from grammar JPTreeParser
putscreenstate :#(  PUT SCREEN
      ( ATTRSPACE | NOATTRSPACE | #(COLOR anyorvalue) | #(COLUMN expression) | #(ROW expression) | expression )*
      state_end
    )
  ;

// inherited from grammar JPTreeParser
putkeyvaluestate :#(  PUTKEYVALUE
      ( SECTION expression KEY (DEFAULT|expression) VALUE expression
      | (COLOR|FONT) (expression|ALL)
      )
      (NOERROR_KW)? state_end
    )
  ;

// inherited from grammar JPTreeParser
querytuningphrase :#( QUERYTUNING LEFTPAREN
      ( ARRAYMESSAGE | NOARRAYMESSAGE
      | BINDWHERE | NOBINDWHERE
      | #(CACHESIZE NUMBER (ROW|BYTE)? )
      | #(DEBUG (SQL|EXTENDED|CURSOR|DATABIND|PERFORMANCE|VERBOSE|SUMMARY|NUMBER)? )
      | NODEBUG
      | DEFERLOBFETCH
      | #(HINT expression )
      | INDEXHINT | NOINDEXHINT
      | JOINBYSQLDB | NOJOINBYSQLDB
      | LOOKAHEAD | NOLOOKAHEAD
      | ORDEREDJOIN
      | REVERSEFROM
      | SEPARATECONNECTION | NOSEPARATECONNECTION
      )*
      RIGHTPAREN
    )
  ;

// inherited from grammar JPTreeParser
quitstate :#(QUIT state_end )
  ;

// inherited from grammar JPTreeParser
radiosetphrase :#(  RADIOSET
      ( #(HORIZONTAL (EXPAND)? )
      | VERTICAL
      | (sizephrase)
      | #(RADIOBUTTONS 
          (QSTRING|UNQUOTEDSTRING) COMMA (constant|TODAY|NOW)
          (COMMA (QSTRING|UNQUOTEDSTRING) COMMA (constant|TODAY|NOW))*
        )
      | tooltip_expr
      )*
    )
  ;

// inherited from grammar JPTreeParser
rawfunc :#(RAW funargs )
  ;

// inherited from grammar JPTreeParser
readkeystate :#(READKEY (stream_name_or_handle)? (#(PAUSE expression))? state_end )
  ;

// inherited from grammar JPTreeParser
releaseexternalstate :#(RELEASE EXTERNAL (PROCEDURE)? expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
releaseobjectstate :#(RELEASE OBJECT expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
repositionstate :#( REPOSITION ID
      ( #(  TO
          ( ROWID expression (COMMA expression)* (FOR TENANT expression)?
          | RECID expression
          | ROW expression
          )
        )
      | #(ROW expression )
      | #(FORWARDS expression )
      | #(BACKWARDS expression )
      )
      (NOERROR_KW)? state_end
    )
  ;

// inherited from grammar JPTreeParser
returnstate :#(RETURN (return_options)? state_end )
  ;

// inherited from grammar JPTreeParser
return_options :( ( #(ERROR LEFTPAREN RECORD_NAME RIGHTPAREN) )=> expression
    | (ERROR)=> ERROR (expression)?
    | NOAPPLY (expression)?
    | expression
    )
  ;

// inherited from grammar JPTreeParser
routinelevelstate :#(ROUTINELEVEL ON ERROR UNDO COMMA THROW state_end)
  ;

// inherited from grammar JPTreeParser
blocklevelstate :#(BLOCKLEVEL ON ERROR UNDO COMMA THROW state_end)
    ;

// inherited from grammar JPTreeParser
savecachestate :#(SAVE CACHE (CURRENT|COMPLETE) anyorvalue TO filenameorvalue (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
seekstate :#(SEEK (INPUT|OUTPUT|stream_name_or_handle) TO (expression|END) state_end )
  ;

// inherited from grammar JPTreeParser
selectionlistphrase :#( SELECTIONLIST
      ( SINGLE
      | MULTIPLE
      | NODRAG
      | #(LISTITEMS constant (COMMA constant)* )
      | #(LISTITEMPAIRS constant (COMMA constant)* )
      | SCROLLBARHORIZONTAL
      | SCROLLBARVERTICAL
      | #(INNERCHARS expression )
      | #(INNERLINES expression )
      | SORT
      | tooltip_expr
      | sizephrase
      )*
    )
  ;

// inherited from grammar JPTreeParser
showstatsstate :#(SHOWSTATS (CLEAR)? state_end )
  ;

// inherited from grammar JPTreeParser
sizephrase :#(SIZE expression BY expression )
  | #(SIZECHARS expression BY expression )
  | #(SIZEPIXELS expression BY expression )
  ;

// inherited from grammar JPTreeParser
skipphrase :#(SKIP (funargs)? )
  ;

// inherited from grammar JPTreeParser
sliderphrase :#(  SLIDER
      ( HORIZONTAL
      | #(MAXVALUE expression )
      | #(MINVALUE expression )
      | VERTICAL
      | NOCURRENTVALUE
      | LARGETOSMALL
      | #(TICMARKS (NONE|TOP|BOTTOM|LEFT|RIGHT|BOTH) (#(FREQUENCY expression))? )
      | tooltip_expr
      | sizephrase
      )*
    )
  ;

// inherited from grammar JPTreeParser
spacephrase :#(SPACE (funargs)? )
  ;

// inherited from grammar JPTreeParser
state_end :PERIOD | EOF
  ;

// inherited from grammar JPTreeParser
statusstate :#( STATUS
      ( #(DEFAULT (expression)? )
      | #(INPUT (OFF|expression)? )
      )
      (#(IN_KW WINDOW expression))?
    state_end
    )
  ;

// inherited from grammar JPTreeParser
stopstate :#(STOP state_end )
  ;

// inherited from grammar JPTreeParser
stream_name_or_handle :#(STREAM ID )
  | #(STREAMHANDLE expression )
  ;

// inherited from grammar JPTreeParser
subscribestate :#(  SUBSCRIBE ( #(PROCEDURE expression) )? (TO)? expression
      (ANYWHERE | #(IN_KW expression) )
      ( #(RUNPROCEDURE expression) )?
      (NOERROR_KW)? state_end
    )
  ;

// inherited from grammar JPTreeParser
substringfunc :#(SUBSTRING funargs )
  ;

// inherited from grammar JPTreeParser
systemhelpstate :#( SYSTEMHELP expression
      ( #(WINDOWNAME expression) )?
      ( #(ALTERNATEKEY expression )
      | #(CONTEXT expression )
      | CONTENTS 
      | #(SETCONTENTS expression )
      | FINDER
      | #(CONTEXTPOPUP expression )
      | #(HELPTOPIC expression )
      | #(KEY expression )
      | #(PARTIALKEY (expression)? )
      | #(MULTIPLEKEY expression TEXT expression )
      | #(COMMAND expression )
      | #(POSITION (MAXIMIZE | X expression Y expression WIDTH expression HEIGHT expression) )
      | FORCEFILE
      | HELP
      | QUIT
      )
      state_end
    )
  ;

// inherited from grammar JPTreeParser
textphrase :#(TEXT (sizephrase | tooltip_expr)* )
  ;

// inherited from grammar JPTreeParser
titlephrase :#(TITLE (color_expr | #(COLOR anyorvalue) | #(FONT expression) )* expression )
  ;

// inherited from grammar JPTreeParser
toggleboxphrase :#(TOGGLEBOX (sizephrase | tooltip_expr)* )
  ;

// inherited from grammar JPTreeParser
tooltip_expr :#(TOOLTIP (valueexpression | constant) )
  ;

// inherited from grammar JPTreeParser
transactionmodeautomaticstate :#(TRANSACTIONMODE AUTOMATIC (CHAINED)? state_end )
  ;

// inherited from grammar JPTreeParser
undostate :#( UNDO (BLOCK_LABEL)?
      ( COMMA
        ( #(LEAVE (BLOCK_LABEL)? )
        | #(NEXT (BLOCK_LABEL)? )
        | #(RETRY (BLOCK_LABEL)? )
        | #(RETURN (return_options)? )
        | #(THROW expression)
        )
      )?
      state_end
    )
  ;

// inherited from grammar JPTreeParser
unloadstate :#(UNLOAD expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
unsubscribestate :#(UNSUBSCRIBE (#(PROCEDURE expression))? (TO)? (expression|ALL) (#(IN_KW expression))? state_end )
  ;

// inherited from grammar JPTreeParser
usestate :#(USE expression (NOERROR_KW)? state_end )
  ;

// inherited from grammar JPTreeParser
usingstate :#(USING TYPE_NAME (#(FROM (ASSEMBLY|PROPATH)))? state_end )
  ;

// inherited from grammar JPTreeParser
viewasphrase :#(  VIEWAS
      ( comboboxphrase
      | editorphrase
      | fillinphrase
      | radiosetphrase
      | selectionlistphrase
      | sliderphrase
      | textphrase
      | toggleboxphrase
      )
    )
  ;

// inherited from grammar JPTreeParser
waitforstate :#(  WAITFOR
      ( widattr (#(SET field))? // .NET WAIT-FOR.
      | eventlist OF widgetlist
        (#(OR eventlist OF widgetlist))*
        (#(FOCUS gwidget))?
        (#(PAUSE expression))?
        (EXCLUSIVEWEBUSER (expression)?)?
      )
      state_end
    )
  ;

// inherited from grammar JPTreeParser
widget_id :#(WIDGETID expression ) ;

// inherited from grammar JPTreeParser
xml_data_type :#(XMLDATATYPE constant ) ;

// inherited from grammar JPTreeParser
xml_node_name :#(XMLNODENAME constant ) ;

// inherited from grammar JPTreeParser
xml_node_type :#(XMLNODETYPE constant ) ;

// inherited from grammar JPTreeParser
closestate :#(CLOSE ID state_end )
  ;

// inherited from grammar JPTreeParser
createtablestate :#(  CREATE TABLE ID 
      LEFTPAREN
      ( sql_col_def
      | #(UNIQUE LEFTPAREN ID (COMMA ID)* RIGHTPAREN)
      )
      ( COMMA
        ( sql_col_def
        | #(UNIQUE LEFTPAREN ID (COMMA ID)* RIGHTPAREN)
        )
      )*
      RIGHTPAREN
      state_end
    )
  ;

// inherited from grammar JPTreeParser
declarecursorstate :#(DECLARE ID CURSOR FOR selectstatea (#(FOR (#(READ (ONLY)?) | UPDATE)))? state_end )
  ;

// inherited from grammar JPTreeParser
dropindexstate :#(DROP INDEX ID state_end )
  ;

// inherited from grammar JPTreeParser
dropviewstate :#(DROP VIEW ID state_end )
  ;

// inherited from grammar JPTreeParser
grant_rev_to :#(TO (PUBLIC | FILENAME (COMMA FILENAME)*) )
  | #(FROM (PUBLIC | FILENAME (COMMA FILENAME)*) )
  ;

// inherited from grammar JPTreeParser
openstate :#(OPEN ID state_end )
  ;

// inherited from grammar JPTreeParser
select_from_spec :select_sqltableref
    ( #(LEFT (OUTER)? JOIN select_sqltableref ON sqlexpression )
    | #(RIGHT (OUTER)? JOIN select_sqltableref ON sqlexpression )
    | #(INNER JOIN select_sqltableref ON sqlexpression )
    | #(OUTER JOIN select_sqltableref ON sqlexpression )
    | #(JOIN select_sqltableref ON sqlexpression )
    )*
    ( #(WHERE sqlexpression) )?
  ;

// inherited from grammar JPTreeParser
select_order_expr :sqlscalar (ASC|DESCENDING)? (COMMA sqlscalar (ASC|DESCENDING)?)*
  ;

// inherited from grammar JPTreeParser
sql_col_def :#( ID
      . // datatype
      (PRECISION)?
      (LEFTPAREN NUMBER (COMMA NUMBER)? RIGHTPAREN)?
      ( #(Not_null NOT NULL_KW (UNIQUE)? ) )?
      ( label_constant
      | #(DEFAULT expression )
      |   #(FORMAT expression)
      |   casesens_or_not
      )*
    )
  ;

// inherited from grammar JPTreeParser
sqlexpression :#(OR sqlexpression sqlexpression )
  | #(AND sqlexpression sqlexpression )
  | #(NOT sqlexpression )
  | #(MATCHES sqlscalar (sqlscalar | sql_comp_query) )
  | #(BEGINS  sqlscalar (sqlscalar | sql_comp_query) )
  | #(CONTAINS  sqlscalar (sqlscalar | sql_comp_query) )
  | #(EQ    sqlscalar (sqlscalar | sql_comp_query) )
  | #(NE    sqlscalar (sqlscalar | sql_comp_query) )
  | #(GTHAN   sqlscalar (sqlscalar | sql_comp_query) )
  | #(GE    sqlscalar (sqlscalar | sql_comp_query) )
  | #(LTHAN   sqlscalar (sqlscalar | sql_comp_query) )
  | #(LE    sqlscalar (sqlscalar | sql_comp_query) )
  | #(EXISTS LEFTPAREN selectstatea RIGHTPAREN )
  | #(Sql_begins (NOT)? BEGINS sqlscalar )
  | #(Sql_between (NOT)? BETWEEN sqlscalar AND sqlscalar )
  | #(Sql_in (NOT)? IN_KW LEFTPAREN (selectstatea | sql_in_val (COMMA sql_in_val)*) RIGHTPAREN )
  | #(Sql_like (NOT)? LIKE sqlscalar (ESCAPE sqlscalar)? )
  | #(Sql_null_test IS (NOT)? NULL_KW )
  | sqlscalar
  ;

// inherited from grammar JPTreeParser
sql_comp_query :#(Sql_comp_query (ANY|ALL|SOME)? LEFTPAREN selectstatea RIGHTPAREN )
  ;

// inherited from grammar JPTreeParser
sqlscalar :#(PLUS sqlscalar sqlscalar )
  | #(MINUS sqlscalar sqlscalar )
  | #(MULTIPLY sqlscalar sqlscalar )
  | #(DIVIDE sqlscalar sqlscalar )
  | #(MODULO sqlscalar sqlscalar )
  | #(UNARY_PLUS exprt )
  | #(UNARY_MINUS exprt )
  | (LEFTPAREN)=> #(LEFTPAREN sqlexpression RIGHTPAREN )
  | exprt
  ;


