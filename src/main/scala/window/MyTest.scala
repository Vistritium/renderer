package window

import struct.Vector3

class MyTest {

  def main(args: Array[String]) {
    val eye = Vector3(0f, 0f, 10)
    val target = Vector3(0f, 0f, 0f)

    val minus = target - eye
    println(minus)
    println(minus.normalised)
  }

}
