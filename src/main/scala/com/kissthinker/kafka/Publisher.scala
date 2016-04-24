package com.kissthinker.kafka

import com.kissthinker.kafka.collection.array.Writer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class Publisher[M : Writer](topic: String)(implicit config: PublisherConfig) {
  val producer = new KafkaProducer[M, Array[Byte]](config)

  def publish(message: M) = producer.send(new ProducerRecord[M, Array[Byte]](topic, implicitly[Writer[M]].write(message)))

  def close() = producer.close()
}