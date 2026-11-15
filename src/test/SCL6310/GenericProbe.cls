/* SCL-6310 fixture - the probe of the ticket: a generic class with one
   type parameter, used as property and return type in the body. */

class SCL6310.GenericProbe<T as Progress.Lang.Object>:
    define public property Value as T no-undo get. set.
    method public T GetValue ():
        return this-object:Value.
    end method.
end class.
