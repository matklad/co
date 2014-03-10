name := "co"

version := "1.0"

autoCompilerPlugins := true

libraryDependencies +=
  compilerPlugin("org.scala-lang.plugins" % "continuations" % scalaVersion.value)

scalacOptions ++= Seq("-P:continuations:enable", "-feature")

