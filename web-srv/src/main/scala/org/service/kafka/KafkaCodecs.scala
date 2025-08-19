package org.service.kafka

import cats.effect.Async
import fs2.kafka.Serializer
import io.circe.Encoder
import io.circe.syntax.EncoderOps

object KafkaCodecs {
  val ENCODING_UTF8 = "UTF-8"
  implicit def circeJsonSerializer[F[_]: Async, A: Encoder]: Serializer[F, A] =
    Serializer.lift[F, A] { value =>
      Async[F].delay(value.asJson.noSpaces.getBytes(ENCODING_UTF8))
    }
}
