package org.db.model

import cats.effect.MonadCancelThrow
import doobie.implicits._
import doobie.{Read, Transactor, Write}

case class CustomRecord(id : Int, value : String)

trait Records[F[_]] {
  def findById(id: Int): F[Option[CustomRecord]]
  def findAll: F[List[CustomRecord]]
  def insertRecord(value: String): F[Int]
  def initializeSchema : F[Unit]
}

object Records {
  def make[F[_]: MonadCancelThrow](xa: Transactor[F]): Records[F] = {
    new Records[F] {

      import RecordSQL._
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

private object RecordSQL {
  implicit val recordRead: Read[CustomRecord] =
    Read[(Int, String)].map { case (id, value) => CustomRecord(id, value) }

  implicit val recordWrite: Write[CustomRecord] =
    Write[(Int, String)].contramap { record => (record.id, record.value)  }

}
