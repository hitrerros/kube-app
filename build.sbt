ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

lazy val commons =
  (project in file("web-commons")).settings(
      name := "commons",
      scalaVersion :=  "2.13.16"
    )

lazy val client = (project in file("web-client"))
  .dependsOn(commons)
  .settings(
  name := "web-client",
  scalaVersion :=  "2.13.16"
)
lazy val srv = (project in file("web-srv"))
  .dependsOn(commons)
  .settings(
  name := "web-srv",
  scalaVersion :=  "2.13.16"
)

lazy val root = (project in file("."))
  .settings(
    name := "kube-app",
    publish / skip := true
  ).aggregate(commons,client,srv)

