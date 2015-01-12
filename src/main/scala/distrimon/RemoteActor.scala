package distrimon

import akka.actor.{ActorPath, ActorRef}

class RemoteActor(dst: String, link: ActorRef) {
  def tell(msg: Any)(implicit src: ActorPath): Unit = link ! Envolope(src.toStringWithoutAddress, dst, msg)
  def !(msg: Any)(implicit src: ActorPath) = tell(msg)
}