package distrimon

class Minion(id: Int) extends BaseActor {
  val master = addChild[MasterTunnel]("master", id)
  val pinger = addChild[Pinger]("pinger", master)

  def receive = unexpected
}