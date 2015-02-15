package distrimon

import akka.actor.Props

class Stub(path: String) extends BaseActor {
  def receive = {
    case x => context.parent ! Envolope(sender().path.toStringWithoutAddress, path, x)
  }
}

object Stub {
  def apply(path: String): Props = Props(new Stub(path))
}