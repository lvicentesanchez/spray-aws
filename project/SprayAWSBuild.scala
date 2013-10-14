import sbt._
import Keys._
// sbt-assembly
import sbtassembly.Plugin._
import AssemblyKeys._
// sbt-buildinfo
import sbtbuildinfo.Plugin._
// sbt-dependecy-graph
import net.virtualvoid.sbt.graph.Plugin._
// sbt-scalariform
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
// sbt-release
import sbtrelease.{ Version => ReleaseVersion, _ }
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.Utilities._
// sbt-revolver
import spray.revolver.RevolverPlugin._

object SprayAWSBuild extends Build {

  lazy val appName = "spray-aws"

  lazy val buildSettings = Seq(
    name := appName,
    organization := "io.github.lvicentesanchez",
    scalaVersion := "2.10.3",
    scalaBinaryVersion := "2.10"
  )

  lazy val compileSettings = Seq(
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")
  )

  // dependencies
  //
  lazy val dependencies = mainDependencies ++ testDependencies ++ versionBumpDependencies

  lazy val mainDependencies = Seq(
    "com.amazonaws"                          %  "aws-java-sdk"                % "1.6.1" exclude("commons-logging", "commons-logging"),
    "com.typesafe.akka"                      %% "akka-actor"                  % "2.2.1",
    "com.typesafe.akka"                      %% "akka-slf4j"                  % "2.2.1",
    "com.typesafe"                           %% "scalalogging-slf4j"          % "1.0.1",
    "io.argonaut"                            %% "argonaut"                    % "6.0.1",
    "io.spray"                               %  "spray-client"                % "1.2+",
    "org.scalaz"                             %% "scalaz-core"                 % "7.0.4",
    "org.slf4j"                              %  "jcl-over-slf4j"              % "1.7.5",
    "org.typelevel"                          %% "scalaz-contrib-210"          % "0.2-SNAPSHOT"
  )

  lazy val testDependencies = Seq(
    "com.typesafe.akka"                      %% "akka-testkit"                % "2.2.1"   % "test",
    "io.spray"                               %  "spray-testkit"               % "1.2+"    % "test",
    "org.scalacheck"                         %% "scalacheck"                  % "1.10.1"  % "test",
    "org.specs2"                             %% "specs2"                      % "2.2.3"   % "test"
  )

  lazy val versionBumpDependencies =  Seq(
    "org.apache.httpcomponents"              %  "httpclient"                  % "4.3" exclude("commons-logging", "commons-logging"),
    "org.apache.httpcomponents"              %  "httpcore"                    % "4.3" exclude("commons-logging", "commons-logging"),
    "org.slf4j"                              %  "slf4j-api"                   % "1.7.5",
    "ch.qos.logback"                         %  "logback-classic"             % "1.0.13",
    "ch.qos.logback"                         %  "logback-core"                % "1.0.13",
    "org.slf4j"                              %  "slf4j-api"                   % "1.7.5"
  )
  //

  lazy val forkedJvmOption = Seq(
    "-server",
    "-Dfile.encoding=UTF8",
    "-Duser.timezone=GMT",
    "-Xss1m",
    "-Xms1536m",
    "-Xmx1536m",
    "-XX:+CMSClassUnloadingEnabled",
    "-XX:MaxPermSize=384m",
    "-XX:ReservedCodeCacheSize=256m",
    "-XX:+DoEscapeAnalysis",
    "-XX:+UseConcMarkSweepGC",
    "-XX:+UseParNewGC",
    "-XX:+UseCodeCacheFlushing",
    "-XX:+UseCompressedOops"
  )

  lazy val formattingSettings =
    FormattingPreferences()
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, false)
      .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
      .setPreference(CompactControlReadability, false)
      .setPreference(CompactStringConcatenation, false)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(FormatXml, true)
      .setPreference(IndentLocalDefs, false)
      .setPreference(IndentPackageBlocks, true)
      .setPreference(IndentSpaces, 2)
      .setPreference(IndentWithTabs, false)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
      .setPreference(PreserveSpaceBeforeArguments, false)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
  

  lazy val mainProjectRef = LocalProject("spray-aws-main")
  
  lazy val resolverSettings = Seq(
    "sonatype oss releases" at "http://oss.sonatype.org/content/repositories/releases/",
    "sonatype oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    "spray nightlies" at "http://nightlies.spray.io/",
    "spray releases" at "http://repo.spray.io",
    "typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
    "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  )

  lazy val testSettings = Seq(testOptions in Test += Tests.Argument("exclude", "intensive", "showtimes"))

  // Default settings
  //
  lazy val defaultSettings =
    Defaults.defaultSettings ++
    assemblySettings ++
    buildInfoSettings ++
    buildSettings ++
    compileSettings ++
    graphSettings ++
    Revolver.settings ++
    scalariformSettings ++
    testSettings
  //

  lazy val main = Project(
    id = "spray-aws-main",
    base = file("."),
    settings = defaultSettings ++ Seq(
      resolvers ++= resolverSettings,
      libraryDependencies ++= dependencies,
      // Build Info
      //
      sourceGenerators in Compile <+= buildInfo,
      buildInfoKeys := Seq[BuildInfoKey](name, version),
      //
      ScalariformKeys.preferences := formattingSettings,
      fork in run := true,
      fork in Test := true,
      fork in testOnly := true,
      connectInput in run := true,
      javaOptions in run ++= forkedJvmOption,
      javaOptions in Test ++= forkedJvmOption,
      jarName in assembly <<= (name, version) map ( (n, v) => s"$n-$v.jar" ),
      mergeStrategy in assembly <<= (mergeStrategy in assembly)((default: String => MergeStrategy) => _  match {
          case "rootdoc.txt" => MergeStrategy.discard
          case x => default(x)
      }),
      publishArtifact in (Compile, packageBin) := false
    )
  )
}
