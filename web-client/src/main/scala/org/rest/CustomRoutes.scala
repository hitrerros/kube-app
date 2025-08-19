package org.rest

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.Http4sDsl

object CustomRoutes {
  def routes: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "records" =>
        Ok("gotcha")
    }
  }
}