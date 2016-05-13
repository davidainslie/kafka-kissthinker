/*
package com.kissthinker.kafka

import java.util.Properties
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import kafka.consumer.{Consumer, ConsumerConfig}

class EmbeddedKafka2Spec(implicit ev: ExecutionEnv) extends Specification {
  "blah" should {
    "blah" in new EmbeddedKafka {
      skipped

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

      /////////////////////////

      val subscriber = Subscriber("test-topic")

      val promise = Promise[String]()

      Future {
        subscriber.read.foreach { s =>
          println(s" Subscriber got: $s")
          promise.success(s)
        }
      }

      ////////////////

      producer.send(new ProducerRecord[String, String]("test-topic", "Test message"))
      producer.close()

      promise.future must beEqualTo("Test message").awaitFor(3 seconds)
    }
  }

  case class Subscriber(topic: String) {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("zookeeper.connect", "127.0.0.1:2181")
    props.put("group.id", "1")
    props.put("auto.offset.reset", "largest")
    props.put("zookeeper.session.timeout.ms", "400")
    props.put("zookeeper.sync.time.ms", "200")
    props.put("auto.commit.interval.ms", "1000")
    //props.put("key.deserializer", classOf[StringDeserializer].getName)
    //props.put("value.deserializer", classOf[StringDeserializer].getName)
    //props.put("key.deserializer", classOf[ByteArrayDeserializer].getName)
    //props.put("value.deserializer", classOf[ByteArrayDeserializer].getName)

    protected val config = new ConsumerConfig(props)

    private lazy val consumerConnector = Consumer create config //new KafkaConsumer[String, String](consumerProps)
    val threadNum = 1

    private lazy val consumerMap = consumerConnector.createMessageStreams(Map(topic -> threadNum))
    private lazy val stream = consumerMap.getOrElse(topic, List()).head

    def read: Stream[String] = Stream.cons(new String(stream.head.message()), read)
  }
}*/
