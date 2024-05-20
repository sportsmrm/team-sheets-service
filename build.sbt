import Dependencies.*

import com.typesafe.sbt.packager.docker._
import sbt.Compile
import sbtprotoc.ProtocPlugin.autoImport.PB

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
      LogbackClassic % Test,
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
      LogbackClassic % Test,
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

lazy val grpcBase = (project in file("grpc"))
  .settings(
    libraryDependencies ++= Seq(
      ScalaPBRuntime,
      ScalaTestShouldMatchers % Test,
      ScalaTestFlatSpec % Test
    ),
    Compile / unmanagedResourceDirectories ++= (Compile / PB.protoSources).value
  )

lazy val grpcServer = (project in file("grpc/server"))
  .enablePlugins(AshScriptPlugin, JavaAppPackaging, DockerPlugin, PekkoGrpcPlugin)
  .dependsOn(grpcBase, commands)
  .settings(
    libraryDependencies ++= Seq(
      LogbackClassic,
      LogbackCore,
      PekkoActorTyped,
      PekkoClusterShardingTyped,
      PekkoClusterTyped,
      PicoCli
    ),
    pekkoGrpcGeneratedSources := Seq(PekkoGrpc.Server),
    pekkoGrpcCodeGeneratorSettings += "scala3_sources",
    Compile / PB.protoSources ++= (grpcBase / Compile / PB.protoSources).value,
    Docker / packageName := "sportsmrm/team-sheets-service",
    dockerUpdateLatest := true,
    dockerBaseImage := "eclipse-temurin:21-jre-alpine",
    Docker / daemonUser := "teamsheets_server",
    dockerCommands += ExecCmd("CMD", "serve","--http-interface", "0.0.0.0")
  )

lazy val specs = project
  .enablePlugins(CucumberPlugin, PekkoGrpcPlugin)
  .dependsOn(grpcBase)
  .settings(
    libraryDependencies ++= Seq(
      CucumberScala
    ),
    pekkoGrpcGeneratedSources := Seq(PekkoGrpc.Client),
    pekkoGrpcCodeGeneratorSettings += "scala3_sources",
    Compile / PB.protoSources ++= (grpcBase / Compile / PB.protoSources).value
  )

lazy val configUtil = (project in file("util/config"))
  .settings(
    name := "config-util",
    libraryDependencies ++= Seq(
      TypesafeConfig
    )
  )