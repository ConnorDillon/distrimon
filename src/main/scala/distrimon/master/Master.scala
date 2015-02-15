package distrimon.master

import java.net.InetSocketAddress
import distrimon.BaseActor

class Master(val addr: InetSocketAddress) extends BaseActor {
  val manager = addChild[Manager]("manager")
  val server = addChild[MinionServer]("server", addr, manager)
  val ponger = addChild[Ponger]("ponger")

  def receive = {
    case x: Ponger.Msg => ponger forward x
    case x => log.error(s"unexpected: $x")
  }
}