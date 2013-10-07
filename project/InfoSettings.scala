import sbt._
import Keys._

object InfoSettings {
  
  def all = Seq[Project.Setting[_]](versioninfo)

  val versioninfo = sourceGenerators in Compile <+= (sourceManaged in Compile, version, name) map { (d, v, n) =>
    val file = d / "info.scala"
    IO.write(
      file,
      """package spray.aws
        |
        |object VersionInfo {
        |  val version = "%s"
        |  val name = "spray-aws"
        |}
      """.stripMargin.format(v)
    )
    Seq(file)
  }

}