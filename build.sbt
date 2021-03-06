import scoverage.ScoverageKeys
import scala.sys.process._

name := """sundial"""

version := "git describe --tags --dirty --always".!!.stripPrefix("v").trim

enablePlugins(PlayScala, PlayAkkaHttpServer)
disablePlugins(PlayNettyServer)

scalaVersion := "2.12.8"

val prometheusLibVersion = "0.9.0-M5"

val awsVersion = "1.11.445"

scalafmtOnCompile := true

libraryDependencies ++= Seq(
    jdbc,
    guice,
    evolutions,
    ws,
  "org.playframework.anorm" %% "anorm" % "2.6.2",
    "com.amazonaws"                % "aws-java-sdk-emr"              % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-s3"               % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-simpledb"         % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-ecs"              % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-batch"            % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-ses"              % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-logs"             % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-cloudformation"   % awsVersion,
    "com.amazonaws"                % "aws-java-sdk-ec2"              % awsVersion,
    "commons-io"                   % "commons-io"                % "2.4",             // for utility functions
    "org.quartz-scheduler"         % "quartz"                    % "2.2.1",           // used only for CronExpression.getNextValidTimeAfter
    "org.postgresql"               % "postgresql"                % "42.2.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"     % "2.8.11",           // only for JSON serialization for PostgreSQL
    "org.apache.commons"           % "commons-compress"          % "1.9",
    "org.typelevel" %% "cats-effect" % "1.0.0",
    "org.lyranthe.prometheus" %% "client" % prometheusLibVersion,
    "org.lyranthe.prometheus" %% "play26" % prometheusLibVersion,
    "org.scalatestplus.play"       %% "scalatestplus-play"       % "3.1.2" % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test"
  )

routesImport ++= Seq("com.hbc.svc.sundial.v1.Bindables.Core._", "com.hbc.svc.sundial.v1.Bindables.Models._")

javaOptions in Test ++= Seq(
  "-Dconfig.file=conf/application.test.conf"
)


import com.typesafe.sbt.packager.docker._
dockerBaseImage := "openjdk:8-jre"

dockerCommands := (
  Seq(dockerCommands.value.head) ++ Seq(
    ExecCmd("RUN", "apt-get", "update"),
    ExecCmd("RUN", "apt-get", "install", "-y", "graphviz"),
    ExecCmd("RUN", "apt-get", "clean"),
    ExecCmd("RUN", "rm", "-rf", "/var/lib/apt/lists/*", "/tmp/*", "/var/tmp/*")
  ) ++ dockerCommands.value.tail)

bashScriptExtraDefines ++= Seq(
  """addJava "-Dplay.evolutions.db.default.autoApply=true"""",
  """addJava "-Dconfig.resource=application.prod.conf""""
)
