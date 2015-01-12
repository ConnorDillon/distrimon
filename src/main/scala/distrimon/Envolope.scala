package distrimon

case class Envolope(src: String, dst: String, msg: Any) {
  def reply(msg: Any) = Envolope(dst, src, msg)
}