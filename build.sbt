ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.0"

val JooqVersion = "3.19.6"
val PekkoVersion = "1.0.2"
val ScalaTestVersion = "3.2.18"

lazy val root = (project in file("."))
  .settings(
    name := "team-sheets-service"
  )
  .aggregate(domain,queries)

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
  .dependsOn(
    commands,
    events,
    valueObjects % "compile->compile;test->test"
  )

lazy val queries = project
  .settings(
    name := "team-sheets-service-queries",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-persistence-typed" % PekkoVersion,
      "org.apache.pekko" %% "pekko-projection-eventsourced" % "1.0.0",
      "org.apache.pekko" %% "pekko-projection-r2dbc" % "1.0.0",
      "org.jooq" % "jooq" % JooqVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.2" % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % PekkoVersion % Test,
      "org.apache.pekko" %% "pekko-projection-testkit" % "1.0.0" % Test,
      "org.apache.pekko" %% "pekko-stream-testkit" % PekkoVersion % Test,
      "org.scalatest" %% "scalatest-shouldmatchers" % ScalaTestVersion % Test,
      "org.scalatest" %% "scalatest-wordspec" % ScalaTestVersion % Test
    )
  )
  .dependsOn(
    configUtil,
    events,
    valueObjects % "compile->compile;test->test"
  )

lazy val configUtil = (project in file("util/config"))
  .settings(
    name := "config-util",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.3"
    )
  )