/* SCL-6310 fixture - a class without type parameters: no Type_parameter_list. */

class SCL6310.Plain inherits SCL6310.GenericBase:
    define variable oProbe as SCL6310.GenericProbe<Progress.Lang.Object> no-undo.
end class.
