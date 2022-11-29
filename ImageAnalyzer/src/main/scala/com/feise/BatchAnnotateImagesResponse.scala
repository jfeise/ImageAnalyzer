package com.feise

import cats.effect.IO
import io.circe.literal._
import io.circe.{Decoder, Encoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf


case class Vertex(x: Int, y: Int)
case class BoundingPoly(vertices: List[Vertex], normalizedVertices: List[Vertex])
case class FaceAnnotation(
  boundingPoly: BoundingPoly
)
case class AnnotateImagesResponse(faceAnnotations: List[FaceAnnotation])
case class BatchAnnotateImagesResponse(responses: List[AnnotateImagesResponse])

object BatchAnnotateImagesResponse {
  implicit val batchAnnotateImagesResponseEncoder: Encoder[BatchAnnotateImagesResponse] =
    Encoder.instance { (response: BatchAnnotateImagesResponse) =>
      json"""{"responses": ${response.responses}}"""
    }

  implicit val batchAnnotateImagesResponseDecoder: Decoder[BatchAnnotateImagesResponse] = (c: HCursor) => for {
    responses <- c.downField("responses").as[List[AnnotateImagesResponse]]
  } yield {
    BatchAnnotateImagesResponse(responses)
  }

  implicit val batchAnnotateImagesResponseEntityDecoder: EntityDecoder[IO, BatchAnnotateImagesResponse] = jsonOf[IO, BatchAnnotateImagesResponse]
}

object AnnotateImagesResponse {
  implicit val annotateImagesResponseEncoder: Encoder[AnnotateImagesResponse] =
    Encoder.instance { (response: AnnotateImagesResponse) =>
      json"""{"faceAnnotation": ${response.faceAnnotations}}"""
    }

  implicit val annotateImagesResponseDecoder: Decoder[AnnotateImagesResponse] = (c: HCursor) => for {
    faceAnnotations <- c.downField("faceAnnotations").as[List[FaceAnnotation]]
  } yield {
    AnnotateImagesResponse(faceAnnotations)
  }

  implicit val annotateImagesResponseEntityDecoder: EntityDecoder[IO, AnnotateImagesResponse] = jsonOf[IO, AnnotateImagesResponse]
}

object FaceAnnotation {
  implicit val faceAnnotationResponseEncoder: Encoder[FaceAnnotation] =
    Encoder.instance { (response: FaceAnnotation) =>
      json"""{"boundingPoly": ${response.boundingPoly}}"""
    }

  implicit val faceAnnotationResponseDecoder: Decoder[FaceAnnotation] = (c: HCursor) => for {
    boundingPoly <- c.downField("boundingPoly").as[BoundingPoly]
  } yield {
    FaceAnnotation(boundingPoly)
  }

  implicit val faceAnnotationResponseEntityDecoder: EntityDecoder[IO, FaceAnnotation] = jsonOf[IO, FaceAnnotation]
}


object BoundingPoly {
  implicit val boundingPolyResponseEncoder: Encoder[BoundingPoly] =
    Encoder.instance { (response: BoundingPoly) =>
      json"""{"vertices": ${response.vertices}, "normalizedVertices": ${response.normalizedVertices}}"""
    }

  implicit val boundingPolyResponseDecoder: Decoder[BoundingPoly] = (c: HCursor) => for {
    vertices <- c.downField("x").as[List[Vertex]]
    normalizedVertices <- c.downField("y").as[List[Vertex]]
  } yield {
    BoundingPoly(vertices, normalizedVertices)
  }

  implicit val boundingPolyResponseEntityDecoder: EntityDecoder[IO, BoundingPoly] = jsonOf[IO, BoundingPoly]
}

object Vertex {
  implicit val vertexResponseEncoder: Encoder[Vertex] =
    Encoder.instance { (response: Vertex) =>
      json"""{"x": ${response.x}, "y": ${response.y}}"""
    }

  implicit val vertexResponseDecoder: Decoder[Vertex] = (c: HCursor) => for {
    x <- c.downField("x").as[Int]
    y <- c.downField("y").as[Int]
  } yield {
    Vertex(x, y)
  }

  implicit val vertexResponseEntityDecoder: EntityDecoder[IO, Vertex] = jsonOf[IO, Vertex]
}

