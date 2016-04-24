package com.kissthinker.kafka.collection.array

import scala.util.Try

trait Reader[M] {
  def read(bytes: Array[Byte]): Try[M]
}

object Reader {
  implicit object StringReader extends Reader[String] {
    override def read(bytes: Array[Byte]): Try[String] = Try {
      new String(bytes)
    }
  }
}