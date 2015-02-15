package distrimon.minion

import distrimon.{addActor, BaseActor}

class Minion(id: Int) extends BaseActor {
  val master = addChild[MasterTunnel]("master", id)
  val shell = addActor[Shell]("shell")
  val pinger = addChild[Pinger]("pinger", master)

  def receive = unexpected
}