import java.net.InetSocketAddress
import akka.actor.{ActorSystem, ActorRef, Props}
import scala.reflect.ClassTag

package object distrimon {
  def addActor[T: ClassTag](name: String, args: Any*)(implicit system: ActorSystem): ActorRef = {
    val cls = implicitly[ClassTag[T]].runtimeClass
    system.actorOf(Props(cls, args: _*), name)
  }

  val firstMaster = new InetSocketAddress("localhost", 10001)
  val backupMaster = new InetSocketAddress("localhost", 10002)

  case object Tick
}