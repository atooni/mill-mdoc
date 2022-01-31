# Simple MDoc runner for Mill

This plugin provides a mill module that allows to execute [Scala MDoc](https://scalameta.org/mdoc/) from 
within a mill build. Scala MDoc simply compiles properly marked Scala 
snippets in plain md files and optionally runs them through an interpreter, 
augmenting the code with the interpreter output. 

If the code can't be compiled, mdoc throws an error and the build fails.

Using mdoc, developers can ensure the correctness of their code samples. 

The mill plugin is simply a wrapper around the main class, translating 
common project parameters into arguments for mdocs `Main` class. 

Further, the mill plugin can run the compilation in watch mode, 
redirecting the md files to a different location. For example, if 
that location points to an input directory for md files used by 
a static content generator such as [Docusaurus 2](https://docusaurus.io/)
and that generator is started in watch mode as well, the developer 
can work on the md files and code snippets and immediately observe 
the results. 

## Parameters 

### `scalaMdocVersion`

A string parameter denoting the Scala MDoc version used. This is 
currently set to `2.2.24` by default.

### `scalaMdocDep` 

An Ivy dependency pointing to Scala MDoc. This is by default derived 
from `scalaMdocVersion` as `ivy"org.scalameta::mdoc:${scalaMdocVersion()}"`

### `watchedMDocsDestination`

An optional `Path` parameter pointing to a directory where compiled `md` files 
should be saved in watch mode. This is set to `None` as a default. 

Using `mdocWatch` without setting this parameter won't work. 

### `mdocSources`

The sources where the input md files are kept.

## Targets

### `mdoc`

Run `mill -i __.mdoc` or any other mdoc task to compile the Scala code within 
the source md files and produce new md files in the task's destination directory.

### `mdocWatch`

Run `mill -i __.mdocWatch` to run mdoc in watch mode. This will block the shell 
the command is run in until the command is interrupted. Normally this is used in 
combination with a static content generator in watch mode. 

## Example usage  

```scala
  //... Other imports if requried 

  // Add simple mdoc support for mill
  import $ivy.`de.wayofquality.blended::de.wayofquality.blended.mill.mdoc::0.0.1-1-fdff74`
  import de.wayofquality.mill.mdoc.MDocModule

  object site extends DocusaurusModule with MDocModule {
    // Set the Scala version (required to invoke the proper compiler)
    override def scalaVersion = T(Deps.scalaVersion)
    // The md inputs live in the "docs" folder of the project 
    override def mdocSources = T.sources{ T.workspace / "docs" }
    override def docusaurusSources = T.sources(
      T.workspace / "website"
    )

    // If we are running docusaurus in watch mode we want to replace compiled 
    // mdoc files on the fly - this will NOT build md files for the site
    // Hence we must use `mdoc` once we finished editing.
    override def watchedMDocsDestination: T[Option[Path]] = T(Some(docusaurusBuild().path / "docs"))

    // This is where docusaurus will find the compiled mdocs to BUILD the site
    override def compiledMdocs: Sources = T.sources(mdoc().path)
}

```
