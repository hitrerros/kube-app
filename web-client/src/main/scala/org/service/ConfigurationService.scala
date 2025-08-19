package org.service

import com.typesafe.config._

object ConfigurationService {
  private val kafkaPrefix = "kafka.server"

  val env: String = sys.env.getOrElse("APP_ENV", "local") // default to local
  println(s"Application profile: ${env}")
  private val rootConfig: Config = ConfigFactory.load(s"application${if (env == "local") "" else "-container"}.conf")

  val kafkaBootstrapUrl: String = rootConfig.getString(s"${kafkaPrefix}.bootstrapUrl")
  val kafkaTopic: String = rootConfig.getString(s"${kafkaPrefix}.topic")
  val kafkaGroupId: String = rootConfig.getString(s"${kafkaPrefix}.groupId")
}
