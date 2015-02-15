package distrimon

import Ponger._
import Pinger._

class Ponger extends BaseActor {
  def receive = {
    case Ping(id) => sender() ! Pong(id)
  }
}

object Ponger {
  case class Ping(id: Int)
}