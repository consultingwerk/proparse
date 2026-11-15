/* SCL-6310 fixture - type parameter constraints that are bound generic
   types themselves (one TYPE_NAME with the SCL-6029 text rule), and bound
   generic type references in the body (unchanged). */

using Progress.Collections.* from propath.

class SCL6310.GenericConstraint<L as Progress.Collections.IList<Progress.Lang.Object>,
                                M as IMap<Progress.Lang.Object, Outer<Progress.Lang.Object, Inner>>>
    implements IComparable<Progress.Lang.Object>
    use-widget-pool:

    define variable oPair as Pair<Alpha, Beta> no-undo.

    method public integer CompareTo (poOther as Progress.Lang.Object):
        oPair = new Pair<Alpha, Beta> ().
        return 0.
    end method.

end class.
