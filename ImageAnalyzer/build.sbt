import Libs._

lazy val AnalyzerService = (project in file("."))
  .settings(
    name := "ImageAnalyzer",
    organization := "com.feise",
    Compile / mainClass := Some("com.feise.RunServer")
  )
  .settings(
    libraryDependencies ++= Seq(
      catsEffectKernel,
      catsEffectStd,
      catsEffect,
      http4sDsl,
      http4sServer,
      http4sClient,
      http4sCirce,
      circe,
      circeLiteral,
      jwtScala,
      uuid,
      logbackClassic,

      // db
      postgres,
      doobieCore,
      doobiePostgres,
      doobieHikari
    )
  )
