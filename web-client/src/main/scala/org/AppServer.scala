package org

import cats.effect._
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s.IpLiteralSyntax
import org.api.CustomRoutes.{authRoutes, publicRoutes}
import org.commons4n.service.MessageCache
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.service.auth.Authentificator
import org.service.kafka.KafkaService

object AppServer extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val resources = for {
      cache <- MessageCache.provider[IO]
      kafka <- KafkaService.provider[IO](cache)
      client <- EmberClientBuilder.default[IO].build
     } yield (kafka, cache,client)

    resources.use { case (_, cache,client) =>
      val middleware = Authentificator.authUser(client)
      val httpApp = (publicRoutes(client) <+> middleware(authRoutes(cache))).orNotFound

      for {
        code <- EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8081")
          .withHttpApp(httpApp)
          .build
          .useForever

      } yield code
    }
  }
}
