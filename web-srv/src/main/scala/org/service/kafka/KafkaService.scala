package org.service.kafka

import cats.effect._
import cats.implicits._
import fs2.kafka._
import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.commons4n.CustomRecord
import org.service.ConfigurationService.{kafkaBootstrapUrl, kafkaTopic}

import java.util.Properties
import scala.jdk.CollectionConverters._
import scala.util.Using

trait KafkaService[F[_]] {
  def send(key: String, value: CustomRecord): F[Unit]
}

object KafkaService {
  import KafkaCodecs._
  private def producerSettings[F[_]: Async]
      : ProducerSettings[F, String, CustomRecord] =
    ProducerSettings[F, String, CustomRecord].withBootstrapServers(
      kafkaBootstrapUrl
    )

  private def make[F[_]: Async](
      producer: KafkaProducer[F, String, CustomRecord]
  ): KafkaService[F] =
    (key: String, value: CustomRecord) => {
      producer
        .produceOne(ProducerRecord(kafkaTopic, key, value))
        .flatten
        .void
    }

  def createTopics[F[_] : Sync]: F[Unit] =
    Sync[F].blocking {
      val props = new Properties()
      props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrl)
      val adminClient = AdminClient.create(props)

      Using.resource(adminClient) { adminClient =>
        if (!adminClient
          .listTopics().names()
          .get().contains(kafkaTopic)) {
          val singleTopic: List[(String, Int, Short)] =
            List(Tuple3(kafkaTopic, 1, 1))
          adminClient
            .createTopics(
              singleTopic.map(k => new NewTopic(k._1, k._2, k._3)).asJava
            )
            .all()
            .get()
        }
      }
    }

  def provider[F[_]: Async]: Resource[F, KafkaService[F]] =
    KafkaProducer
      .resource[F, String, CustomRecord](producerSettings)
      .map(make[F])
}
