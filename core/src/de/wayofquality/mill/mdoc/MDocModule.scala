package de.wayofquality.mill.mdoc

trait MDocModule extends ScalaModule {

  def scalaMdocVersion : T[String] = T("2.2.24")

  def scalaMDocDep : T[Dep] = T(ivy"org.scalameta::mdoc:${scalaMdocVersion()}")

  def watchedMDocsDestination: T[Option[Path]] = T(None)

  override def ivyDeps: T[Agg[Dep]] = T {
    super.ivyDeps() ++ Agg(scalaMDocDep())
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

    watchedMDocsDestination().foreach{ p =>
      val cp = runClasspath().map(_.path)
      val dirParams = mdocSources().map(pr => Seq(s"--in", pr.path.toIO.getAbsolutePath, "--out",  p.toIO.getAbsolutePath)).iterator.flatten.toSeq
      Jvm.runLocal("mdoc.Main", cp, dirParams ++ Seq("--watch"))
    }

  }
}

