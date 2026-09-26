proparse
========

Proparse is a parser for the OpenEdge ABL (Progress 4GL) language. It reads ABL
source code, resolves include files and preprocessor directives, and produces a
syntax tree (JPNode tree) with symbol information (variables, buffers, frames,
blocks). Consultingwerk uses it in the SmartComponent Library, for example in
the Business Entity Designer and in the refactoring and code analysis tools.

This repository is a fork of the original Proparse / ProRefactor project by
Joanju Software (http://www.oehive.org/proparse/), extended by Consultingwerk.

[PUG Challenge 2017 Presentation](
https://github.com/consultingwerk/proparse/files/1066604/2017.Proparse.EN.pdf)

How it fits together
--------------------

1. The parser is written in Java. The grammars use ANTLR 2.7.7 syntax and the
   generated Java parsers are checked in, so a normal build does not need ANTLR.
2. The Java classes are compiled into `bin/` and packaged into `proparse.jar`.
3. IKVM (https://github.com/ikvmnet/ikvm) converts `proparse.jar` plus its Java
   library dependencies into a strong-named .NET assembly, `proparse.net.dll`.
4. OpenEdge ABL code loads `proparse.net.dll` like any other .NET assembly and
   calls the Java classes directly, for example
   `org.prorefactor.treeparser.ParseUnit` or `com.joanju.proparse.Environment`.

Repository layout
-----------------

| Path | Purpose |
|------|---------|
| `src/com/joanju/proparse` | Lexer, preprocessor, the ANTLR grammar `proparse.g` and the generated `ProParser.java`, node type definitions |
| `src/org/prorefactor` | ProRefactor: tree parser (`treeparser`, `treeparser01`), schema handling, symbol scopes, refactoring helpers |
| `src/de/consultingwerk` | Consultingwerk extensions |
| `src/test` | JUnit 3 tests (`test.SCLnnnn.*`), one folder per SCL ticket, with the ABL test code next to the tests |
| `lib/` | Java dependencies compiled into the assembly (ANTLR 2.7.7, commons-*, jdbm, json.org, junit 3.8.1, groovy 1.7.3, ant 1.8.1) |
| `build.xml` | Ant build: compile, jar, and the IKVM/.NET build |
| `proparse.csproj` | MSBuild project used by the Ant build to run IKVM |
| `proparse.snk` | Strong name key for `proparse.net.dll` (public key token `cda1b098b1034b24`) |
| `build.number` | Ant build counter, becomes the 4th part of the assembly version |
| `data/`, `prorefactor/`, `tmp/` | Sample ABL code, project settings and output folder used by the ProRefactor unit tests |
| `src-abl/` | Legacy ABL shim reproducing the old C++ `proparse.dll` API on top of `proparse.net.dll` (do not use for new code) |
| `ikvmbin/` | Legacy IKVM 8.1 command line tools, kept for reference only |

Prerequisites
-------------

* A JDK. The Java sources are compiled with `--release 8` because IKVM 8.x is
  based on OpenJDK 8. JDK 13 and JDK 17 have been verified; any JDK from 9 up
  that still supports `--release 8` should work.
* Apache Ant 1.10 or newer (OpenEdge ships one under `%DLC%\ant`).
* .NET SDK 8.0 or newer (verified with SDK 10.0) with the .NET Framework 4.7.2
  targeting pack (part of Visual Studio or the .NET Framework Developer Pack).
* Access to nuget.org: the first build downloads IKVM 8.15.0 and its runtime
  images (several hundred MB) into the NuGet cache.

Building
--------

Set up the tools and run the default target:

```bat
set JAVA_HOME=C:\path\to\jdk
set PATH=%JAVA_HOME%\bin;C:\Progress\OpenEdge\ant\bin;%PATH%
ant -f build.xml make_dotnet_msbuild
```

The target runs these steps:

1. `compile`: compiles `src/` into `bin/` and copies the token tables
   (`*TokenTypes.txt`), properties, icons and test data next to the classes.
2. `makeproparsejar`: increments `build.number` and packs `bin/` into
   `proparse.jar`.
3. `make_java_package`: zips `proparse.jar`, `lib/` and `build.number` into
   `output/proparse.java.zip` for JVM consumers.
4. `make_dotnet_msbuild`: runs `dotnet restore`, `clean` and `build`/`publish`
   on `proparse.csproj` once per target. IKVM compiles `proparse.jar` and all
   jars in `lib/` into one assembly.

Outputs (all ignored by git):

| Path | Content |
|------|---------|
| `output/proparse.java.zip` | `proparse.java/proparse.jar`, `proparse.java/lib/*.jar`, `proparse.java/build.number` |
| `output/x64/` | `proparse.net.dll` for .NET Framework 4.7.2, win-x64, plus the IKVM runtime (`IKVM.Runtime.dll`, `IKVM.Java.dll`, `IKVM.CoreLib.dll`, `IKVM.ByteCode.dll`, `ikvm.dll`, `ikvm.properties`, `ikvm/`, `runtimes/`) and the `System.*` support assemblies |
| `output/netcore/` | `proparse.net.dll` for .NET 8, linux-x64, framework dependent |
| `output/proparse.win-x64.zip`, `output/proparse.netcore-linux-x64.zip` | The two folders above as zip files |
| `output/netcore-win-x64/`, `output/proparse.netcore-win-x64.zip` | `proparse.net.dll` for .NET 8, win-x64, framework dependent. Not built by default; run `ant make_dotnet_netcore_win` after the default target |

All three variants are produced by the same IKVM 8.15 toolchain from the same
`proparse.jar`; only the target framework and runtime identifier differ.

Useful individual targets: `ant clean`, `ant compile`, `ant makeproparsejar`,
`ant make_dotnet_netcore_win`.

The `win-x86` build is commented out in `build.xml`; enable it there if a
32-bit assembly is needed.

Versioning
----------

The assembly version is `5.0.0.<build.number>`. The major/minor part is set in
`proparse.csproj`, the last part comes from `build.number`, which Ant increments
on every `makeproparsejar` run. Commit `build.number` together with a release
build so the numbers stay in sync with what was shipped.

Deploying into the SmartComponent Library
-----------------------------------------

Copy the complete content of `output/x64/` (except `proparse.net.pdb`) into the
`Assemblies/Support` folder of the ABL project and keep the `ikvm/` and
`runtimes/` sub folders. IKVM finds its Java home through `ikvm.properties`
(`ikvm.home.root=ikvm`), which must stay next to `proparse.net.dll`.

The IKVM runtime references several `System.*` assemblies in versions that
differ from the ones in the GAC. The hosting process therefore needs the usual
assembly binding redirects (for example for
`System.Runtime.CompilerServices.Unsafe`); OpenEdge provides these through the
`.config` file of `prowin.exe` / `_progres.exe`.

Running the tests
-----------------

The JUnit 3 tests live in `src/test`. After `ant compile`, run a test class with
the text runner from the repository root (the tests use paths relative to it):

```bat
java -cp "bin;lib/*" junit.textui.TestRunner test.SCL5228.TestOnStatement
```

Some tests read their propath from `src/test/propath.txt` or
`src/test/propath_128.txt` and resolve include files from a local OpenEdge
installation; adjust those files to your machine if such tests fail with
"Could not find include file".

Working on the grammar
----------------------

The grammar files use ANTLR 2.x syntax (compiler in `lib/antlr-2.7.7.jar`). The
newer ANTLR IDE plugins only support ANTLR 3+; the old ANTLR plugin for Eclipse
is at http://antlreclipse.sourceforge.net/updates.

Grammar files used by proparse:

* `com/joanju/proparse/proparse.g` -> `ProParser.java`
* `com/joanju/proparse/proeval.g` -> `ProEval.java` (only used when code chunk
  or preprocessor evaluation is requested)

Regenerate them with `ant -f src/com/joanju/proparse/build.xml`.

ProRefactor has additional tree parser grammars that mostly mirror the proparse
grammar with extensions:

* `org/prorefactor/treeparserbase/JPTreeParser.g` (base grammar, referenced via
  `glib`)
* `org/prorefactor/treeparser01/JPTreeParser.g`
* `org/prorefactor/treeparser01/expandedtreeparser01.g` -> `TreeParser01.java`
  (regenerate with `ant -f src/org/prorefactor/treeparser01/build.xml`)

Adding a new keyword usually requires:

1. Add the keyword to `com/joanju/proparse/BaseTokenTypes.txt` (also increment
   `Last_Token_Number`).
2. Add the keyword in `com/joanju/proparse/NodeTypes.java` (there is a long
   list in the static constructor).
3. Depending on the keyword type the grammar file might need to be updated:
   * SYSTEM HANDLE (SYSHDL): add the keyword to the `systemhandlename` rule
   * FUNCTIONS (MAY_BE_REGULAR_FUNC or MAY_BE_NO_ARG_FUNC): add it to one of
     the rules `builtinfunc`, `argfunc`, `recordfunc`, `noargfunc`
4. Regenerate the parser, rebuild, and add a JUnit test under `src/test`.

Notes
-----

* `src/com/joanju/scripting/*.groovy` and `src/proparse/*.groovy` are
  development scripts (Eclipse compiles them through its Groovy plugin). The
  Ant `compile` target skips them; they are not needed by `proparse.net.dll`.
* The Eclipse project (`.project`, `.classpath`) still works for day to day
  development. Eclipse compiles into the same `bin/` folder the Ant build uses.
* IKVM prints many `IKVM0100: Class ... not found` warnings for optional
  dependencies of Groovy and Ant (jline, ivy, servlet API). They are expected.
