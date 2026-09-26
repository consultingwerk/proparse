# CLAUDE.md

Guidance for Claude Code when working in this repository. See `README.md` for
the human-oriented overview; this file adds the details that matter when
changing or building the code.

## What this is

Proparse: an OpenEdge ABL parser written in Java (ANTLR 2.7.7), compiled to a
strong-named .NET assembly (`proparse.net.dll`) with IKVM 8.15 and consumed by
the SmartComponent Library ABL code. The .NET assembly exposes the Java classes
unchanged (`com.joanju.proparse.*`, `org.prorefactor.*`), so every public Java
signature is API surface for ABL callers. Do not rename or remove public
members without checking the ABL consumers.

## Build

Requirements: JDK (9+, compiles with `--release 8`), Ant 1.10+, .NET SDK 8+ with
the net472 targeting pack, nuget.org access. `java`, `ant` and `dotnet` must be
on `PATH` (`JAVA_HOME` set for Ant).

```bat
ant -f build.xml make_dotnet_msbuild
```

Targets in `build.xml`:

- `clean` removes `bin/`, `output/`, `obj/`, `proparse.jar`
- `compile` javac of `src/` into `bin/` plus resource copy (token tables,
  properties, icons, test data). Groovy scripts are skipped on purpose.
- `makeproparsejar` increments `build.number`, jars `bin/` into `proparse.jar`
- `make_java_package` zips `proparse.jar`, `lib/*.jar` and `build.number` into
  `output/proparse.java.zip` (layout `proparse.java/...`)
- `make_dotnet_msbuild` (default) runs `dotnet restore/clean/build|publish` on
  `proparse.csproj` for net472/win-x64 (`output/x64`) and net8.0/linux-x64
  (`output/netcore`), then zips both
- `make_dotnet_netcore_win` (optional, run after the default target) builds
  net8.0/win-x64 into `output/netcore-win-x64` and zips it; reuses
  `proparse.jar` and `build.number` without bumping it

Everything the build produces is git-ignored (`bin/`, `output/`,
`proparse.jar`). `obj/` is deleted by the build itself.

Expected noise: hundreds of `warning IKVM0100: Class ... not found` lines for
optional Groovy/Ant dependencies. A build is fine as long as Ant reports
`BUILD SUCCESSFUL`.

### Versioning

Assembly version is `5.0.0.<build.number>` (`proparse.csproj` +
`build.number`). Ant bumps `build.number` on every `makeproparsejar` run.
For a throwaway verification build, restore it with
`git checkout -- build.number` afterwards. For a release build, commit it.

### Verifying a build

1. `java -cp "bin;lib/*" junit.textui.TestRunner test.SCL5228.TestOnStatement`
   (JUnit 3, run from the repo root; test data paths are relative to it).
2. Load `output/x64/proparse.net.dll` in a .NET Framework host and parse a
   file: `Environment.instance()`, `Schema.getInstance().loadSchema(...)`,
   `configSet("propath"|"opsys"|"proversion"|...)`, then
   `new ParseUnit(file, "ISO8859-1").treeParser01()` and walk `getTopNode()`.
   A bare host (for example `powershell.exe`) needs an `AssemblyResolve`
   handler or binding redirects for the `System.*` assemblies shipped next to
   the DLL; OpenEdge supplies those through its `.config` files.
3. Compare against the assemblies deployed in the ABL project
   (`Assemblies/Support` folder): same public key token `cda1b098b1034b24`,
   same IKVM 8.15.0 dependency set, same `ikvm/` and `runtimes/` layout.

## Source layout

- `src/com/joanju/proparse`: `Lexer`, `Preprocessor`, `Postlexer`,
  `DoParse` (drives a parse), `ProParser.java` (generated from `proparse.g`),
  `ProEval.java` (generated from `proeval.g`), `NodeTypes`, `ProToken`,
  `Environment` (global configuration singleton), `SymbolScope`.
- `src/org/prorefactor/treeparser`: `ParseUnit` (main entry point for
  callers), symbol tables, `TreeParser01` support.
