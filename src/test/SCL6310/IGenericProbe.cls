/* SCL-6310 fixture - a generic interface that inherits another interface. */

interface SCL6310.IGenericProbe<T as Progress.Lang.Object, U as SCL6310.GenericProbe<Progress.Lang.Object>>
    inherits SCL6310.IPlain:
    method public T Get (poKey as U).
end interface.
