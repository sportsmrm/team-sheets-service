import sbt._
object Dependencies {
  val CucumberScala = "io.cucumber" %% "cucumber-scala" % "8.22.0"


  val JooqVersion = "3.19.8"

  val Jooq = "org.jooq" % "jooq" % JooqVersion


  val LogbackVersion = "1.5.6"

  val LogbackClassic = "ch.qos.logback" % "logback-classic" % LogbackVersion
  val LogbackCore = "ch.qos.logback" % "logback-core" % LogbackVersion

  val PekkoVersion = "1.1.2"

  val PekkoActorTestkitTyped = "org.apache.pekko" %% "pekko-actor-testkit-typed" % PekkoVersion
  val PekkoActorTyped = "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion
  val PekkoClusterShardingTyped = "org.apache.pekko" %% "pekko-cluster-sharding-typed" % PekkoVersion
  val PekkoClusterTyped = "org.apache.pekko" %% "pekko-cluster-typed" % PekkoVersion
  val PekkoDiscovery = "org.apache.pekko" %% "pekko-discovery" % PekkoVersion
  val PekkoPersistenceR2dbc = "org.apache.pekko" %% "pekko-persistence-r2dbc" % "1.0.0"
  val PekkoPersistenceTestkit = "org.apache.pekko" %% "pekko-persistence-testkit" % PekkoVersion
  val PekkoPersistenceTyped = "org.apache.pekko" %% "pekko-persistence-typed" % PekkoVersion
  val PekkoProjectionEventSourced = "org.apache.pekko" %% "pekko-projection-eventsourced" % "1.0.0"
  val PekkoProjectionR2dbc = "org.apache.pekko" %% "pekko-projection-r2dbc" % "1.0.0"
  val PekkoProjectionTestkit = "org.apache.pekko" %% "pekko-projection-testkit" % "1.0.0"
  val PekkoProtobufV3 = "org.apache.pekko" %% "pekko-protobuf-v3" % PekkoVersion
  val PekkoSerializationJackson = "org.apache.pekko" %% "pekko-serialization-jackson" % PekkoVersion
  val PekkoStream = "org.apache.pekko" %% "pekko-stream" % PekkoVersion
  val PekkoStreamTestkit = "org.apache.pekko" %% "pekko-stream-testkit" % PekkoVersion


  val PicoCli = "info.picocli" % "picocli" % "4.7.6"


  val ScalaPBRuntime = "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.13"


  val ScalaTestVersion = "3.2.18"

  val ScalaTestFlatSpec = "org.scalatest" %% "scalatest-flatspec" % ScalaTestVersion
  val ScalaTestShouldMatchers = "org.scalatest" %% "scalatest-shouldmatchers" % ScalaTestVersion
  val ScalaTestWordSpec = "org.scalatest" %% "scalatest-wordspec" % ScalaTestVersion


  val TypesafeConfig = "com.typesafe" % "config" % "1.4.3"
}
