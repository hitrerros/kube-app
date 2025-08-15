package org

import cats.effect.IO
import org.db.model.CustomRecord
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

package object rest {
  implicit val recordsDecoder: EntityDecoder[IO, CustomRecord] = jsonOf[IO, CustomRecord]
  implicit val recordsEncoder: EntityEncoder[IO, List[CustomRecord]] = jsonEncoderOf[IO,List[CustomRecord]]
}
