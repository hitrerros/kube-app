package org.service.kafka

import cats.effect.Async
import fs2.kafka.{Deserializer, Serializer}
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

object KafkaCodecs {
  implicit def circeJsonSerializer[F[_]: Async, A: Encoder]: Serializer[F, A] =
    Serializer.lift[F, A] { value =>
      Async[F].delay(value.asJson.noSpaces.getBytes("UTF-8"))
    }

  implicit def circeJsonDeserializer[F[_]: Async, A: Decoder]: Deserializer[F, A] =
    Deserializer.lift[F, A] { bytes =>
      Async[F].fromEither(
        decode[A](new String(bytes, "UTF-8")).left.map(err => new RuntimeException(err))
      )
    }
}
