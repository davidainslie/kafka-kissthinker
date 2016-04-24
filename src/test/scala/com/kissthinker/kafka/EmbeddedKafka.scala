package com.kissthinker.kafka

import java.io.File
import java.net.InetSocketAddress
import java.util.Properties
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.apache.zookeeper.server.{ServerCnxnFactory, ZooKeeperServer}
import org.specs2.execute.{AsResult, Result}
import org.specs2.matcher.Scope
import de.flapdoodle.embed.process.runtime.Network._

trait EmbeddedKafka extends Scope with ComposableAround {
  val zookeeper = new Zookeeper
  val kafka = new Kafka(zookeeper)

  implicit val subscriberConfig = new SubscriberConfig(zookeeper.address, kafka.address)
  implicit val publisherConfig = new PublisherConfig(kafka.address)

  override def around[R: AsResult](r: => R): Result = {
    try {
      zookeeper.start
      kafka.start
      super.around(r)
    } finally {
      kafka.stop
      zookeeper.stop
    }
  }
}

/**
  * On Mac:
  * <pre>
  *   brew install zookeeper
  *   zkServer start
  *   zkServer stop
  * </pre>
  */
class Zookeeper {
  val logs = new File("./logs/zookeeper")

  val port = getFreeServerPort

  val address = s"127.0.0.1:$port"

  val server = new ZooKeeperServer(/*snapDir*/ logs, /*logDir*/ logs, /*tickTime*/ 2000)

  def start = {
    println(s"Starting Zookeeper...")
    val factory = ServerCnxnFactory.createFactory
    factory.configure(new InetSocketAddress("127.0.0.1", /*2181*/ port), /*maxClientCnxns*/ 1024)
    factory.startup(server)

    // Let Zookeeper get going
    TimeUnit.SECONDS.sleep(1)

    server
  }

  def stop = {
    println(s"...Stopping Zookeeper")
    server.shutdown()
    server
  }
}

/**
  * On a Mac:
  * <pre>
  *   brew install kafka
  *   kafka-server-start /usr/local/etc/kafka/server.properties
  * </pre>
  */
class Kafka(zookeeper: Zookeeper) {
  val port = getFreeServerPort

  val address = s"127.0.0.1:$port"

  val props = new Properties()
  //props.put("zookeeper.connect", "127.0.0.1:2181")
  props.put("zookeeper.connect", zookeeper.address)
  //props.setProperty("brokerid", "0")
  props.setProperty("brokerid", "1")
  props.setProperty("host.name", "127.0.0.1")
  props.setProperty("port", s"$port")
  props.setProperty("num.partitions", "1")
  props.setProperty("default.replication.factor", "1")
  props.setProperty("zookeeper.connection.timeout.ms", "1000")
  props.setProperty("log.dirs", "./logs/kafka")
  props.setProperty("enable.zookeeper", "true")
  props.setProperty("auto.create.topics.enable", "true")
  props.setProperty("log.flush.interval.messages", 1.toString)

  val kafkaServer = new KafkaServer(new KafkaConfig(props))

  def start = {
    Future {
      println(s"Starting Kafka...")
      kafkaServer.startup()
    }

    // Let Kafka get going
    TimeUnit.SECONDS.sleep(2)

    this
  }

  def stop = {
    Future {
      println(s"...Stopping Kafka")
      kafkaServer.shutdown()
    }

    kafkaServer.awaitShutdown()

    this
  }
}