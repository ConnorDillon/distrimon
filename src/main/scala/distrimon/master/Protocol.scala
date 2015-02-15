package distrimon.master

trait Protocol extends distrimon.Protocol {
  trait Msg extends MasterMsg
}

trait MasterMsg extends distrimon.AnyMsg