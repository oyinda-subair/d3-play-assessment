package testkit

import scala.util.Random

object Util {
  private val random = new Random(System.currentTimeMillis)

  def string10 = new String(Array.fill(10)((random.nextInt(26) + 65).toByte))
}