- `src/org/prorefactor/treeparser01`: `TreeParser01.java` (generated from
  `expandedtreeparser01.g` with `JPTreeParser.g` as base grammar).
- `src/org/prorefactor/core`: `JPNode` tree, `Schema`, `TokenTypes`.
- `src/org/prorefactor/refactor`: older ProRefactor refactoring code, still
  compiled into the assembly.
- `src/de/consultingwerk/proparse`: Consultingwerk additions.
- `src/test`: JUnit 3 tests, one package per SCL ticket (`test.SCL1234`),
  with ABL fixtures beside them. `test.ProparseTestCase` sets up
  `Environment` and `Schema` from `src/test/schema.txt` and the propath files.

## Rules when editing

- Generated files: `ProParser.java`, `ProEval.java`, `TreeParser01.java`, the
  `*.smap` files and `*TokenTypes.java/.txt` come from ANTLR. Edit the `.g`
  grammar and regenerate with the `build.xml` next to it
  (`src/com/joanju/proparse/build.xml`, `src/org/prorefactor/treeparser01/build.xml`,
  ANTLR 2.7.7 from `lib/`). Only hand-edit the generated files if the change
  is also applied to the grammar. Without the Ant `antlr` task, run
  `java -cp lib/antlr-2.7.7.jar antlr.Tool proparse.g` in
  `src/com/joanju/proparse`, then copy the new `ProParserTokenTypes.txt` to
  `src/org/prorefactor/treeparser01/` and run
  `antlr.Tool -glib JPTreeParser.g expandedtreeparser01.g` there (delete the
  `expandedexpandedtreeparser01.g` it leaves behind). `expandedtreeparser01.g`
  is the tree grammar that is actually compiled; `JPTreeParser.g` is the
  action-free specification, keep both in sync. `ProEval.java` is checked in
  from an older ANTLR build; do not regenerate it unless `proeval.g` changes.
- Token numbers (`*TokenTypes` interfaces, `Last_Token_Number`) are compile
  time constants that javac inlines into every class using them. After a
  token table change, always compile from a clean `bin/` (the Ant `compile`
  target does that) or classes like `org.prorefactor.core.TokenTypes` keep
  the old numbers.
- New node types must match the Proparse ABL engine's table in the ABL
  project (`Consultingwerk/Studio/ProparseApi/NodeTypesEnum.cls`,
  `.../ProparseAbl/proparse-tokentypes.dat`); both engines are compared by
  parity tests run from OpenEdge, not from this repo.
- New keyword: `BaseTokenTypes.txt` (bump `Last_Token_Number`),
  `NodeTypes.java` static initializer, then the grammar rule
  (`systemhandlename`, `builtinfunc`, `argfunc`, `recordfunc`, `noargfunc`),
  regenerate, add a `src/test/SCLnnnn` test.
- Java 8 language level and bytecode only (IKVM 8.x is OpenJDK 8 based).
  No `var`, records, switch expressions, or newer APIs.
- Files use CRLF line endings (`core.autocrlf=true`); keep them.
- Every fix is tied to a Jira ticket. Commit messages start with the ticket
  key, e.g. `SCL-5228 : Fix ...`, and tests live in a matching
  `src/test/SCL5228` package.
- Do not commit build outputs or `.bak` files; use git to restore files.
- After opening a pull request, add a comment to the Jira ticket with the PR
  link and a description of the changes (Jira comments are written as ADF).
- Some JUnit tests depend on a local OpenEdge install via
  `src/test/propath*.txt` (include files like `adecomm/appserv.i`). Failures
  of the form "Could not find include file" on a machine without that layout
  are environmental, not regressions.

## Legacy material (leave alone unless asked)

`ikvmbin/` (IKVM 8.1 command line tools) and `src-abl/` (ABL shim for the old
C++ API) are historical artifacts and are not used by the current build. Build
outputs (`proparse.jar`, `output/`, any `proparse.*.zip`) are never committed;
the old `proparse.assemblies.zip` and `proparse.java.zip` were removed from git
for that reason.
