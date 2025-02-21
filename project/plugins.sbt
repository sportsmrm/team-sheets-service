addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.13.0")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.17"

addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.1.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
