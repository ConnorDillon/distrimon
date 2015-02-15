package distrimon

import concurrent.duration.FiniteDuration

trait ClockWorker extends BaseActor {
  private var cnt = 0
  def count = cnt

  val interval: FiniteDuration
  def schedule(): Unit = context.system.scheduler.scheduleOnce(interval, self, Tick)
  def work(): Unit

  def receive = {
    case Tick =>
      work()
      schedule()
      cnt += 1
  }

  override def preStart(): Unit = schedule()
  override def postRestart(reason: Throwable): Unit = { }
}