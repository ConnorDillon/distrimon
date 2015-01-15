package distrimon

import akka.actor.ActorRef
import Manager._

class Manager extends BaseActor {
  val minions = collection.mutable.Map[Int, Manager.Minion]()
  val groups = collection.mutable.Map[Int, Group]()

  load()

  def load(): Unit = {
    self ! AddMinion(0)
    self ! AddMinion(1)
    self ! AddMinion(2)
    self ! AddGroup(0)
    self ! AddMember(0, 0)
    self ! AddMember(0, 1)
  }

  def removeMember(group: Group, minion: Manager.Minion): Unit = {
    groups(group.id) = group.copy(minions = group.minions - minion.id)
  }

  def addMember(group: Group, minion: Manager.Minion): Unit = {
    groups(group.id) = group.copy(minions = group.minions + (minion.id -> minion))
  }

  def receive = {
    case AddMinion(id) =>
      val name = s"minion$id"
      minions(id) = Minion(id, name, addChild[MinionTunnel](name))

    case RemoveMinion(id) => minions remove id match {
      case None => sender ! NoSuchMinion
      case Some(Minion(_, _, tunnel)) =>
        context stop tunnel
        groups.values foreach { group =>
          group.minions get id match {
            case None => ;
            case Some(minion) => removeMember(group, minion)
          }
        }
    }
    case GetMinion(id) => minions get id match {
      case None => sender ! NoSuchMinion
      case Some(minion) => sender ! minion
    }
    case TellMinion(id, msg) => minions get id match {
      case None => sender ! NoSuchMinion
      case Some(minion) => minion.tunnel forward msg
    }
    case AddGroup(id) => groups(id) = Group(id, Map())

    case RemoveGroup(id) => groups remove id match {
      case None => sender ! NoSuchGroup
      case Some(_) => ;
    }
    case GetGroup(id) => groups get id match {
      case None => sender ! NoSuchGroup
      case Some(group) => sender ! group
    }
    case TellGroup(id, msg) => groups get id match {
      case None => sender ! NoSuchGroup
      case Some(group) => group.minions.values.foreach(_.tunnel forward msg)
    }
    case AddMember(gid, mid) => groups get gid match {
      case None => sender ! NoSuchGroup
      case Some(group) => minions get mid match {
        case None => sender ! NoSuchMinion
        case Some(minion) => addMember(group, minion)
      }
    }
    case RemoveMember(gid, mid) => groups get gid match {
      case None => sender ! NoSuchGroup
      case Some(group) => group.minions get mid match {
        case None => sender ! NoSuchMember
        case Some(minion) => removeMember(group, minion)
      }
    }
    case TellAllMinions(msg) => minions.values.foreach(_.tunnel ! msg)
  }
}

object Manager {
  case class Minion(id: Int, name: String, tunnel: ActorRef)
  case class Group(id: Int, minions: Map[Int, Minion])

  case class AddMinion(id: Int)
  case class RemoveMinion(id: Int)
  case class GetMinion(id: Int)
  case class TellMinion(id: Int, env: Envolope)

  case class AddGroup(id: Int)
  case class RemoveGroup(id: Int)
  case class GetGroup(id: Int)
  case class TellGroup(id: Int, env: Envolope)
  case class AddMember(gid: Int, mid: Int)
  case class RemoveMember(gid: Int, mid: Int)

  case class TellAllMinions(env: Envolope)

  case object NoSuchMinion
  case object NoSuchGroup
  case object NoSuchMember
}