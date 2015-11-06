package raytracer

import struct.{Color, Plane, Ray, Vector3}

class ColoredTriangle protected(val a: Vector3, val b: Vector3, val c: Vector3, val material: Material, val na: Vector3 = Vector3.up, val nb: Vector3 = Vector3.up, val nc: Vector3 = Vector3.up) extends RayIntersectable with Colored {
  lazy val trianglesPlane = Plane.fromPoints(a, b, c)

  override def intersects(ray: Ray): RayHit = {
    def noHit = RayHit(None, ray, this)
    def hit(hit: Vector3) = RayHit(Some(hit), ray, this)

    val rayHit = trianglesPlane.intersects(ray)
    if (rayHit.hit.isEmpty) {
      noHit
    } else {
      val hitPoint = rayHit.hit.get

      val fa = a - hitPoint
      val fb = b - hitPoint
      val fc = c - hitPoint

      val facrossfb = fa cross fb
      if ((facrossfb dot -trianglesPlane.normal) < ColoredTriangle.MINUS_ZERO) {
        noHit
      } else {
        val fbcrossfc = fb cross fc
        if ((fbcrossfc dot -trianglesPlane.normal) < ColoredTriangle.MINUS_ZERO) {
          noHit
        } else {
          val fccrossfa = fc cross fa
          if ((fccrossfa dot -trianglesPlane.normal) < ColoredTriangle.MINUS_ZERO) {
            noHit
          } else {
            hit(rayHit.hit.get)
          }
        }
      }
    }
  }

  override def color(vector: Vector3): Color = material.diffuse
}

object ColoredTriangle {

  def apply(a: Vector3, b: Vector3, c: Vector3, material: Material, na: Vector3 = Vector3.up, nb: Vector3 = Vector3.up, nc: Vector3 = Vector3.up) = new ColoredTriangle(a, b, c, material, na, nb, nc)

  val PLUS_ZERO = 0.00001f
  val MINUS_ZERO = -0.0001f

}
