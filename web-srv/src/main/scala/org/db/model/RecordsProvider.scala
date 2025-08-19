package org.db.model

import cats.effect.MonadCancelThrow
import doobie.Transactor
import doobie.implicits._
import org.commons4n.CustomRecord

trait RecordsProvider[F[_]] {
  def findById(id: Int): F[Option[CustomRecord]]
  def findAll: F[List[CustomRecord]]
  def insertRecord(value: String): F[Int]
  def initializeSchema : F[Unit]
}

object RecordsProvider {
  def make[F[_]: MonadCancelThrow](xa: Transactor[F]): RecordsProvider[F] = {
    new RecordsProvider[F] {

      def findById(id: Int): F[Option[CustomRecord]] =
        sql"SELECT id, value FROM scl.records WHERE id = $id".query[CustomRecord].option.transact(xa)

      def findAll: F[List[CustomRecord]] =
        sql"SELECT id, value FROM scl.records".query[CustomRecord].to[List].transact(xa)

      def insertRecord(value: String): F[Int] =
        sql"INSERT INTO scl.records (value) VALUES ($value)".update.withUniqueGeneratedKeys[Int]("id").transact(xa)

      def initializeSchema: F[Unit] = {
       val sqlCommands =  for {
          _ <- sql"CREATE SCHEMA IF NOT EXISTS scl".update.run
          _ <- sql"CREATE TABLE IF NOT EXISTS scl.records (id SERIAL PRIMARY KEY, value TEXT)".update.run
        } yield ()
        sqlCommands.transact(xa)
      }
    }
  }

}
