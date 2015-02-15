package distrimon.minion

import akka.actor.ActorRef
import concurrent.duration._
import distrimon.{ClockWorker, Sender}
import distrimon.master.Ponger._
import Pinger._

class Pinger(val tunnel: ActorRef) extends ClockWorker with Sender {
  val interval = 1.second
  val ponger = remote("/user/master/ponger")

  def work(): Unit = ponger ! Ping(count)

  override def receive = {
    case Pong(_) => Unit
    case x => super.receive(x)
  }
}

object Pinger {
  case class Pong(id: Int)
}