package distrimon

import akka.actor.ActorRef

trait Sender extends BaseActor {
  implicit val src = context.self.path
  val tunnel: ActorRef
  def remote(dst: String) = new RemoteActor(dst, tunnel)
}