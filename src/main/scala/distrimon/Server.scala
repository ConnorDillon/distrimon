package distrimon

import java.net.InetSocketAddress

import akka.actor.ActorRef
import akka.io.{IO, Tcp}
import Tcp.{Bind, Bound, CommandFailed, Connected}

trait Server extends FSM {
  val address: InetSocketAddress

  bind()

  val initial = Binding

  case object Binding extends State { is {
    case Bound(addr) => log.info(s"server bound on: $addr")
                        become(Listening)
    case CommandFailed(bind: Bind) => log.error(s"server failed to bind on: ${bind.localAddress}")
                                      context stop self
  }}

  case object Listening extends State { is {
    case info: Connected => log.info(s"accepted connection from: $info")
                            handle(sender, info)
  }}

  def bind(): Unit = IO(Tcp) ! Bind(self, address)

  def handle(conn: ActorRef, info: Connected): Unit
}