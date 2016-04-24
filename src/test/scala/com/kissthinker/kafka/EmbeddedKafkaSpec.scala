package com.kissthinker.kafka

import java.util.Properties
import java.util.concurrent.Future
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.specs2.mutable.Specification

class EmbeddedKafkaSpec extends Specification {
  "blah" should {
    "blah" in new EmbeddedKafka {

      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9092")
      props.put("metadata.broker.list", "127.0.0.1:9092")
      props.put("client.id", "KafkaProducer")
      //props.put("serializer.class", "kafka.serializer.StringEncoder")
      props.put("key.serializer", classOf[StringSerializer].getName)
      props.put("value.serializer", classOf[StringSerializer].getName)
      //props.put("partitioner.class", "com.colobu.kafka.SimplePartitioner")
      props.put("producer.type", "async")
      //props.put("request.required.acks", "1")


      val producer = new KafkaProducer[String, String](props)

      val metaF: Future[RecordMetadata] = producer.send(new ProducerRecord[String, String]("test-topic", "Test message"))

      val meta = metaF.get() // blocking!
      val msgLog =
        s"""
           |offset    = ${meta.offset()}
           |partition = ${meta.partition()}
           |topic     = ${meta.topic()}
        """.stripMargin

      println(msgLog)

      producer.close()

      /////////////////////////

      val consumerProps = new Properties()
      consumerProps.put("bootstrap.servers", "localhost:9092")
      consumerProps.put("zookeeper.connect", "127.0.0.1:2181")
      consumerProps.put("group.id", "1")
      consumerProps.put("auto.offset.reset", "latest")
      consumerProps.put("zookeeper.session.timeout.ms", "400")
      consumerProps.put("zookeeper.sync.time.ms", "200")
      consumerProps.put("auto.commit.interval.ms", "1000")
      consumerProps.put("key.deserializer", classOf[StringDeserializer].getName)
      consumerProps.put("value.deserializer", classOf[StringDeserializer].getName)

      val consumer = new KafkaConsumer[String, String](consumerProps)

      import scala.collection.JavaConversions._

      consumer.subscribe(Seq("test-topic"))

      var hack = true

      while (hack) {
        val records = consumer.poll(1000)

        println(s"===> RECORDS = $records")

        records.iterator().foreach { r =>
          println(s"===> HEY, got $r}")
          hack = false
        }
      }

      ok
    }
  }
}