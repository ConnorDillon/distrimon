package distrimon

import akka.actor.Actor

trait State {
  type Receive = Actor.Receive

  def is(fn: Receive) = recv match {
    case Some(x) => recv = Some(fn orElse x)
    case None => recv = Some(fn)
  }

  private var recv: Option[Receive] = None
  def receive: Receive = recv.get
}
