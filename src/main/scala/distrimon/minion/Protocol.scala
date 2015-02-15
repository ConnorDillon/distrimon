package distrimon.minion

trait Protocol extends distrimon.Protocol {
  trait Msg extends MinionMsg
}

trait MinionMsg extends distrimon.AnyMsg