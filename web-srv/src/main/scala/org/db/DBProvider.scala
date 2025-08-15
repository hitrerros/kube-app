package org.db

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.service.ConfigurationService

object DBProvider {
   val provider: Resource[IO, HikariTransactor[IO]] = for {
    ce <- ExecutionContexts.cachedThreadPool[IO]
    xa <- HikariTransactor.newHikariTransactor[IO](
      driverClassName = ConfigurationService.driver,
      url = ConfigurationService.jdbcUrl,
      user = ConfigurationService.user,
      pass = ConfigurationService.password,
      connectEC = ce
    )
  } yield xa
}
