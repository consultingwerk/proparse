
block-level on error undo, throw.

class test.SCL3885.TestCode:

    define temp-table ttTable no-undo
        field SomeField as character.

    method public void MethodOne ():
        define variable hBuffer as handle no-undo.

        create ttTable.
        assign
            ttTable.SomeField = "Hello World!".

        hBuffer = ttTable:handle.

        this-object:MethodTwo (buffer ttTable::SomeField).
        this-object:MethodTwo (ttTable.SomeField).
        this-object:MethodTwo (hBuffer::SomeFieldSomeField).
        this-object:MethodTwo (buffer ttTable:handle::SomeField).

    end method.

    method public void MethodTwo (pcText as character):
    
        message pcText
            view-as alert-box.

    end method.

end class.