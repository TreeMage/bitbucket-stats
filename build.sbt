import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / version := "0.1.0-SNAPSHOT"

val commonSettings = Seq(
  scalaVersion := "3.4.2",
  idePackagePrefix := Some("org.treemage")
)

lazy val zioDependencies = Seq(
  "dev.zio" %% "zio" % "2.1.1",
  "dev.zio" %% "zio-http" % "3.0.0-RC7",
  "dev.zio" %% "zio-config" % "4.0.2",
  "dev.zio" %% "zio-config-magnolia" % "4.0.2",
  "dev.zio" %% "zio-config-typesafe" % "4.0.2",
  "dev.zio" %% "zio-schema" % "1.2.0",
  "dev.zio" %% "zio-schema-json" % "1.2.0",
  "io.getquill" %% "quill-jdbc" % "4.8.4",
  "io.getquill" %% "quill-jdbc-zio" % "4.8.4",
  "org.postgresql" % "postgresql" % "42.7.3"
)

lazy val root = (project in file("."))
  .aggregate(backend, frontend, shared.jvm, shared.js)

lazy val backend = (project in file("backend"))
  .settings(commonSettings)
  .settings(
    name := "bitbucket-stats-backend",
    libraryDependencies ++= zioDependencies
  )
  .dependsOn(shared.jvm)

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "bitbucket-stats-frontend",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("org.treemage"))
        )
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0",
      "com.raquo" %%% "laminar" % "17.0.0",
      "dev.zio" %%% "zio" % "2.1.1",
      "dev.zio" %%% "zio-http" % "3.0.0-RC7"
    )
  )
  .dependsOn(shared.js)

lazy val shared = // (project in file("shared"))
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("shared-domain"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings)
    .settings(
      name := "bitbucket-stats-shared",
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio" % "2.1.1",
        "dev.zio" %% "zio-schema" % "1.2.0",
        "dev.zio" %% "zio-schema-derivation" % "1.2.0",
        "dev.zio" %% "zio-http" % "3.0.0-RC7",
        "io.getquill" %% "quill-jdbc" % "4.8.4"
      )
    )
