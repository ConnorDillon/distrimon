package distrimon

import akka.actor.ActorRef
import akka.io.Tcp.{Received, Write, Register, ConnectionClosed, CommandFailed}

trait Connection extends FSM with Serializer {
  def register(conn: ActorRef): Unit = conn ! Register(self)

  trait Connected extends Disconnected {
    val conn: ActorRef

    def handleClose(): Unit

    def send(e: Envolope): Unit = {
      log.info(s"sent: $e")
      conn ! Write(serialize(e))
    }

    is {
      case e: Envolope => send(e)
      case _: ConnectionClosed => handleClose()
    }
  }
  
  trait Disconnected extends State {
    def deliver(e: Envolope): Unit
    
    def retry(e: Envolope): Unit
    
    is {
      case CommandFailed(Write(data, _)) =>
        retry(deserialize(data).head.asInstanceOf[Envolope])
      case Received(data) => deserialize(data) foreach { obj =>
        log.info(s"received: $obj")
        deliver(obj.asInstanceOf[Envolope])
      }  
    }
  }
}