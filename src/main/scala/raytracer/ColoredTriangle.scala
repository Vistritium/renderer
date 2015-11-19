package raytracer

import struct._

class ColoredTriangle protected(val a: Vector3, val b: Vector3, val c: Vector3, val material: Material, val na: Vector3 = Vector3.up,
                                val nb: Vector3 = Vector3.up, val nc: Vector3 = Vector3.up, val texA: Vector2 = Vector2.zero,
                                val texB: Vector2 = Vector2.zero, val texC: Vector2 = Vector2.zero) extends RayIntersectable
with Colored {

  val trianglesPlane = Plane.fromPoints(a, b, c)

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
      if ((facrossfb dot -trianglesPlane.normal) < 0) {
        noHit
      } else {
        val fbcrossfc = fb cross fc
        if ((fbcrossfc dot -trianglesPlane.normal) < 0) {
          noHit
        } else {
          val fccrossfa = fc cross fa
          if ((fccrossfa dot -trianglesPlane.normal) < 0) {
            noHit
          } else {
            hit(rayHit.hit.get)
          }
        }
      }
    }
  }

  override def color(vector: Vector3): Color = material.diffuse

  override def diffuseAmbientSpecularNormal(hit: Vector3): (Color, Color, Color, Vector3) = {

    val (hitPointNormal, texUv) = barycentricInterpolation(this.a, this.b, this.c, this.na, this.nb, this.nc, hit, this.texA, this.texB, this.texC)

    val (objDiff, objAmbient) = {
      material.texture match {
        case None => (material.diffuse, material.ambient)
        case Some(img) => {
          val b = try {
            img.getRGB((texUv.x * (img.getWidth - 1)).toInt, (texUv.y * (img.getHeight - 1)).toInt)
          } catch {
            case x: ArrayIndexOutOfBoundsException => {
              throw x
            }
          }
          val ambient = material.ambient
          (Color.fromInt(b),
            Color.fromInt(b) * ambient)
        }
      }
    }

    (objDiff, objAmbient, material.specular, hitPointNormal)
  }


  //http://answers.unity3d.com/questions/383804/calculate-uv-coordinates-of-3d-point-on-plane-of-m.html
  def barycentricInterpolation(p1: Vector3, p2: Vector3, p3: Vector3, uv1: Vector3, uv2: Vector3, uv3: Vector3, f: Vector3, texUv1: Vector2, texUv2: Vector2, texUv3: Vector2): (Vector3, Vector2) = {

    val f1 = p1 - f
    val f2 = p2 - f
    val f3 = p3 - f
    // calculate the areas and factors (order of parameters doesn't matter):
    val a = ((p1 - p2) cross (p1 - p3)).magnitude // main triangle area a
    val a1 = (f2 cross f3).magnitude / a;
    // p1's triangle area / a
    val a2 = (f3 cross f1).magnitude / a;
    // p2's triangle area / a
    val a3 = (f1 cross f2).magnitude / a;
    // p3's triangle area / a
    // find the uv corresponding to point f (uv1/uv2/uv3 are associated to p1/p2/p3):
    val uv = uv1 * a1 + uv2 * a2 + uv3 * a3
    val texUv = texUv1 * a1 + texUv2 * a2 + texUv3 * a3



    (uv, Vector2(FloatUtils.clamp(texUv.x), FloatUtils.clamp(texUv.y)))
  }

}

object ColoredTriangle {

  def apply(a: Vector3, b: Vector3, c: Vector3, material: Material, na: Vector3 = Vector3.up, nb: Vector3 = Vector3.up, nc: Vector3 = Vector3.up, texA: Vector2 = Vector2.zero, texB: Vector2 = Vector2.zero, texC: Vector2 = Vector2.zero) = new ColoredTriangle(a, b, c, material, na, nb, nc, texA, texB, texC)

  val PLUS_ZERO = 0.00001f
  val MINUS_ZERO = -0.0001f

}
