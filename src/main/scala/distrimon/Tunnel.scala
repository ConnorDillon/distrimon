package distrimon

import scala.collection.mutable
import akka.actor.ActorRef
import akka.pattern.ask

trait Tunnel extends FSM {

  val stubs = mutable.Map[String, ActorRef]()

  type Queue = mutable.Queue[Envolope]

  object Queue {
    def apply(e: Envolope*): Queue = mutable.Queue[Envolope](e: _*)
  }

  trait Open extends State with EnvolopeDelivery {
    def send(e: Envolope): Unit

    is { case e: Envolope => send(e) }
  }

  trait Closed extends State with EnvolopeDelivery {
    val queue: Queue

    def flush(): Unit = {
      log.info(s"flushing queue: $queue")
      queue foreach retry
    }

    is {
      case e: Envolope => log.info(s"queueing: $e")
                          queue enqueue e
    }
  }

  trait EnvolopeDelivery {
    def retry(e: Envolope): Unit = self ! e

    def deliver(env: Envolope): Unit = {
      def askDst(dst: ActorRef, env: Envolope): Unit = {
        val fut = dst ? env.msg
        fut.map(msg => self ! env.reply(msg))
      }

      def getStub(src: String): ActorRef = stubs get env.src match {
        case Some(stub) => stub
        case None =>
          log.info(s"creating stub for: $src")
          val stub = context.actorOf(Stub(src))
          stubs(src) = stub
          stub
      }
      
      def checkTemp(src: String): Boolean = src.take(6) == "/temp/"

      if (env.dst == "none") {
        val dst = context.parent
        if (checkTemp(env.src)) {
          askDst(dst, env)
        } else {
          val stub = getStub(env.src)
          dst.tell(env.msg, stub)
        }
      } else {
        val dstFut = context.actorSelection(env.dst).resolveOne()
        if (checkTemp(env.src)) {
          dstFut.map(x => askDst(x, env))
        } else {
          val stub = getStub(env.src)
          dstFut.map(_.tell(env.msg, stub))
        }
      }
    }
  }
}