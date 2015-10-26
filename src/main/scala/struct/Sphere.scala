package struct

import raytracer.{RayHit, RayIntersectable}

class Sphere protected(val min: Vector3, val max: Vector3) extends RayIntersectable {
  val center = Vector3.lerp(min, max, 0.5f)
  val distance = Vector3.distance(min, max)
  val radius = distance / 2.0f

  def this(center: Vector3, length: Float) = {
    this(center - Vector3.up * length, center + Vector3.up * length)
  }

  def project(projection: Matrix4): Sphere = {

    val projectedMin = Vector3.transformCoordinates(min, projection)
    val projectedMax = Vector3.transformCoordinates(max, projection)

    new Sphere(projectedMin, projectedMax)
  }

  override def intersects(ray: Ray): RayHit = ray.intersectsSphere(this)

}


