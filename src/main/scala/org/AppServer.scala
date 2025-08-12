package org

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import org.rest.Routes

object AppServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
    //  _    <- DBService.schemaBootstrap
      code <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(Routes.httpApp)
        .build
        .use(_ => IO.never)
    } yield code
  }
}
