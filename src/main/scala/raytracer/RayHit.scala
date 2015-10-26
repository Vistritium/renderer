package raytracer

import struct.{Ray, Vector3}

case class RayHit(hit: Option[Vector3], ray: Ray, hitObj: RayIntersectable){
  val distance = hit.map(hit => Vector3.distance(hit, ray.origin))


}

