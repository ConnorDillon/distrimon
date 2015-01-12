package distrimon

import akka.actor.ActorRef
import akka.io.Tcp.{ConnectionClosed, Write, Close, Received}
import akka.util.ByteString
import Manager._
import MinionTunnel._

class MinionHandler(val manager: ActorRef, val conn: ActorRef) extends Connection {
  val initial = Unidentified

  register(conn)

  case object Unidentified extends State { is {
    case _: ConnectionClosed => context stop self
    case Received(data) =>
      val minionId = data.utf8String.toInt
      log.info(s"identifying minion: $minionId")
      manager ! GetMinion(minionId)
      become(Identifying)
  }}

  case object Identifying extends State { is {
    case _: ConnectionClosed => context stop self
    case NoSuchMinion =>
      log.error("identifying failed")
      conn ! Write(ByteString("FAIL"))
      conn ! Close
      context stop self
    case minion: ActorRef =>
      log.error("succesfully identified")
      conn ! Write(ByteString("OK"))
      minion ! ConnUp
      become(Identified(minion, conn))
  }}

  case class Identified(minion: ActorRef, conn: ActorRef) extends Connected with Delivery {
    def handleClose(): Unit = {
      log.error("connection closed unexpectedly")
      minion ! ConnDown
      become(Disconnected(minion))
    }
  }

  case class Disconnected(minion: ActorRef) extends super.Disconnected with Delivery { is {
    case e: Envolope => retry(e)
  }}


  trait Delivery {
    val minion: ActorRef
    def deliver(e: Envolope): Unit = minion ! Recv(e)
    def retry(e: Envolope): Unit = minion ! e
  }
}