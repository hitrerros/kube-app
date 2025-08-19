ThisBuild / version := "0.1.0-SNAPSHOT"

// depedendency versions
val http4sVersion = "0.23.25"
val catsEffectVersion   = "3.5.4"
val doobieVersion = "1.0.0-RC1"
val fs2KafkaVersion   = "3.5.1"

// depedendencies
libraryDependencies ++= Seq(
  // http4s
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-server"       % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion,
  // doobie
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari"   % doobieVersion, // for connection pool
  // logback
  "ch.qos.logback" % "logback-classic" % "1.5.13",
  // config
  "com.typesafe" % "config" % "1.3.0",
  // cats effects
  "org.typelevel" %% "cats-effect"       % catsEffectVersion,
  // fs-kafka
  "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion,
)

// FAT Jar assembly
enablePlugins(AssemblyPlugin)
assembly / assemblyJarName := s"${name.value}-${version.value}.jar"
assembly / mainClass := Some("org.AppServer")
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ =>
    // For all the other files, use the default sbt-assembly merge strategy
    MergeStrategy.first
}