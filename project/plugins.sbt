// Comment to get more information during initialization
//
logLevel := Level.Warn

// Resolvers
//
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

// Assembly
//
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.0")

// Build info
//
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.2.5")

// Dependency graph
//
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

// Releases
//
addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8")

// Revolver
//
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")

// Scalariform
//
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.0")

// Sublime2 + Ensime plugins
//
addSbtPlugin("com.orrsella" % "sbt-sublime" % "1.0.8")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.1.2")

// Update plugin
//
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.2")
