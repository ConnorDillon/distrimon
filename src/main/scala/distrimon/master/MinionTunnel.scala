package distrimon.master

import akka.actor.{PoisonPill, ActorRef}
import distrimon.{Envolope, Tunnel}
import MinionTunnel._

class MinionTunnel extends Tunnel {
  val initial = Closed(Queue())

  case class Closed(queue: Queue) extends super.Closed { is {
    case Recv(e) => deliver(e)
    case ConnUp => flush()
                   become(Open(sender))
  }}

  case class Open(conn: ActorRef) extends super.Open {
    def send(e: Envolope): Unit =  conn ! e

    is { 
      case Recv(e) => deliver(e)
      case ConnDown => become(Closed(Queue()))  
                       conn ! PoisonPill
    }
  }
}

object MinionTunnel {
  case object ConnUp
  case object ConnDown
  case class Recv(e: Envolope)
}