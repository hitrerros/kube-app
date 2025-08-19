package org

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import org.rest.CustomRoutes
import org.service.kafka.KafkaService

object AppServer extends IOApp{

  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- KafkaService.kafkaConsumerStream[IO]
      code <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8081")
        .withHttpApp(
          CustomRoutes.routes.orNotFound
        )
        .build
        .useForever

    } yield code
  }
}
