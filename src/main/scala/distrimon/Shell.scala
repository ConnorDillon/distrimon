package distrimon

import akka.actor.ActorRef
import scala.concurrent.Future
import sys.process.stringSeqToProcess
import Shell._

class Shell extends BaseActor {
  import context.dispatcher

  def exec(sender: ActorRef, env: Envolope): Completed = env.msg match {
    case Command(id, cmd) =>
      log.info(s"executing command: $cmd")
      val result = Result(id, stringSeqToProcess(Seq("sh", "-c", cmd)).!!.init)
      log.info(s"command: ( $cmd ) completed with result: ${result.result}")
      Completed(sender, env.reply(result))
  }
  
  def receive = {
    case e: Envolope => Future {
      exec(sender, e)
    } onSuccess {
      case Completed(sender, env) => sender ! env
    }
  }

  case class Completed(sender: ActorRef, envolope: Envolope)
}

object Shell {
  case class Command(id: Int, cmd: String)
  case class Result(id: Int, result: String)
}