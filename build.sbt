name := """oasys"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.21",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.1"
)