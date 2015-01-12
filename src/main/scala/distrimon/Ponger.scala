package distrimon

import Ponger._
import Pinger._

class Ponger extends Replier {
  def receive = {
    case e @ Envolope(_, _, Ping(id)) => reply(e, Pong(id))
  }
}

object Ponger {
  case class Ping(id: Int)
}