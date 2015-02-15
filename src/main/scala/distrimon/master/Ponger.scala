package distrimon.master

import distrimon.BaseActor
import distrimon.minion.Pinger._
import Ponger._

class Ponger extends BaseActor {
  def receive = {
    case Ping(id) => sender() ! Pong(id)
  }
}

object Ponger {
  case class Ping(id: Int)
}