package distrimon

import java.net.InetSocketAddress
import akka.actor.ActorRef
import akka.io.{IO, Tcp}
import akka.util.ByteString
import akka.io.Tcp.{Connected=>TcpConnected, Write, Connect, CommandFailed, Received}
import concurrent.duration._

trait Client extends Connection with Tunnel {
  connect()

  val initial = Connecting(Queue())

  case class Closed(queue: Queue) extends super.Closed with Disconnected { is {
    case Tick => connect()
                 become(Connecting(queue))
  }}

  case class Connecting(queue: Queue) extends super.Closed with Disconnected { is {
    case c: TcpConnected =>
      register(sender)
      sender ! Write(ByteString(id.toString))
      log.info(s"identifying with ID: $id")
      become(Identifying(sender, queue))
    case CommandFailed(_: Connect) => reconnect(queue)
  }}

  case class Identifying(conn: ActorRef, queue: Queue) extends Connected with super.Closed {
    def handleClose(): Unit = reconnect(queue)

    is {
      case Received(data) if data.utf8String != "OK" => context stop self
      case Received(data) if data.utf8String == "OK" => flush()
                                                        become(Open(conn))
    }
  }

  case class Open(conn: ActorRef) extends super.Open with Connected {
    def handleClose(): Unit = reconnect(Queue())
  }

  val id: Int

  def getAddress: InetSocketAddress

  def reconnect(queue: Queue): Unit = {
    context.system.scheduler.scheduleOnce(10.seconds, self, Tick)
    become(Closed(queue))
  }

  def connect(): Unit = {
    val addr = getAddress
    log.info(s"connecting to: $addr")
    IO(Tcp) ! Connect(addr)
  }
}