package org.rest
import cats.effect.IO
import io.circe.syntax.EncoderOps
import org.db.model.{CustomRecord, Records}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.service.kafka.KafkaService

object CustomRoutes {

  def routes(db: Records[IO],kafkaService : KafkaService[IO]): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "records" =>
        db.findAll.flatMap(records => Ok(records.asJson))

      case req @ POST -> Root / "records"  =>
        for {
          record <- req.as[CustomRecord]
          _ <- kafkaService.send("1",record)
          resp <- db.insertRecord(record.value).flatMap(id => Created(s"Inserted with id: $id"))
        } yield resp
    }
  }
}