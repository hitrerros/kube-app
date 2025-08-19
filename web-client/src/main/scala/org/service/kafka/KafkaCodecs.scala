package org.service.kafka

import cats.effect.Async
import fs2.kafka.Deserializer
import io.circe.Decoder
import io.circe.parser.decode

object KafkaCodecs {
  val ENCODING_UTF8 = "UTF-8"
  implicit def circeJsonDeserializer[F[_]: Async, A: Decoder]: Deserializer[F, A] =
    Deserializer.lift[F, A] { bytes =>
      Async[F].fromEither(
        decode[A](new String(bytes, ENCODING_UTF8)).left.map(err => new RuntimeException(err))
      )
    }
}
