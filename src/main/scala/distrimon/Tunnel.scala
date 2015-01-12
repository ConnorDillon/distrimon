package distrimon

import collection.mutable.{Queue=>MQueue}

trait Tunnel extends FSM {

  type Queue = MQueue[Envolope]

  object Queue {
    def apply(e: Envolope*): Queue = MQueue[Envolope](e: _*)
  }

  trait Open extends State with EnvolopeDelivery {
    def send(e: Envolope): Unit

    is { case e: Envolope => send(e) }
  }

  trait Closed extends State with EnvolopeDelivery {
    val queue: Queue

    def retry(e: Envolope): Unit

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
    def deliver(e: Envolope): Unit = context.actorSelection(e.dst) ! e
    def retry(e: Envolope): Unit = self ! e
  }
}