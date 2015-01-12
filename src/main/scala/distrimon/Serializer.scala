package distrimon

import java.io.{ObjectInputStream, ObjectOutputStream}
import akka.util.{ByteStringBuilder, ByteString}

trait Serializer {
  def serialize(e: Any): ByteString = {
    val bsb = new ByteStringBuilder
    val oos = new ObjectOutputStream(bsb.asOutputStream)
    oos.writeObject(e)
    bsb.result()
  }

  def deserialize(bs: ByteString): List[Any] = {
    val objects = collection.mutable.ListBuffer[Any]()
    val input = bs.iterator.asInputStream
    while (input.available > 0) {
      val ois = new ObjectInputStream(input)
      objects += ois.readObject()
    }
    objects.toList
  }
}