import sbt._
import Keys._
// sbt-assembly
import sbtassembly.Plugin._
import AssemblyKeys._
// sbt-revolver
import spray.revolver.RevolverPlugin._
// sbt-scalariform
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
// sbt-dependecy-graph
import net.virtualvoid.sbt.graph.Plugin._
// sbt-release
import sbtrelease.{ Version => ReleaseVersion, _ }
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.Utilities._

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
    "com.amazonaws"                          %  "aws-java-sdk"                % "1.6.1",
    "com.typesafe.akka"                      %% "akka-actor"                  % "2.2.1",
    "com.typesafe.akka"                      %% "akka-slf4j"                  % "2.2.1",
    "com.typesafe"                           %% "scalalogging-slf4j"          % "1.0.1",
    "io.argonaut"                            %% "argonaut"                    % "6.0.1",
    "io.spray"                               %  "spray-client"                % "1.2+",
    "org.scalaz"                             %% "scalaz-core"                 % "7.0.4",
    //"org.scalaz"                             %% "scalaz-effect"               % "7.0.4",
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
    "org.slf4j"                              %  "slf4j-api"                   % "1.7.5",
    // test dependencies
    //
    "ch.qos.logback"                         %  "logback-classic"             % "1.0.13"  % "test",
    "ch.qos.logback"                         %  "logback-core"                % "1.0.13"  % "test"
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
    buildSettings ++
    compileSettings ++
    scalariformSettings ++
    Revolver.settings ++
    assemblySettings ++
    graphSettings ++
    releaseSettings ++
    testSettings ++
    InfoSettings.all
  //

  lazy val main = Project(
    id = "spray-aws-main",
    base = file("."),
    settings = defaultSettings ++ Seq(
      resolvers ++= resolverSettings,
      libraryDependencies ++= dependencies,
      ScalariformKeys.preferences := formattingSettings,
      fork in run := true,
      fork in Test := true,
      fork in testOnly := true,
      connectInput in run := true,
      javaOptions in run ++= forkedJvmOption,
      javaOptions in Test ++= forkedJvmOption,
      publishArtifact in (Compile, packageBin) := false
    )
  )
}
