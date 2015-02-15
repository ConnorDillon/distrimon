package distrimon.minion

import java.net.InetSocketAddress
import distrimon.Client

class MasterTunnel(val id: Int) extends Client {
  def getAddress: InetSocketAddress = {
    val addr = Masters.current
    Masters.switch()
    addr
  }

  object Masters {
    private var cur = firstMaster
    private var bac = backupMaster

    def current = cur
    def backup = bac

    def switch(): Unit = {
      val tmp = cur
      cur = bac
      bac = tmp
    }

    def replace(addr: InetSocketAddress): Unit = bac = addr
  }
}