import $exec.plugins
import $exec.shared
import mill._
import mill.scalalib._
import de.wayofquality.mill.mdoc._
import os.Path

def verify()  = T.command {
  val res : Path = simple.mdoc().path / "HelloWorld.md"
  if (!os.exists(res))
    throw new Exception(s"Expected mdoc file <${res.toIO.getAbsolutePath}> does not exist")

  val content = os.read.lines(res)
  if (!content.exists(_.contains("""// hello: String = "Hello Mdoc!"""")))
    throw new Exception(s"Generated md file does not contain expected REPL output")

}

object simple extends MDocModule {
  override def mdocSources = T.sources(build.millSourcePath / "docs")
  override def scalaVersion: T[String] = "2.13.7"
}
