import sbt._
object Dependencies {
  val CucumberScala = "io.cucumber" %% "cucumber-scala" % "8.22.0"


  val JooqVersion = "3.19.8"

  val Jooq = "org.jooq" % "jooq" % JooqVersion


  val LogBackClassic = "ch.qos.logback" % "logback-classic" % "1.5.6"


  val PekkoVersion = "1.0.2"

  val PekkoActorTestkitTyped = "org.apache.pekko" %% "pekko-actor-testkit-typed" % PekkoVersion
  val PekkoActorTyped = "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion
  val PekkoPersistenceTestkit = "org.apache.pekko" %% "pekko-persistence-testkit" % PekkoVersion
  val PekkoPersistenceTyped = "org.apache.pekko" %% "pekko-persistence-typed" % PekkoVersion
  val PekkoProjectionEventSourced = "org.apache.pekko" %% "pekko-projection-eventsourced" % "1.0.0"
  val PekkoProjectionR2dbc = "org.apache.pekko" %% "pekko-projection-r2dbc" % "1.0.0"
  val PekkoProjectionTestkit = "org.apache.pekko" %% "pekko-projection-testkit" % "1.0.0"
  val PekkoSerializationJackson = "org.apache.pekko" %% "pekko-serialization-jackson" % PekkoVersion
  val PekkoStreamTestkit = "org.apache.pekko" %% "pekko-stream-testkit" % PekkoVersion


  val ScalaTestVersion = "3.2.18"

  val ScalaTestShouldMatchers = "org.scalatest" %% "scalatest-shouldmatchers" % ScalaTestVersion
  val ScalaTestWordSpec = "org.scalatest" %% "scalatest-wordspec" % ScalaTestVersion


  val TypesafeConfig = "com.typesafe" % "config" % "1.4.3"
}
