package org.commons4n.service

import cats.effect._
import cats.effect.kernel.Ref
import cats.syntax.all._
import org.commons4n.CustomRecord

import scala.language.implicitConversions

trait MessageCache[F[_], K, V] {
  def get(key: K): F[Option[V]]
  def getAll: F[List[V]]
  def put(key: K, value: V): F[Option[V]]
  def clear: F[Unit]
}

object MessageCache {
  def of[F[_]: Async, K, V]: F[MessageCache[F, K, V]] = {
    Ref.of[F, Map[K, V]](Map.empty).map { ref =>
      new MessageCache[F, K, V] {

        override def get(key: K): F[Option[V]] =
          ref.get.map(_.get(key))

        override def getAll: F[List[V]] =
          ref.get.map(_.values.toList)

        override def put(key: K, value: V): F[Option[V]] =
          ref.modify { m =>
            val prev = m.get(key)
            (m.updated(key, value), prev)
          }

        override def clear: F[Unit] =
          ref.set(Map.empty)
      }
    }
  }

    def provider[F[_] : Async]: Resource[F, MessageCache[F,String,CustomRecord]] =
      Resource.eval(MessageCache.of[F, String, CustomRecord])
}