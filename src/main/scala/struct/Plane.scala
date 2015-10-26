package struct

class Plane private (val normal: Vector3, val w: Float) {


}

object Plane {

  def apply(normal: Vector3, w: Float) = new Plane(normal, w)

  def fromPoints(a: Vector3, b: Vector3, c: Vector3): Unit ={
    val v0 = c - a
    val v1 = b -a
    if (v0.magnitude == 0 || v1.magnitude == 0) {
      throw new IllegalArgumentException(s"Plane cannot be created from those vectors $a $b $c")
    }
    val n = (v0 cross v1).normalised
    new Plane(n, n dot a)
  }

}
