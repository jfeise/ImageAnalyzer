package com.feise

import java.util.UUID
import doobie.hikari.HikariTransactor
import doobie._
import doobie.implicits._
import cats.effect._
import cats.data._
import doobie.free.connection.ConnectionIO
import doobie.postgres.implicits._
import org.http4s.Status


class PostgresClient(transactor: Transactor[IO]) {

  def getMetadata(objectFilter: Option[List[String]], imageId: Option[UUID]): EitherT[IO, RequestException, List[Image]] = {
    for {
      result <- EitherT {
        getMetadataImpl(objectFilter, imageId)
      }.transact(transactor)
    } yield result
  }

  private def getMetadataImpl(objectFilter: Option[List[String]], imageId: Option[UUID]):
    ConnectionIO[Either[RequestException, List[Image]]] = {
    val imageIdQuery = imageId match {
      case Some(id) => Some("WHERE id = $id")
      case _ => None
    }
    val filterQuery = objectFilter match {
      case Some(f) if imageIdQuery.isEmpty => {
        val v = f.mkString(",")
        sql" WHERE '$v' = ANY(objects)"
      }
      case _ if imageIdQuery.nonEmpty => sql" WHERE id=${imageIdQuery.get}"
      case _ => sql""
    }
    sql"""
        SELECT id, label, objects
        FROM images
        $filterQuery
      """
      .query[Image]
      .to[List]
      .attemptSql.map {
      case Right(value) => Right(value)
      case Left(e) =>
        Left(new RequestException(Status.InternalServerError)("Select on images failed due to unknown error", Some(e)))
    }
  }

  def writeMetadata(image: Image): EitherT[IO, RequestException, None.type] = {
    for {
      result <- EitherT {
        writeMetadataImpl(image)
      }.transact(transactor)
    } yield result
  }

  private def writeMetadataImpl(image: Image): ConnectionIO[Either[RequestException, None.type]] = {
    sql"""
      INSERT INTO images (id, url, label, objects)
      VALUES (
        ${image.imageId},
        ${image.url},
        ${image.label},
        ${image.objects}
        )
      """
      .update
      .withUniqueGeneratedKeys[Image](
        "id",
        "url",
        "label",
        "objects"        )
      .attemptSql map {
      case Right(_) => Right(None)
      case Left(e) =>
        Left(new RequestException(Status.InternalServerError)("Inserting images failed due to unknown error", Some(e)))
    }
  }
}

object PostgresClient {
  def createClient(transactor: HikariTransactor[IO]): Either[RequestException, PostgresClient] = {
    Right(new PostgresClient(transactor))
  }
}

