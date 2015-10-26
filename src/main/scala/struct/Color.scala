package struct

class Color private(red: Int, green: Int, blue: Int) {
  require(red >= 0 && red <= 255, s"Red component is out of range: $red")
  require(green >= 0 && green <= 255, s"Green component is out of range: $green")
  require(blue >= 0 && blue <= 255, s"Blue component is out of range: $blue")

  val redByte: Byte = (red & 0xFF).toByte
  val greenByte: Byte = (green & 0xFF).toByte
  val blueByte: Byte = (blue & 0xFF).toByte

  def *(multiplier: Float): Color = {
    val newRed = (red.toFloat * multiplier).toInt
    val newGreen = (green.toFloat * multiplier).toInt
    val newBlue = (green.toFloat * multiplier).toInt

    Color(Color.clampColor(newRed).toInt, Color.clampColor(newGreen).toInt, Color.clampColor(newBlue).toInt)
  }

  /*  println(redByte & 0xff)
    println(greenByte & 0xff)
    println(blueByte & 0xff)*/

}

object Color {
  def apply(red: Int, green: Int, blue: Int): Color = new Color(red, green, blue)

  def apply(red: Float, green: Float, blue: Float): Color = new Color(lerpColor(red), lerpColor(green), lerpColor(blue))

  private def lerpColor(red: Float): Int = {
    FloatUtils.lerp(0f, 255f, red).toInt
  }

  def clampColor(value: Int) = FloatUtils.clamp(value, 0, 255)
}
