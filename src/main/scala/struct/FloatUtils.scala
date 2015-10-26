package struct

object FloatUtils {

  def clamp(value: Float, min: Float = 0, max: Float = 1): Float = {
    Math.max(min, Math.min(value, max))
  }

  def lerp(min: Float, max: Float, value: Float): Float = {
    min + (max - min) * clamp(value)
  }

}
