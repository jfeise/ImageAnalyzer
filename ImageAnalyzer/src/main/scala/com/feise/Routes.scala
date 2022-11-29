package com.feise

import java.util.UUID

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import io.circe.literal._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.headers.`Content-Type`
import org.http4s.implicits._
import cats.effect.unsafe.implicits.global

//import com.google.cloud.vision.v1.BatchAnnotateImagesResponse


class Routes(postgresClient: PostgresClient, httpClient: Client[IO]) extends Http4sDsl[IO] {

  def routes: HttpRoutes[IO] = {
    object OptionalQueryParamMatcher extends OptionalQueryParamDecoderMatcher[String]("objects")

    HttpRoutes.of[IO] {
      case GET -> Root / "images" :? OptionalQueryParamMatcher(objects) =>
        val o = objects match {
          case Some(o) => Some(o.split(",").toList)
          case _ => None
        }
        val res = postgresClient.getMetadata(o, None)
        for {
          result <- res.value
          response <- result match {
            case Right(data) => Ok(data)
            case Left(e) =>
              BadRequest(e.toErrorResponse)
          }
        } yield response

      case GET -> Root / "images" / imageId =>
        val res = for {
          _ <- isValidUUID(imageId)
          id = UUID.fromString(imageId)
          res = postgresClient.getMetadata(None, Some(id))
          r = for {
            result <- res.value
            response = result match {
              case Right(data) => Ok(data)
              case Left(e) => BadRequest(e.toErrorResponse)
            }
          } yield response
      } yield r.flatten
      res match {
        case Right(data) => data
        case Left(e) => BadRequest(e.toErrorResponse)
      }

      case req@POST -> Root / "images" =>
        val res = for {
          result <- req.as[Image].attempt.map {
              case Right(data) if data.detect.nonEmpty && data.detect.get =>
                data.url match {
                  case Some(url) =>
                    // Limit to face detection for now
                    val jsonBody =
                      json"""
                             {
                             "requests":
                                [
                                  {
                                    "image": {
                                      "source": {
                                        "imageUri": ${url}
                                      }
                                    },
                                    "features": {
                                      "type": "FACE_DETECTION",
                                       "maxResults": 1
                                    }
                                  }] }"""
                    val req = Request[IO](
                      method = Method.POST,
                      uri = uri"https://vision.googleapis.com/v1/images:annotate")
                      .withEntity(jsonBody.asString)
                      .withContentType(`Content-Type`(MediaType.application.json, Some(Charset.`UTF-8`)))
                    try {
                      val res = httpClient.fetchAs[BatchAnnotateImagesResponse](req).unsafeRunSync()
                      val objects = res.responses.size match {
                        case 0 => List.empty[String]
                        case _ => List("face")
                      }
                      val image = Image(data.imageId, data.url, data.label, Some(objects), Some(true))
                      postgresClient.writeMetadata(image)
                      IO(Right(image))
                    }
                    catch {
                      case (_: Throwable) => IO(Left(new RequestException(Status.BadRequest)("Invalid data received", None)))
                    }
                  case _ => IO(Left(new RequestException(Status.BadRequest)("No URL found", None)))
                }
              case Right(data) =>
                val image = Image(data.imageId, data.url, data.label, None, Some(false))
                postgresClient.writeMetadata(image)
                IO(Right(image))

              case Left(_) => IO(Left(new RequestException(Status.BadRequest)("Invalid Json", None)))
           }

        } yield result
        val r = res.flatten
        val result = r.map { res =>
          res match {
            case Right(data) => Ok(data)
            case Left(e) => BadRequest(e.toErrorResponse)
          }
        }
        result.flatten

    }
  }
  def isValidUUID(value: String): Either[RequestException, None.type] = {
    val uuidRegex = raw"\b[0-9a-f]{8}\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\b[0-9a-f]{12}\b".r
    value match {
      case uuidRegex(_*) => Right(None)
      case _ => Left(new RequestException(Status.BadRequest)("Invalid UUID", None))
    }
  }

}
