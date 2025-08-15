package org.service
import com.typesafe.config.{Config, ConfigFactory}

object ConfigurationService {
  private val dbPrefix = "db.postgres"
  private val kafkaPrefix = "kafka.server"

  val env: String = sys.env.getOrElse("APP_ENV", "local") // default to local
  println(s"Application profile: ${env}")
  private val rootConfig: Config = ConfigFactory.load(s"application${if (env == "local") "" else "-container"}.conf")

  val jdbcUrl: String = rootConfig.getString(s"${dbPrefix}.url")
  val driver: String = rootConfig.getString(s"${dbPrefix}.driver")
  val user: String = rootConfig.getString(s"${dbPrefix}.user")
  val password: String = rootConfig.getString(s"${dbPrefix}.password")

  val kafkaBootstrapUrl: String = rootConfig.getString(s"${kafkaPrefix}.bootstrapUrl")
  val kafkaTopic: String = rootConfig.getString(s"${kafkaPrefix}.topic")

}
