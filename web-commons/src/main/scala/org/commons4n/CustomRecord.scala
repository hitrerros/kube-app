package org.commons4n

import io.circe._
import io.circe.generic.semiauto._

case class CustomRecord(id : Int, value : String)

object CustomRecord {
  implicit val customRecordEncoder: Encoder[CustomRecord] = deriveEncoder
  implicit val customRecordDecoder: Decoder[CustomRecord] = deriveDecoder
}
