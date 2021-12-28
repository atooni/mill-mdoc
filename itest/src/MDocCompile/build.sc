import $exec.plugins
import $exec.shared
import mill._
import mill.scalalib._
import de.wayofquality.mill.mdoc._
import os.Path

def verify()  = T.command {
  val res = simple.mdoc().path
  if (!os.exists(res / "HelloWorld.md" )) throw new Exception("Boom")
}

object simple extends MDocModule {
  override def mdocSources = T.sources(build.millSourcePath / "docs")
  override def scalaVersion: T[String] = "2.13.7"
}
