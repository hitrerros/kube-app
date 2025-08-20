package org

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import org.db.DBProvider
import org.db.model.RecordsProvider
import org.http4s.ember.server._
import org.rest.CustomRoutes
import org.service.kafka.KafkaService

object AppServer extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    val resources = for {
      _ <- Resource.eval(KafkaService.createTopics[IO])
      xa <- DBProvider.provider
      kafka <- KafkaService.provider[IO]
    } yield (xa,kafka)

    resources.use { case(xa,kafka) =>
      val dbService = RecordsProvider.make[IO](xa)

      for {
        _ <- dbService.initializeSchema
        code <- EmberServerBuilder
          .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(CustomRoutes.routes(dbService,kafka).orNotFound)
      .build
          .useForever
      } yield code
    }
  }
}
