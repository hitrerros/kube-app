package org

import cats.effect.IO
import org.commons4n.CustomRecord
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

package object api {
  implicit val recordsDecoder: EntityDecoder[IO, CustomRecord] = jsonOf[IO, CustomRecord]
  implicit val recordsEncoder: EntityEncoder[IO, List[CustomRecord]] = jsonEncoderOf[IO,List[CustomRecord]]
}
