package distrimon

import akka.actor.{ActorPath, ActorRef}

class RemoteActor(dst: String, tunnel: ActorRef) {
  def tell(msg: Any)(implicit src: ActorPath): Unit = tunnel ! Envolope(src.toStringWithoutAddress, dst, msg)
  def !(msg: Any)(implicit src: ActorPath) = tell(msg)
}