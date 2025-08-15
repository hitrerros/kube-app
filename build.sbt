ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val client = (project in file("web-client"))
lazy val srv = (project in file("web-srv"))


lazy val root = (project in file("."))
  .settings(
    name := "kube-app",
    publish / skip := true
  ).aggregate(client,srv)

