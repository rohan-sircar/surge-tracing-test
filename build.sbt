name := "surge-tracing-test"

version := "0.1"

ThisBuild / scalaVersion := "2.13.5"

fork := true

lazy val common = (project in file("modules/common"))
  .settings(
    libraryDependencies ++= Seq(
      Akka.http,
      akkaHttpPlayJson,
      "org.julienrf" %% "play-json-derived-codecs" % "10.0.2",
      surge,
      gatling,
      gatlingFramework,
      "com.lihaoyi" %% "os-lib" % "0.7.1"
    )
  )

lazy val app1 = (project in file("modules/app1"))
  .dependsOn(common)
  // .aggregateOn(common)
  .enablePlugins(JavaServerAppPackaging)

lazy val app2 = (project in file("modules/app2"))
  .dependsOn(common)
  // .aggregateOn(common)
  .enablePlugins(JavaServerAppPackaging)

lazy val app3 = (project in file("modules/app3"))
  .dependsOn(common)
  // .aggregateOn(common)
  .enablePlugins(JavaServerAppPackaging)
