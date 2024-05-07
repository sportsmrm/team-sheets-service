import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"
ThisBuild / scalaBinaryVersion := "3"

ThisBuild / scalacOptions ++= Seq("-Wunused:all")

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

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
      PekkoActorTyped
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
      PekkoPersistenceTyped,
      LogBackClassic % Test,
      PekkoPersistenceTestkit % Test,
      PekkoSerializationJackson % Test,
      ScalaTestShouldMatchers % Test,
      ScalaTestWordSpec % Test
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
      PekkoPersistenceTyped,
      Jooq,
      PekkoProjectionEventSourced,
      PekkoProjectionR2dbc,
      LogBackClassic % Test,
      PekkoActorTestkitTyped % Test,
      PekkoProjectionTestkit % Test,
      PekkoStreamTestkit % Test,
      ScalaTestShouldMatchers % Test,
      ScalaTestWordSpec % Test
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
      TypesafeConfig
    )
  )