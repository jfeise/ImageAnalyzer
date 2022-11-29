package com.feise

import java.util.UUID

import cats.effect.IO
import io.circe.literal._
import io.circe.{Decoder, Encoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import scala.util.Random

case class Image(imageId: UUID, url: Option[String], label: Option[String], objects: Option[List[String]], detect: Option[Boolean])

object Image {
  implicit val imageEncoder: Encoder[Image] =
    Encoder.instance { (image: Image) =>
      json"""{"imageId": ${image.imageId}, "url": ${image.url}, "label": ${image.label}, "objects": ${image.objects}}"""
    }

  implicit val imageDecoder: Decoder[Image] = (c: HCursor) => for {
    imageId <- c.downField("imageId").as[Option[UUID]]
    url <- c.downField("url").as[Option[String]]
    label <- c.downField("label").as[Option[String]]
    objects <- c.downField("objects").as[Option[List[String]]]
    detect <- c.downField("detect").as[Option[Boolean]]
  } yield {
    Image(imageId.getOrElse(UUID.randomUUID()), url, Some(label.getOrElse(Random.alphanumeric.take(10).mkString)), objects, detect)
  }

  implicit val imageEntityDecoder: EntityDecoder[IO, Image] = jsonOf[IO, Image]
}
