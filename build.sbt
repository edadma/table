ThisBuild / licenses += "ISC" -> url("https://opensource.org/licenses/ISC")
ThisBuild / versionScheme := Some("semver-spec")

lazy val table = crossProject(JSPlatform, JVMPlatform, NativePlatform).in(file(".")).
  settings(
    name := "table",
    version := "1.0.2",
    scalaVersion := "3.1.3",
    scalacOptions ++=
      Seq(
        "-deprecation", "-feature", "-unchecked",
        "-language:postfixOps", "-language:implicitConversions", "-language:existentials", "-language:dynamics",
      ),
    organization := "io.github.edadma",
    githubOwner := "edadma",
    githubRepository := "table",
    mainClass := Some("io.github.edadma.table.Main"),
    Test / mainClass := Some("io.github.edadma.table.Main"),
//    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.3" % "test",
    publishMavenStyle := true,
    Test / publishArtifact := false,
    licenses := Seq("ISC" -> url("https://opensource.org/licenses/ISC"))
  ).
  jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
  ).
  nativeSettings(
    nativeLinkStubs := true,
  ).
  jsSettings(
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
    Test / scalaJSUseMainModuleInitializer := true,
    Test / scalaJSUseTestModuleInitializer := false,
//    Test / scalaJSUseMainModuleInitializer := false,
//    Test / scalaJSUseTestModuleInitializer := true,
    scalaJSUseMainModuleInitializer := false
  )
