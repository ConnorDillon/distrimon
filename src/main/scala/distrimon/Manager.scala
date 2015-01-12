package distrimon

import Manager._

class Manager extends BaseActor {
  val minions = collection.mutable.Map(0 -> addChild[MinionTunnel]("minion0"),
                                       1 -> addChild[MinionTunnel]("minion1"))

  def receive = {
    case GetMinion(id) => minions get id match {
      case None => sender ! NoSuchMinion
      case Some(minion) => sender ! minion
    }
    case AddMinion(id) => minions get id match {
      case None => minions(id) = addChild[MinionTunnel](s"minion$id")
      case Some(_) => Unit
    }
    case RemoveMinion(id) => minions remove id match {
      case None => sender ! NoSuchMinion
      case Some(minion) => context stop minion
    }
    case TellMinion(id, msg) => minions get id match {
      case None => sender ! NoSuchMinion
      case Some(minion) => minion forward msg
    }
    case TellAllMinions(msg) => minions.values.foreach(_ ! msg)
  }
}

object Manager {
  case object NoSuchMinion
  case class AddMinion(id: Int)
  case class RemoveMinion(id: Int)
  case class GetMinion(id: Int)
  case class TellMinion(id: Int, env: Envolope)
  case class TellAllMinions(env: Envolope)
}