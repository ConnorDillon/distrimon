package distrimon

trait FSM extends BaseActor {
  def become(state: State): Unit = context become state.receive

  val initial: State
  def receive = initial.receive
}