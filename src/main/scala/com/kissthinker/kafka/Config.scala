package com.kissthinker.kafka

import java.util.Properties
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer}

abstract class Config(kafkaAddress: String) extends Properties {
  put("bootstrap.servers", kafkaAddress)
}

class PublisherConfig(kafkaAddress: String) extends Config(kafkaAddress) {
  put("metadata.broker.list", kafkaAddress)
  //put("client.id", "kafka-publisher")
  put("key.serializer", classOf[ByteArraySerializer].getName)
  put("value.serializer", classOf[ByteArraySerializer].getName)
  put("producer.type", "async")
}

class SubscriberConfig(zookeeperAddress: String, kafkaAddress: String) extends Config(kafkaAddress) {
  //put("zookeeper.connect", "127.0.0.1:2181")
  put("zookeeper.connect", zookeeperAddress)
  //put("group.id", "1")
  put("group.id", "2")
  put("auto.offset.reset", "largest")
  put("zookeeper.session.timeout.ms", "400")
  put("zookeeper.sync.time.ms", "200")
  put("auto.commit.interval.ms", "1000")
  put("key.deserializer", classOf[ByteArrayDeserializer].getName)
  put("value.deserializer", classOf[ByteArrayDeserializer].getName)
}