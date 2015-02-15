package distrimon

import scala.sys.process.stringSeqToProcess
import scala.concurrent.Future
import akka.actor.ActorRef

class Shell extends BaseActor {
  import Shell._

  def exec(cmd: String): String = stringSeqToProcess(Seq("sh", "-c", cmd)).!!

  def handle(sender: ActorRef, id: Int, cmd: String): Unit = Future(exec(cmd)).map(res => sender ! Result(id, res))

  def receive = {
    case Command(id, cmd) =>
      log.info(s"executing command: $cmd")
      handle(sender(), id, cmd)
  }
}

object Shell {
  case class Command(id: Int, cmd: String)
  case class Result(id: Int, result: String)
}