package org.service

import com.typesafe.config._
import org.http4s.{ParseResult, Uri}

import scala.language.implicitConversions


object ConfigurationService {

  implicit def toUri(parseResult: ParseResult[Uri]) : Uri
  = parseResult match {
    case Right(value) => value
  }

  // general launch profile
  private val env: String = sys.env.getOrElse("APP_ENV", "local") // default to local
  println(s"Application profile: ${env}")

  private val rootConfig: Config = ConfigFactory.load(s"application${if (env == "local") "" else "-container"}.conf")
  val kafkaBootstrapUrl: String = rootConfig.getString("kafka.server.bootstrapUrl")
  val kafkaTopic: String = rootConfig.getString("kafka.server.topic")
  val kafkaGroupId: String = rootConfig.getString("kafka.server.groupId")

  //oauth via GitHub
  private val oauthConfig: Config = ConfigFactory.load("authorization.conf")

  val oauthClientId: String = sys.env.getOrElse("CLIENT_ID", "missed")
  val oauthClientSecret: String = sys.env.getOrElse("CLIENT_SECRET", "missed")
  val authorizeUri: Uri = Uri.fromString(oauthConfig.getString("oauth.authorizeUri"))
  val tokenUri: Uri =  Uri.fromString(oauthConfig.getString("oauth.tokenUri"))
  val userUri: Uri =  Uri.fromString(oauthConfig.getString("oauth.userUri"))
  val redirectUri: Uri =  Uri.fromString(oauthConfig.getString("oauth.redirectUri"))
}
