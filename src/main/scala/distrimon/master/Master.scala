package distrimon.master

import distrimon.BaseActor
import java.net.InetSocketAddress

class Master(val addr: InetSocketAddress) extends BaseActor {
  val manager = addChild[Manager]("manager")
  val server = addChild[MinionServer]("server", addr, manager)
  val ponger = addChild[Ponger]("ponger")

  def receive = unexpected
}