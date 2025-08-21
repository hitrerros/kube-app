ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

val circeVersion = "0.14.9"
val catsEffectVersion = "3.5.4"

// depedendencies
libraryDependencies ++= Seq(
  // circe
  "io.circe"   %% "circe-generic"      % circeVersion,
  "io.circe"   %% "circe-parser"       % circeVersion,
  // cats effects
  "org.typelevel" %% "cats-effect"     % catsEffectVersion
)
