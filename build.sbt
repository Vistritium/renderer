name := "renderer"

version := "1.0"

scalaVersion := "2.11.7"

mainClass in (Compile,run) := Some("window.RendererApp")

mainClass in assembly := Some("window.RendererApp")

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.10"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9"

libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"


javaOptions += "-Xmx1G"
