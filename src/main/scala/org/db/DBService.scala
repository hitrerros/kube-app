package org.db

import cats.effect._
import doobie._
import doobie.hikari._
import doobie.implicits._
import org.resource.ResourceService
import org.service.ConfigurationService

import scala.concurrent.ExecutionContext

sealed trait DBTransactionService extends ResourceService{
  // transactor resource
  val transactor: Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = ConfigurationService.driver,
      url = ConfigurationService.jdbcUrl,
      user = ConfigurationService.user,
      pass = ConfigurationService.password,
      connectEC = ExecutionContext.global
    )

  def statementRunner(dslIO: ConnectionIO[Int]): IO[Unit] = transactor.use {
    expr =>
      dslIO.transact(expr).flatMap(_ => IO.unit)
  }
}

object DBService extends  DBTransactionService {

  // initialize schema
  val schemaBootstrap: ConnectionIO[Int] =
    sql"""
         CREATE SCHEMA IF NOT EXISTS scl;
         CREATE TABLE IF NOT EXISTS scl.records (id SERIAL PRIMARY KEY, value TEXT)""".update.run

  // get records
  def getRecords: ConnectionIO[List[Record]] =
    sql"SELECT id, value FROM scl.records".query[Record].to[List]

  // post records
  def postRecord(record: String): IO[Unit] =
       statementRunner(
         sql"INSERT INTO scl.records (value) VALUES $record".update.run
       )

  val schemaBootstrapRunner: IO[Unit] = statementRunner(schemaBootstrap)

}
