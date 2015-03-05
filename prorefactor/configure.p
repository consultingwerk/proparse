
def var path as character no-undo.
def var projectName as character.
def var workDirFullPath as character.
def var ed1 as character.
def var ed2 as character.

function escapePropertiesString returns character (s as character):
  /* In the properties file, colons and backslashes are escaped. */
  ASSIGN s = REPLACE(s, "~\", "~\~\").
  ASSIGN s = REPLACE(s, ":", "~\:").
  return s.
end function.


ed1 =
"ProRefactor reads and writes configuration and other files in a directory
 named 'prorefactor' in your working/startup directory. This program will
 create (if necessary) that 'prorefactor' directory, and some
 configuration files within it. Enter the full path to your
 working/startup directory. It must already exist.
".
display ed1
  view-as editor inner-chars 75 inner-lines 5
  with no-labels.
update workDirFullPath format "x(70)".


ed2 =
"ProRefactor keeps its configuration in a directory with the same name as
 your project. Enter the name of your project, ex: sports. In that case,
 a directory named prorefactor/projects/sports would be created, and
 the project configuration files will be written there.
".
display ed2
  view-as editor inner-chars 75 inner-lines 4.
update projectName format "x(70)".

path = workDirFullPath + "/prorefactor".
os-create-dir value(path).
path = path + "/projects".
os-create-dir value(path).
path = path + "/" + projectName.
os-create-dir value(path).
path = path + "/".

run prorefactor/configdump/configdump.p
  (path + "progress.properties").

output to value (path + "proparse.properties").
put unformatted "schema_file=" + escapePropertiesString(path + "prorefactor.schema").
output close.

run prorefactor/configdump/schemadump1.p
  (path + "prorefactor.schema").


