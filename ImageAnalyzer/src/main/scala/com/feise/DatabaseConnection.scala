package com.feise

import cats.effect._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object DatabaseConnection {

  def transactor(config: DatabaseConfig): Resource[IO, HikariTransactor[IO]] =
    for {
      contexts <- ExecutionContexts.fixedThreadPool[IO](config.poolMaxConnections)
      transactor <- HikariTransactor.newHikariTransactor[IO] (
        config.driver,
        f"jdbc:postgresql://${config.host}:${config.port}/${config.dbName}",
        config.username,
        config.password,
        contexts
      )
    } yield transactor

}