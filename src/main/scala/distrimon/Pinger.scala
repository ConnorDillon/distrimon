package distrimon

import akka.actor.ActorRef
import concurrent.duration._
import Pinger._
import Ponger._

class Pinger(val tunnel: ActorRef) extends ClockWorker with Sender {
  val interval = 1000.millis
  val ponger = remote("/user/master/ponger")

  def work(): Unit = ponger ! Ping(count)

  override def receive = {
    case Envolope(_, _, Pong(_)) => Unit
    case x => super.receive(x)
  }
}

object Pinger {
  case class Pong(id: Int)
}