package struct

object FloatUtils {

  val invPI: Float = 0.3183098861837906715f
  val invTWO_PI = 0.1591549430918953358f

  def clamp(value: Float, min: Float = 0, max: Float = 1): Float = {
    Math.max(min, Math.min(value, max))
  }

  def lerp(min: Float, max: Float, value: Float): Float = {
    min + (max - min) * clamp(value)
  }

}
