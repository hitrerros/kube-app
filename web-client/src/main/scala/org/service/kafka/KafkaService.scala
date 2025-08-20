package org.service.kafka

import cats.effect.implicits.genSpawnOps
import cats.effect.{Async, Resource}
import cats.implicits.toFunctorOps
import cats.syntax.all._
import fs2.kafka._
import org.commons4n.CustomRecord
import org.service.ConfigurationService.{kafkaBootstrapUrl, kafkaGroupId, kafkaTopic}
import org.service.MessageCache
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.DurationInt

trait KafkaService[F[_]] {
  def receive: F[Unit]
}

object KafkaService {
  private def processRecord[F[_]: Async]
  (messageCache: MessageCache[F,String,CustomRecord])
  (record: ConsumerRecord[String, CustomRecord])
  (logger: Logger[F]): F[Unit] = {

    for {  _ <- logger.info(s"=> App received record: ${record.value}")
          _ <-  messageCache.put(record.key, record.value)
          } yield ()
  }


  import KafkaCodecs._
  private def consumerSettings[F[_] : Async]: ConsumerSettings[F, String, CustomRecord] =
    ConsumerSettings[F, String, CustomRecord]
      .withBootstrapServers(kafkaBootstrapUrl)
      .withGroupId(kafkaGroupId)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)

  private def make[F[_]: Async](messageCache: MessageCache[F,String,CustomRecord])(logger: Logger[F]): KafkaService[F] =
    new KafkaService[F] {
      override def receive: F[Unit] =
        KafkaConsumer
          .stream(consumerSettings[F])
          .evalTap(_.subscribeTo(kafkaTopic))
          .flatMap(_.stream)
          .evalMap { committable =>
            processRecord[F](messageCache)(committable.record)(logger).as(committable.offset)
          }
          .through(commitBatchWithin(100, 5.seconds))
          .compile
          .drain
    }

  def provider[F[_] : Async](messageCache: MessageCache[F,String,CustomRecord]): Resource[F, KafkaService[F]] =
    for {
      logger <- Resource.eval(Slf4jLogger.create[F])
      service = make[F](messageCache)(logger)
      _ <- service.receive.background
    } yield service
}