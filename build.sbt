name := """leaky-houses"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

libraryDependencies += "com.firebase" % "firebase-client-jvm" % "2.2.4"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

ivyLoggingLevel := UpdateLogging.Full
