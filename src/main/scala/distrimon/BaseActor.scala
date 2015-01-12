package distrimon

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import scala.reflect.ClassTag

trait BaseActor extends Actor {
  implicit val system = context.system
  val me = context.self.path.toStringWithoutAddress

  val log = Logging(system, this)
  log.info("starting")

  def unexpected: Receive = { case x => log.error("unexpected: " + x.toString) }

  def addChild[T: ClassTag](name: String, args: Any*): ActorRef = {
    val cls = implicitly[ClassTag[T]].runtimeClass
    context.actorOf(Props(cls, args: _*), name)
  }

  override def postStop(): Unit = log.info("stopped")
}