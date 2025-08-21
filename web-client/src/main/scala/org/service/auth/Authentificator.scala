package org.service.auth

import cats.data.{Kleisli, OptionT}
import cats.effect._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import org.service.ConfigurationService.userUri
import org.typelevel.ci.CIStringSyntax


object Authentificator {

  def authUser(client: Client[IO]): AuthMiddleware[IO, AuthUser] = {
    val validate: Kleisli[IO, Request[IO], Either[String, AuthUser]] = Kleisli { req =>
      req.headers.get[headers.Authorization] match {
        case Some(headers.Authorization(Credentials.Token(AuthScheme.Bearer, token))) =>
          val userReq = Request[IO](Method.GET, userUri)
            .withHeaders(
              Authorization(Credentials.Token(AuthScheme.Bearer, token)),
              Header.Raw(ci"User-Agent", "http4s-oauth-app")
            )
          client.expectOption[AuthUser](userReq)(jsonOf[IO, AuthUser]).map {
            case Some(user) => Right(user)
            case None       => Left("Invalid token")
          }
        case _ => IO.pure(Left("Missing token"))
      }
    }

    val onFailure: AuthedRoutes[String, IO] = Kleisli { req =>
      OptionT.liftF(Forbidden(s"Auth failed: ${req.context}"))
    }

    AuthMiddleware(validate, onFailure)
  }
}
