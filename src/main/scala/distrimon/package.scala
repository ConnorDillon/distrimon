import java.net.InetSocketAddress
import scala.concurrent.duration._
import scala.reflect.ClassTag
import akka.actor.{ActorSystem, ActorRef, Props}
import akka.util.Timeout

package object distrimon {
  def addActor[T: ClassTag](name: String, args: Any*)(implicit system: ActorSystem): ActorRef = {
    val cls = implicitly[ClassTag[T]].runtimeClass
    system.actorOf(Props(cls, args: _*), name)
  }

  val firstMaster = new InetSocketAddress("localhost", 10001)
  val backupMaster = new InetSocketAddress("localhost", 10002)

  case object Tick

  implicit val timeout = Timeout(5.seconds)
}
