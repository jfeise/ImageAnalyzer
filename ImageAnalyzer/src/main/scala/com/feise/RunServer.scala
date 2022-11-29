package com.feise

import cats.effect._
import org.http4s
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server._

object RunServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val postgresDbConfig = DatabaseConfig(
      "org.postgresql.Driver",
      16,
      "localhost",
      5432,
      "image_analyzer",
      "",
      "")

    val httpClientBuilder = BlazeClientBuilder[IO]

    val postgresDbConnection = DatabaseConnection.transactor(postgresDbConfig)

    postgresDbConnection.use(resources =>
      httpClientBuilder.resource.use(client => {

        for {
          postgresDb <- IO.fromEither(PostgresClient.createClient(resources))
          routes = new Routes(postgresDb, client).routes
          httpApp = Router("/" -> routes).orNotFound
          exitCode <- startServer("localhost", 8080, httpApp)
        } yield exitCode
      }))

  }

  private def startServer(host: String, port: Int, app: http4s.HttpApp[IO]): IO[ExitCode] = {
    (
      for {
        _ <- BlazeServerBuilder[IO]
          .bindHttp(port, host)
          .withHttpApp(app)
          .resource
      } yield ()
      ).use(_ => IO.never).as(ExitCode.Success)
  }

}

