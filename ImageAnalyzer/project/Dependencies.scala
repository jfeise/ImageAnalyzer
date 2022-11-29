import sbt._

object V {
  val http4s = "0.23.12"
  val circe = "0.14.2"
  val uuid = "0.3.1"
  val config = "1.4.2"
  val pureConfig = "0.17.1"
  val logbackClassic = "1.2.11"
  val slf4J = "1.7.36"
  val doobie = "1.0.0-RC2"
  val jwtScala = "5.0.0"
  val catsEffect = "3.3.12"

  val postgres = "42.3.4"
}

object Libs {
  val catsEffectKernel       = "org.typelevel"         %% "cats-effect-kernel"       % V.catsEffect
  val catsEffectStd          = "org.typelevel"         %% "cats-effect-std"          % V.catsEffect
  val catsEffect             = "org.typelevel"         %% "cats-effect"              % V.catsEffect
  val http4sDsl              = "org.http4s"            %% "http4s-dsl"               % V.http4s
  val http4sServer           = "org.http4s"            %% "http4s-blaze-server"      % V.http4s
  val http4sClient           = "org.http4s"            %% "http4s-blaze-client"      % V.http4s
  val http4sCirce            = "org.http4s"            %% "http4s-circe"             % V.http4s
  val circe                  = "io.circe"              %% "circe-generic"            % V.circe
  val circeLiteral           = "io.circe"              %% "circe-literal"            % V.circe
  val uuid                   = "io.jvm.uuid"           %% "scala-uuid"               % V.uuid
  val jwtScala               = "com.pauldijou"         %% "jwt-circe"                % V.jwtScala
  val logbackClassic         = "ch.qos.logback"         % "logback-classic"          % V.logbackClassic
  val slf4J                  = "org.slf4j"              % "slf4j-api"                % V.slf4J

  // Database
  val postgres               = "org.postgresql"         % "postgresql"               % V.postgres
  val doobieCore             = "org.tpolecat"          %% "doobie-core"              % V.doobie
  val doobiePostgres         = "org.tpolecat"          %% "doobie-postgres"          % V.doobie
  val doobieHikari           = "org.tpolecat"          %% "doobie-hikari"            % V.doobie

}
