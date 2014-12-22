name := """EveATS"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "com.fasterxml" % "aalto-xml" % "0.9.9",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

PlayKeys.playWatchService := play.sbtplugin.run.PlayWatchService.sbt(pollInterval.value)

scalacOptions ++= Seq("-feature")