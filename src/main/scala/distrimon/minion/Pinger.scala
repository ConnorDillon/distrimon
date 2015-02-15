package distrimon.minion

import concurrent.duration._
import distrimon.ClockWorker
import distrimon.master.Ponger._
import Pinger._

class Pinger extends ClockWorker {
  val interval = 1.second

  def work(): Unit = context.parent ! Ping(count)

  override def receive = {
    case Pong(_) => Unit
    case x => super.receive(x)
  }
}

object Pinger extends Protocol {
  case class Pong(id: Int) extends Msg
}