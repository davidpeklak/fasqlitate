import bintray.Keys._

scalaVersion := "2.11.8"

lazy val commonSettings = Seq(
  version in ThisBuild := "0.1.1",
  organization in ThisBuild := "com.github.davidpeklak"
)

lazy val root = (project in file(".")).
  settings(commonSettings ++ bintrayPublishSettings: _*).
  settings(
    name := "fasqlitate",
    description := "facilitate work with sql",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    publishMavenStyle := true,
    repository in bintray := "maven",
    bintrayOrganization in bintray := None
  )
