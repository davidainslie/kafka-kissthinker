package com.kissthinker.kafka.collection.array

trait Writer[M] {
  def write(m: M): Array[Byte]
}

object Writer {
  implicit object StringWrite extends Writer[String] {
    override def write(m: String): Array[Byte] = m.getBytes
  }
}