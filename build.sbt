name := """d3-play-assessment"""
organization := "com.example"
version := "1.0"

val slickVersion = "3.3.2"
val playVersion = "4.0.2"

val slick = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
val slickCodegen = "com.typesafe.slick" %% "slick-codegen" % "3.2.0"

val playSlick =  "com.typesafe.play" %% "play-slick" % playVersion
val playSlickEvolution =  "com.typesafe.play" %% "play-slick-evolutions" % playVersion

val paulJwtPlay = "com.pauldijou" %% "jwt-play" % "0.19.0"
val paulJwtCore = "com.pauldijou" %% "jwt-core" % "0.19.0"

val auth0JwksRSA = "com.auth0" % "jwks-rsa" % "0.8.2"
val auth0JavaJwt = "com.auth0" % "java-jwt" % "3.8.1"

val bcyrpt = "org.mindrot" % "jbcrypt" % "0.3m"

val h2Database = "com.h2database" % "h2" % "1.4.199"
val postgres = "org.postgresql" % "postgresql" % "42.2.6"
val forkLift = "com.liyaos" %% "scala-forklift-slick" % "0.3.1"
val slickMigrate = "io.github.nafg" %% "slick-migration-api" % "0.4.0"

val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
val scalactic = "org.scalactic" %% "scalactic" % "3.0.8"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % "test"

val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.26"
val qosClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
val qosCore = "ch.qos.logback" % "logback-core" % "1.2.3"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Xfatal-warnings"
  ),
  resolvers ++= Seq(
    "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.bintrayRepo("naftoligug", "maven"),
    "bintray" at "https://api.bintray.com/maven/naftoligug/maven/slick-migration-api",
    Resolver.jcenterRepo,
    "Artima Maven Repository" at "http://repo.artima.com/releases"
  )
)

//lazy val root = (project in file(".")).enablePlugins(PlayScala)
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      guice,
      slick,
      slickHikaricp,
      slickCodegen,
      playSlick,
      playSlickEvolution,
      paulJwtPlay,
      paulJwtCore,
      auth0JwksRSA,
      auth0JavaJwt,
      bcyrpt,
      postgres,
      h2Database,
      slickMigrate,
      scalaTestPlus,
      scalactic,
      scalaTest,
      specs2 % Test
    )
  )
