/* SCL-6310 fixture - a generic class that inherits: the super class has
   to be found behind the type parameter list (cBase is inherited). */

class SCL6310.GenericChild<T as Progress.Lang.Object> inherits SCL6310.GenericBase:
    method public void Touch ():
        cBase = "x".
    end method.
end class.
