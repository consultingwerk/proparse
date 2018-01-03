define temp-table ttIdx
    field idxName as char
    field pkLike as deci.

procedure test:
    define input parameter tst LIKE ttIdx.idxName.
end procedure.
    
FUNCTION myExampleFunction RETURNS LOGICAL ( 
    output ioP as character, 
    input-output ioj like ttIdx.pkLike,
    input tstf as decimal extent 5,
    input fer like ttIdx.idxName,
    input hkjhkjh as class Progress.Lang.Error,
    input-output class Progress.Lang.AppError extent,
    output decimal extent):
    return true.
end function.