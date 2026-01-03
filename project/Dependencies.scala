import sbt.*
object Dependencies {
  val CucumberScala = "io.cucumber" %% "cucumber-scala" % "8.38.0"

  val GrpcVersion = "1.78.0"
  val GrpcCore = "io.grpc" % "grpc-core" % GrpcVersion
  val GrpcNetty = "io.grpc" % "grpc-netty" % GrpcVersion
  val GrpcStub = "io.grpc" % "grpc-stub" % GrpcVersion

  val JooqVersion = "3.20.10"
  val Jooq = "org.jooq" % "jooq" % JooqVersion

  val LogbackVersion = "1.5.23"
  val LogbackClassic = "ch.qos.logback" % "logback-classic" % LogbackVersion
  val LogbackCore = "ch.qos.logback" % "logback-core" % LogbackVersion

  val PekkoVersion = "1.4.0"
  val PekkoActorTestkitTyped = "org.apache.pekko" %% "pekko-actor-testkit-typed" % PekkoVersion
  val PekkoActorTyped = "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion
  val PekkoClusterShardingTyped = "org.apache.pekko" %% "pekko-cluster-sharding-typed" % PekkoVersion
  val PekkoClusterTyped = "org.apache.pekko" %% "pekko-cluster-typed" % PekkoVersion
  val PekkoDiscovery = "org.apache.pekko" %% "pekko-discovery" % PekkoVersion
  val PekkoPersistenceR2dbc = "org.apache.pekko" %% "pekko-persistence-r2dbc" % "1.1.0"
  val PekkoPersistenceTestkit = "org.apache.pekko" %% "pekko-persistence-testkit" % PekkoVersion
  val PekkoPersistenceTyped = "org.apache.pekko" %% "pekko-persistence-typed" % PekkoVersion
  val PekkoProjectionEventSourced = "org.apache.pekko" %% "pekko-projection-eventsourced" % "1.1.0"
  val PekkoProjectionR2dbc = "org.apache.pekko" %% "pekko-projection-r2dbc" % "1.1.0"
  val PekkoProjectionTestkit = "org.apache.pekko" %% "pekko-projection-testkit" % "1.1.0"
  val PekkoProtobufV3 = "org.apache.pekko" %% "pekko-protobuf-v3" % PekkoVersion
  val PekkoSerializationJackson = "org.apache.pekko" %% "pekko-serialization-jackson" % PekkoVersion
  val PekkoStream = "org.apache.pekko" %% "pekko-stream" % PekkoVersion
  val PekkoStreamTestkit = "org.apache.pekko" %% "pekko-stream-testkit" % PekkoVersion

  val PicoCli = "info.picocli" % "picocli" % "4.7.7"

  val ScalaPBVersion = "0.11.20"
  val ScalaPBRuntime = "com.thesamet.scalapb" %% "scalapb-runtime" % ScalaPBVersion
  val ScalaPBRuntimeGrpc = "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % ScalaPBVersion

  val ScalaTestVersion = "3.2.19"
  val ScalaTestFlatSpec = "org.scalatest" %% "scalatest-flatspec" % ScalaTestVersion
  val ScalaTestShouldMatchers = "org.scalatest" %% "scalatest-shouldmatchers" % ScalaTestVersion
  val ScalaTestWordSpec = "org.scalatest" %% "scalatest-wordspec" % ScalaTestVersion

  val TypesafeConfig = "com.typesafe" % "config" % "1.4.5"
}
