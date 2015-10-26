package struct

class Sphere private(val min: Vector3, val max: Vector3) {
  val center = Vector3.lerp(min, max, 0.5f)
  val distance = Vector3.distance(min, max)
  val radius = distance / 2.0f

  def this(center: Vector3, length: Float) = {
    this(center - Vector3.up * length, center + Vector3.up * length)
  }





}


