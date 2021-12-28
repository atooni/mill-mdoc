import $exec.plugins
import $exec.shared
import mill._
import mill.scalalib._
import de.wayofquality.mill.mdoc._
import os.Path

def verify() = T.command {
  val docs = simple.mdoc().path
  throw new RuntimeException("Boom")
}

object simple extends MDocModule {
  override def scalaVersion: T[String] = "2.13.7"
}
