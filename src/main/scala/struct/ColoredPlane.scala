package struct

import raytracer.Colored

class ColoredPlane(override val normal: Vector3, override val w: Float, val color: Color) extends Plane(normal, w) with Colored {

/*  def project(projection: Matrix4): Sphere = {

    val anyPointOnPlane = normal * w
    val projectedAnyPointOnPlane = Vector3.transformCoordinates(anyPointOnPlane, projection)

    val transpose = projection.invert().transpose

    val projectedNormal = Vector3.transformCoordinates(normal, transpose)

    val nonTranslationProjection =


    val projectedW =

      new ColoredPlane(min, max)
  }*/

}

object ColoredPlane {

  def apply(normal: Vector3, w: Float, color: Color) = new ColoredPlane(normal, w, color)

  def fromPoints(a: Vector3, b: Vector3, c: Vector3, color: Color): Unit = {
    val v0 = c - a
    val v1 = b - a
    if (v0.magnitude == 0 || v1.magnitude == 0) {
      throw new IllegalArgumentException(s"Plane cannot be created from those vectors $a $b $c")
    }
    val n = (v0 cross v1).normalised
    new ColoredPlane(n, n dot a, color)
  }


}
