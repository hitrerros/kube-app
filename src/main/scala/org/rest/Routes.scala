package org.rest
import cats.effect.IO
import io.circe.generic.auto._
import org.db.DBService
import org.db.model.Record
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import org.http4s.server.Router

object Routes {

  // EntityDecoder to parse JSON into Person
  implicit val recordsDecoder: EntityDecoder[IO, Record] = jsonOf[IO, Record]

  private val routes = HttpRoutes.of[IO] {
    case GET -> Root / "records" =>
      Ok("Hello, world!")

    case req @ POST -> Root / "records" =>
       for {
        record <- req.as[Record]
        a <- IO(DBService.postRecord(record.value))
        response <- Ok(a.toString())
      } yield response
  }

  val httpApp: HttpApp[IO] = Router(
    "/" -> routes
  ).orNotFound

}
