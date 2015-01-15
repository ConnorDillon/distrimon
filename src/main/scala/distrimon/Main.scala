package distrimon

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import concurrent.Await
import concurrent.duration._

object Main extends App {
  implicit val system = ActorSystem("distriSystem")

  ping()

  def cmd(): Unit = {
    import Shell._
    implicit val timeout = Timeout(10.seconds)
    val shell = addActor[Shell]("shell")
    val future = shell ? Envolope("", "", Command("sleep 2; ls -la | wc -l"))
    println(Await.result(future, 10.seconds))
  }

  def ping(): Unit = {
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
}