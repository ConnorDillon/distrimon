package distrimon

import java.net.InetSocketAddress

package object minion {
  val firstMaster = new InetSocketAddress("localhost", 10001)
  val backupMaster = new InetSocketAddress("localhost", 10002)
}
