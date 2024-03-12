ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.0"

val PekkoVersion = "1.0.2"
val ScalaTestVersion = "3.2.18"

lazy val root = (project in file("."))
  .settings(
    name := "team-sheets-service"
  )
  .aggregate(domain)

lazy val valueObjects = (project in file("value-objects"))
  .settings(
    name := "team-sheets-service-value-objects"
  )

lazy val commands = project
  .settings(
    name := "team-sheets-service-commands",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion
    )
  )
  .dependsOn(valueObjects)

lazy val events = project
  .settings(
    name := "teams-sheets-service-events"
  )
  .dependsOn(valueObjects)

lazy val domain = project
  .settings(
    name := "team-sheets-service-domain",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-persistence-typed" % PekkoVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.2" % Test,
      "org.apache.pekko" %% "pekko-persistence-testkit" % PekkoVersion % Test,
      "org.apache.pekko" %% "pekko-serialization-jackson" % PekkoVersion % Test,
      "org.scalatest" %% "scalatest-shouldmatchers" % ScalaTestVersion % Test,
      "org.scalatest" %% "scalatest-wordspec" % ScalaTestVersion % Test
    )
  )
  .dependsOn(commands, events)
