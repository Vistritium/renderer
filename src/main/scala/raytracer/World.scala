package raytracer

import struct.{Ray, Sphere}

import scala.collection.mutable.ArrayBuffer

class World {

  var objects = ArrayBuffer[RayIntersectable with Colored]()


  def intersects(ray: Ray): RayHit = {
    objects.map(_.intersects(ray)).reduce(compareRayHits)
  }


  private def compareRayHits(left: RayHit, right: RayHit): RayHit = {
    if (left.hit.isDefined && right.hit.isDefined) {
      if (left.distance.get < right.distance.get) {
        left
      } else {
        right
      }
    } else if (left.hit.isDefined) {
      left
    } else {
      right
    }
  }
}

object World {

  def apply(spheres: RayIntersectable with Colored *): World = {
    val world = new World()
    world.objects ++= spheres

    world
  }

}
