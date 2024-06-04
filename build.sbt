ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val zioDependencies = Seq(
  "dev.zio" %% "zio" % "2.1.1",
  "dev.zio" %% "zio-http" % "3.0.0-RC7",
  "dev.zio" %% "zio-config" % "4.0.2",
  "dev.zio" %% "zio-config-magnolia" % "4.0.2",
  "dev.zio" %% "zio-config-typesafe" % "4.0.2",
  "dev.zio" %% "zio-schema" % "1.1.1",
  "dev.zio" %% "zio-schema-json" % "1.2.0",
  "io.getquill" %% "quill-jdbc" % "4.8.4",
  "io.getquill" %% "quill-jdbc-zio" % "4.8.4",
  "org.postgresql" % "postgresql" % "42.7.3"
)

lazy val root = (project in file("."))
  .settings(
    name := "bitbucket-stats",
    idePackagePrefix := Some("org.treemage"),
    libraryDependencies ++= zioDependencies
  )
