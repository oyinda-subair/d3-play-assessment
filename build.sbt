name := """d3-play-assessment"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

val slickVersion = "3.3.2"

libraryDependencies += guice

libraryDependencies ++= Seq(
//  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
//  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.slf4j" % "slf4j-nop" % "1.7.26",
//  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
  "com.pauldijou" %% "jwt-play" % "0.19.0",
  "com.pauldijou" %% "jwt-core" % "0.19.0",
  "com.auth0" % "jwks-rsa" % "0.8.2",
  "com.auth0" % "java-jwt" % "3.8.1",
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2",
  "com.h2database" % "h2" % "1.4.199" // replace `${H2_VERSION}` with an actual version number
)

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)