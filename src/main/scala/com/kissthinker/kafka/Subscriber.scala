package com.kissthinker.kafka

import scala.util.Try
import com.kissthinker.kafka.collection.array.Reader

class Subscriber[M : Reader](topic: String)(implicit config: SubscriberConfig) {
  private val threadNumber = 1

  private lazy val consumerConnector = Consumer create new ConsumerConfig(config)

  private lazy val consumerMap = consumerConnector createMessageStreams Map(topic -> threadNumber)

  private lazy val stream = consumerMap.getOrElse(topic, List()).head

  def subscribe: Stream[Try[M]] = Stream.cons(implicitly[Reader[M]].read(stream.head.message()), subscribe)
}