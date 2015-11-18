package raytracer

import struct.Ray

import scala.collection.mutable.ArrayBuffer

class World extends RayIntersectable {

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

  def apply(spheres: RayIntersectable with Colored*): World = {
    val world = new World()
    world.objects ++= spheres

    world
  }

  def fromMeshes(mesh: Mesh*): World = {
    val world = new World()
    val triangles = mesh.flatMap(_.faces)
    world.objects ++= triangles

    world
  }

  def fromObjects(obj: RayIntersectable with Colored *) = {
    val world = new World()
    world.objects ++= obj
    world
  }

  def fromObjectsAndMeshes(objs: List[RayIntersectable with Colored], meshes: List[Mesh]) = {
    val world = new World()
    val triangles = meshes.flatMap(_.faces)
    world.objects ++= triangles
    world.objects ++= objs

    world
  }

  def meshesToObjs(mesh: List[Mesh]): List[RayIntersectable with Colored] = {
    mesh.flatMap(_.faces)
  }

}