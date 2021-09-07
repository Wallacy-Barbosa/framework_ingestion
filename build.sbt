// To build Scala, Java and more, including your settings and dependencies

// Defines the project name, and project version
val PROJECT_NAME = "framework_ingestion"
val PROJECT_VERSION = "1.0.0"

val PROJECT_LIBRARIES = Seq(
    // https://mvnrepository.com/artifact/org.apache.spark/spark-core
    "org.apache.spark" %% "spark-core" % "2.4.0",
    // https://mvnrepository.com/artifact/org.apache.spark/spark-sql
    "org.apache.spark" %% "spark-sql" % "2.4.0" % "provided",
    // https://mvnrepository.com/artifact/com.typesafe/config
    "com.typesafe" % "config" % "1.4.0",

    // https://mvnrepository.com/artifact/io.circe/circe-core
    "io.circe" %% "circe-core" % "0.12.0-M3",
    // https://mvnrepository.com/artifact/io.circe/circe-parser
    "io.circe" %% "circe-parser" % "0.12.0-M3",
    // https://mvnrepository.com/artifact/io.circe/circe-generic
    "io.circe" %% "circe-generic" % "0.12.0-M3"
)

lazy val setting = (project in file(".")).settings(
    ThisBuild / organization := "br.com.claro",
    ThisBuild / name := PROJECT_NAME,
    ThisBuild / version := PROJECT_VERSION,
    ThisBuild / scalaVersion := "2.11.12",
    ThisBuild / libraryDependencies ++= PROJECT_LIBRARIES,

    // Dealing with duplicate libraries
    assembly / assemblyMergeStrategy := {
        case PathList("META-INF", xs@_*) => MergeStrategy.discard // Simply discards matching files
        case x => MergeStrategy.first // Picks the first of the matching files in classpath order
    },

    // Defines the path and name of the .jar file
    assembly / assemblyOutputPath := new File(baseDirectory.value / "resource" / PROJECT_NAME + ".jar")

)