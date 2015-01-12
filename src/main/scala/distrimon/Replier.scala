package distrimon

trait Replier extends BaseActor {
  def reply(e: Envolope, msg: Any): Unit = sender ! e.reply(msg)
}