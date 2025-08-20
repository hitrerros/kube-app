package org.rest

import cats.effect.IO
import org.commons4n.CustomRecord
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.service.MessageCache

object CustomRoutes {
  def routes(cache : MessageCache[IO,String,CustomRecord]): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "asm" =>
        for { values <- cache.getAll
               r <- Ok (values.toString())
             }  yield r
    }
  }
}