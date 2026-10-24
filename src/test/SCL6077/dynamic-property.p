/* SCL-6077: DYNAMIC-PROPERTY as an assignment target and as a function */

define variable oObj   as Progress.Lang.Object no-undo .
define variable cProp  as character            no-undo .
define variable cValue as character            no-undo .

/* statement */
dynamic-property (oObj, cProp) = "x":u .

/* single ASSIGN target */
assign dynamic-property (oObj, cProp) = "y":u .

/* ASSIGN with several targets */
assign cProp                          = "Name":u
       dynamic-property (oObj, cProp) = "z":u .

/* function on the right-hand side */
cValue = dynamic-property (oObj, cProp) .

/* index argument, static property of a class given by name */
dynamic-property (oObj, cProp, 2) = "a":u .
dynamic-property ("Consultingwerk.Util.LogManager":u, cProp) = "b":u .

/* both sides, NO-ERROR */
dynamic-property (oObj, cProp) = dynamic-property (oObj, "Other":u) no-error .

/* compound assignment inside ASSIGN */
assign dynamic-property (oObj, cProp) += "c":u .

/* compound assignment statements */
dynamic-property (oObj, cProp) += "e":u .
dynamic-property (oObj, "Count":u) -= 1 .
dynamic-property (oObj, "Count":u) *= 2 no-error .
dynamic-property (oObj, "Count":u) /= 3 .

/* nested in an expression */
cValue = string (dynamic-property (oObj, cProp)) + "d":u .

/* expression statement */
dynamic-property (oObj, cProp) .
