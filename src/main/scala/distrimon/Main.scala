package distrimon

import akka.actor.ActorSystem

object Main extends App {
  implicit val system = ActorSystem("distriSystem")
  val minion0 = addActor[Minion]("minion0", 0)
  Thread.sleep(5000)
  val master = addActor[Master]("master", firstMaster)
  Thread.sleep(20000)
  system stop master
  Thread.sleep(5000)
  val newmaster = addActor[Master]("master", backupMaster)
  Thread.sleep(20000)
  system stop minion0
  Thread.sleep(5000)
  system stop newmaster
}