name := """EveATS"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.zeromq" % "jeromq" % "0.3.4",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "com.fasterxml" % "aalto-xml" % "0.9.9",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

scalacOptions ++= Seq("-feature")
