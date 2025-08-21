package org.api
import cats.effect._
import io.circe.generic.auto._
import org.commons4n.CustomRecord
import org.commons4n.service.MessageCache
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe._
import org.http4s.client._
import org.http4s.dsl.io._
import org.http4s.headers.Location
import org.http4s.{HttpRoutes, _}
import org.service.ConfigurationService
import org.service.ConfigurationService.{authorizeUri, redirectUri, tokenUri}
import org.service.auth.{AccessToken, AuthUser}
import org.typelevel.ci.CIStringSyntax

object CustomRoutes {
  def publicRoutes(client: Client[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    // login
    case GET -> Root / "login" =>
      val uri = authorizeUri
        .withQueryParam("client_id", ConfigurationService.ouathClientId)
        .withQueryParam("redirect_uri", redirectUri)
        .withQueryParam("scope", "read:user user:email")

      SeeOther(Location(uri))

    // callback
    case GET -> Root / "callback" :? CodeQueryParamMatcher(code) =>
      val tokenReq = Request[IO](Method.POST, tokenUri)
        .withEntity(UrlForm(
          "client_id" -> ConfigurationService.ouathClientId,
          "client_secret" -> ConfigurationService.ouathClientSecret,
          "code" -> code,
          "redirect_uri" -> redirectUri.toString()
        ))
        .withHeaders(Header.Raw(ci"Accept", "application/json"))

      for {
        token <- client.expect[AccessToken](tokenReq)(jsonOf[IO, AccessToken])
        res <- Ok(s"Here is your token: ${token.access_token}")
      } yield res
  }

  def authRoutes(cache: MessageCache[IO, String, CustomRecord]): AuthedRoutes[AuthUser, IO] =
    AuthedRoutes.of {
    case GET -> Root / "records" as user =>
      for {values <- cache.getAll
           r <- Ok(values)
           } yield r
  }
}

object CodeQueryParamMatcher extends QueryParamDecoderMatcher[String]("code")
