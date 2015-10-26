package raytracer

import struct.{Vector3, Ray}

trait RayIntersectable {

  def intersects(ray: Ray): RayHit

}
