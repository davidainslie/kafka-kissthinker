import io.gatling.sbt.GatlingPlugin
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin._

object Build extends Build {
  val moduleName = "kafka-kissthinker"

  val root = Project(id = moduleName, base = file(".")).enablePlugins(GatlingPlugin)
    .configs(IntegrationTest)
    .settings(Revolver.settings)
    .settings(Defaults.itSettings: _*)
    .settings(javaOptions in Test += "-Dconfig.resource=application.test.conf")
    .settings(run := (run in Runtime).evaluated) // Required to stop Gatling plugin overriding the default "run".
    .settings(
      name := moduleName,
      organization := "com.kissthinker",
      version := "1.0.0-SNAPSHOT",
      scalaVersion := "2.11.8",
      scalacOptions ++= Seq(
        "-feature",
        "-language:implicitConversions",
        "-language:higherKinds",
        "-language:existentials",
        "-language:reflectiveCalls",
        "-language:postfixOps",
        "-Yrangepos",
        "-Yrepl-sync"
      ),
      ivyScala := ivyScala.value map {
        _.copy(overrideScalaVersion = true)
      },
      resolvers ++= Seq(
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
        "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
        "Kamon Repository" at "http://repo.kamon.io"
      )
    )
    .settings(libraryDependencies ++= {
      val `specs2-version` = "3.7.3"
      val `akka-version` = "2.4.2"
      val `play-version` = "2.5.0"
      val `kafka-version` = "0.9.0.1"
      val `gatling-version` = "2.1.7"

      Seq(
        "com.typesafe.play" %% "play-ws" % `play-version` withSources(),
        "org.apache.kafka" %% "kafka" % `kafka-version` withSources(),
        "org.scalactic" %% "scalactic" % "2.2.6" withSources(),
        "ch.qos.logback" % "logback-classic" % "1.1.3" withSources(),
        "org.slf4j" % "jcl-over-slf4j" % "1.7.12" withSources(),
        "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.2" withSources()
      ) ++ Seq(
        "io.gatling.highcharts" % "gatling-charts-highcharts" % `gatling-version` % IntegrationTest withSources(),
        "io.gatling" % "gatling-test-framework" % `gatling-version` % IntegrationTest withSources(),
        "org.specs2" %% "specs2-core" % `specs2-version` % Test withSources(),
        "org.specs2" %% "specs2-mock" % `specs2-version` % Test withSources(),
        "org.specs2" %% "specs2-matcher-extra" % `specs2-version` % Test withSources(),
        "org.specs2" %% "specs2-junit" % `specs2-version` % Test withSources(),
        "org.scalatest" %% "scalatest" % "2.2.4" % Test withSources(),
        "com.typesafe.akka" %% "akka-testkit" % `akka-version` % Test withSources(),
        "com.typesafe.play" %% "play-test" % `play-version` % Test withSources(),
        "com.typesafe.play" %% "play-specs2" % `play-version` % Test withSources()
      )
    })
}