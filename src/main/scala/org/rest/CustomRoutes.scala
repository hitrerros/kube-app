package org.rest
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.db.model.{CustomRecord, Records}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object CustomRoutes {

  // EntityDecoder to parse JSON into Person
  implicit val recordsDecoder: EntityDecoder[IO, CustomRecord] = jsonOf[IO, CustomRecord]
  implicit val recordsEncoder: EntityEncoder[IO, List[CustomRecord]] = jsonEncoderOf[IO,List[CustomRecord]]

  def routes(db: Records[IO]): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "records" =>
        db.findAll.flatMap(records => Ok(records.asJson))

      case req @ POST -> Root / "records"  =>
        for {
          record <- req.as[CustomRecord]
          resp <- db.insertRecord(record.value).flatMap(id => Created(s"Inserted with id: $id"))
        } yield resp
    }
  }

}
