define temp-table GetObjectInfoResult no-undo 
  field GetObjectInfoResult_id as integer 
  field CreationTime as character
  field Creator as character
  field FullName as character
  field IsFile as character
  field LastWriteTime as character
  field Name  as character
  field Size as character
  field VersionId as character
  field VersionNumber  as character.

define temp-table ObjectType no-undo 
  field GetObjectInfoResult_id as integer .

  define dataset dsGetObjectInfoResult namespace-uri "http://services.kinetic.ch/"
    for GetObjectInfoResult, ObjectType
    parent-id-relation RELATION1 for GetObjectInfoResult, ObjectType
      parent-id-field GetObjectInfoResult_id
      parent-fields-before (CreationTime,Creator,FullName,IsFile,LastWriteTime,Name)
      parent-fields-after (Size,VersionId,VersionNumber).