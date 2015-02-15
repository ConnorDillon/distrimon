package distrimon.minion

import distrimon.{addActor, BaseActor}
import distrimon.master.MasterMsg

class Minion(id: Int) extends BaseActor {
  val master = addChild[MasterTunnel]("master", id)
  val shell = addActor[Shell]("shell")
  val pinger = addChild[Pinger]("pinger")

  def receive = {
    case x: MasterMsg => master forward x
    case x: Shell.Msg => shell forward x
    case x: Pinger.Msg => pinger forward x
  }
}