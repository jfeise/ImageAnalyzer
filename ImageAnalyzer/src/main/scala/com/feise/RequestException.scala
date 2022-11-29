package com.feise

import org.http4s.Status
import cats.effect.IO
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.literal._
import org.http4s.EntityDecoder
import org.http4s.circe._

class RequestException(val status: Status)(message: String, cause: Option[Throwable])
  extends Exception(message, cause.orNull) {

  def getOptException: Option[Throwable] = cause

  def toErrorResponse: ErrorResponse = {
    ErrorResponse(message)
  }
}

final case class ErrorResponse(message: String)

object ErrorResponse {
  implicit val errorResponseEncoder: Encoder[ErrorResponse] =
    Encoder.instance { (error: ErrorResponse) =>
      json"""{"message": ${error.message}}"""
    }

  implicit val labelDecoder: Decoder[ErrorResponse] = (c: HCursor) => for {
    message <- c.downField("message").as[String]
  } yield {
    ErrorResponse(message)
  }

  implicit val labelEntityDecoder: EntityDecoder[IO, ErrorResponse] = jsonOf[IO, ErrorResponse]
}
