package org

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import org.db.DBProvider
import org.db.model.Records
import org.http4s.ember.server._
import org.rest.CustomRoutes

object AppServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val resources = for {
      xa <- DBProvider.initializer
    } yield xa

    resources.use { xa =>
      val dbService = Records.make[IO](xa)

      for {
        _ <- dbService.initializeSchema
        code <- EmberServerBuilder
          .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(CustomRoutes.routes(dbService).orNotFound)
      .build
          .useForever
      } yield code
    }
  }
}
