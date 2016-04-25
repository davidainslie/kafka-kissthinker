package com.kissthinker.kafka

import java.util.Properties
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Success, Try}
import com.kissthinker.kafka.collection.array.{Reader, Writer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import kafka.consumer.{Consumer, ConsumerConfig}

class EmbeddedKafka3Spec(implicit ev: ExecutionEnv) extends Specification {
  "Embedded Kafka" should {
    "allow for publication of a message and subscription to said message" in new EmbeddedKafka {
      skipped

      val messageResult = Promise[String]()

      val subscriber = new Subscriber[String]("test-topic")

      Future {
        subscriber.subscribe.collect {
          case Success(message) =>
            println(s"Subscriber got: $message")
            messageResult success message
        }
      }

      val publisher = new Publisher[String]("test-topic")

      publisher.publish("Test message")

      messageResult.future must beEqualTo("Test message").awaitFor(3 seconds)
    }
  }

  class Publisher[M : Writer](topic: String) {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("metadata.broker.list", "127.0.0.1:9092")
    props.put("client.id", "KafkaProducer")
    //props.put("serializer.class", "kafka.serializer.StringEncoder")
    //props.put("key.serializer", classOf[StringSerializer].getName)
    //props.put("value.serializer", classOf[StringSerializer].getName)
    props.put("key.serializer", classOf[ByteArraySerializer].getName)
    props.put("value.serializer", classOf[ByteArraySerializer].getName)
    //props.put("partitioner.class", "com.colobu.kafka.SimplePartitioner")
    props.put("producer.type", "async")
    //props.put("request.required.acks", "1")

    val producer = new KafkaProducer[M, Array[Byte]](props)

    def publish(message: M) = producer.send(new ProducerRecord[M, Array[Byte]](topic, implicitly[Writer[M]].write(message)))

    def close() = producer.close()
  }

  class Subscriber[M : Reader](topic: String) {
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
    props.put("key.deserializer", classOf[ByteArrayDeserializer].getName)
    props.put("value.deserializer", classOf[ByteArrayDeserializer].getName)

    protected val config = new ConsumerConfig(props)

    private lazy val consumerConnector = Consumer.create(config) //new KafkaConsumer[String, String](consumerProps)
    val threadNum = 1

    private lazy val consumerMap = consumerConnector.createMessageStreams(Map(topic -> threadNum))
    private lazy val stream = consumerMap.getOrElse(topic, List()).head

    def subscribe: Stream[Try[M]] = Stream.cons(implicitly[Reader[M]].read(stream.head.message()), subscribe)
  }
}