package struct

class Color private(val red: Int, val green: Int, val blue: Int) {
  require(red >= 0 && red <= 255, s"Red component is out of range: $red")
  require(green >= 0 && green <= 255, s"Green component is out of range: $green")
  require(blue >= 0 && blue <= 255, s"Blue component is out of range: $blue")

  val redByte: Byte = (red & 0xFF).toByte
  val greenByte: Byte = (green & 0xFF).toByte
  val blueByte: Byte = (blue & 0xFF).toByte

  def redFloat = red.toFloat / 255f
  def greenFloat = green.toFloat / 255f
  def blueFloat = blue.toFloat / 255f

  def *(multiplier: Float): Color = {
    val newRed = (red.toFloat * multiplier).toInt
    val newGreen = (green.toFloat * multiplier).toInt
    val newBlue = (blue.toFloat * multiplier).toInt

    Color(Color.clampColor(newRed), Color.clampColor(newGreen), Color.clampColor(newBlue))
  }

  def *(other: Color): Color = {
    val newRed = (redFloat * other.redFloat) * 255f
    val newGreen = (greenFloat * other.greenFloat) * 255f
    val newBlue = (blueFloat * other.blueFloat) * 255f

    Color(Color.clampColor(newRed), Color.clampColor(newGreen), Color.clampColor(newBlue))
  }

  def +(other: Color): Color = {
    val newRed = Color.clampColor(red + other.red)
    val newGreen = Color.clampColor(green + other.green)
    val newblue = Color.clampColor(blue + other.blue)

    Color(newRed, newGreen, newblue)
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

  def clampColor(value: Int): Int = FloatUtils.clamp(value, 0, 255).toInt

  def clampColor(value: Float): Int = FloatUtils.clamp(value, 0, 255).toInt

  def white = Color(255, 255, 255)

  def black = Color(0, 0, 0)

  def fromInt(RGB: Int) = Color((RGB & 0x00ff0000) >> 16, (RGB & 0x0000ff00) >> 8, RGB & 0x000000ff)
}
