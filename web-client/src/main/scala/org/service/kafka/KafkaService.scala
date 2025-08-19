package org.service.kafka

import cats.effect.{Async, IO}
import fs2.kafka.{AutoOffsetReset, ConsumerRecord, ConsumerSettings, KafkaConsumer}
import org.commons4n.CustomRecord
import org.service.ConfigurationService.{kafkaBootstrapUrl, kafkaGroupId, kafkaTopic}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger


trait KafkaService[F[_]] {
  def receive(key: String): F[Unit]
}

object KafkaService {
  def receive[F[_]: Async](record: ConsumerRecord[String, CustomRecord]) : F[Unit] = {
    implicit val logger: Logger[F] = Slf4jLogger.getLogger[F]
    Logger[F].info(s"=> App received record: ${record.value}")
  }

  import KafkaCodecs._
  private def consumerSettings[F[_]: Async]
  : ConsumerSettings[F, String, CustomRecord] =
    ConsumerSettings[F, String, CustomRecord]
      .withBootstrapServers(kafkaBootstrapUrl)
      .withGroupId(kafkaGroupId)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)

  def kafkaConsumerStream[F[_]: Async]: F[Unit] = {
    KafkaConsumer.stream(consumerSettings)
      .evalTap(_.subscribeTo(kafkaTopic))
      .partitionedRecords
      .map{  partitionStream =>
        partitionStream.evalMap { committable =>
          receive[IO](committable.record)
        }
      }
      .compile
      .drain
      }
}
