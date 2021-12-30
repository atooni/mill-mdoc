package de.wayofquality.mill.mdoc

import mill._
import mill.scalalib._
import mill.modules.Jvm
import os.Path

trait MDocModule extends ScalaModule {

  def scalaMdocVersion : T[String] = T("2.2.24")

  def scalaMdocDep : T[Dep] = T(ivy"org.scalameta::mdoc:${scalaMdocVersion()}")

  def watchedMDocsDestination: T[Option[Path]] = T(None)

  override def ivyDeps: T[Agg[Dep]] = T {
    super.ivyDeps() ++ Agg(scalaMdocDep())
  }

  // where do the mdoc sources live ?
  def mdocSources = T.sources { super.millSourcePath }

  def mdoc : T[PathRef] = T {

    val cp = runClasspath().map(_.path)

    val dir = T.dest.toIO.getAbsolutePath
    val dirParams = mdocSources().map(pr => Seq(s"--in", pr.path.toIO.getAbsolutePath, "--out",  dir)).iterator.flatten.toSeq

    Jvm.runLocal("mdoc.Main", cp, dirParams)

    PathRef(T.dest)
  }

  def mdocWatch() = T.command {

    watchedMDocsDestination() match {
      case None => throw new Exception("watchedMDocsDestination is not set, so we dant know where to put compiled md files")
      case Some(p) =>
        val cp = runClasspath().map(_.path)
        val dirParams = mdocSources().map(pr => Seq(s"--in", pr.path.toIO.getAbsolutePath, "--out",  p.toIO.getAbsolutePath)).iterator.flatten.toSeq
        Jvm.runLocal("mdoc.Main", cp, dirParams ++ Seq("--watch"))
    }

  }
}

