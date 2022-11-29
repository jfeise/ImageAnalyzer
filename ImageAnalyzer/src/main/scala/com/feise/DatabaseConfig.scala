package com.feise

final case class DatabaseConfig(
  driver: String,
  poolMaxConnections: Int,
  host: String,
  port: Int,
  dbName: String,
  username: String,
  password: String
)