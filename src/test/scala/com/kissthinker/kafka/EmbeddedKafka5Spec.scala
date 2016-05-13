/*
package com.kissthinker.kafka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import com.kissthinker.kafka.collection.array.Reader
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class EmbeddedKafka5Spec(implicit ev: ExecutionEnv) extends Specification with EmbeddedKafka {
  "Embedded Kafka" should {
    "have message published and a subscription of said message" in new EmbeddedKafkaContext {
      val messageResult = Promise[String]()

      val subscriber = new Subscriber[String]("test-topic")

      Future {
        subscriber.subscribe collect {
          case Success(message) =>
            println(s"Subscriber got: $message")
            messageResult success message
        }
      }

      val publisher = new Publisher[String]("test-topic")

      publisher publish "Test message"

      messageResult.future must beEqualTo("Test message").awaitFor(3 seconds)
    }

    "have message published and a subscription fail" in new EmbeddedKafkaContext {
      implicit object StringFailureReader extends Reader[String] {
        override def read(bytes: Array[Byte]): Try[String] = Failure(new Exception("Test failure"))
      }

      val messageResult = Promise[String]()

      val subscriber = new Subscriber[String]("test-topic")

      Future {
        subscriber.subscribe collect {
          case f @ Failure(t) =>
            println(s"Subscriber got: $f")
            messageResult success t.getMessage
        }
      }

      val publisher = new Publisher[String]("test-topic")

      publisher publish "Test message"

      messageResult.future must beEqualTo("Test failure").awaitFor(3 seconds)
    }
  }
}*/
