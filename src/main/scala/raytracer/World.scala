package raytracer

import struct.{Color, Ray, Vector3}

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

  def fromObjects(obj: RayIntersectable with Colored*) = {
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

  def makeBox(center: Vector3, length: Float): List[RayIntersectable with Colored] = {

    val upVector = center + new Vector3(0, length, 0)
    val forwardVector = new Vector3(0, 0, length)
    val rightVector = new Vector3(length, 0, 0)

    val roofForwardRight = center + upVector + forwardVector + rightVector
    val roofForwardLeft = center + upVector + forwardVector - rightVector
    val roofBackwardRight = center + upVector - forwardVector + rightVector
    val roofBackwardLeft = center + upVector - forwardVector - rightVector

    val floorForwardRight = center - upVector + forwardVector + rightVector
    val floorForwardLeft = center - upVector + forwardVector - rightVector
    val floorBackwardRight = center - upVector - forwardVector + rightVector
    val floorBackwardLeft = center - upVector - forwardVector - rightVector

    val power = 0.8f

    val red = Color(255, 0, 0) * power
    val blue = Color(0, 0, 255) * power
    val white = Color.white * power
    val black = Color.black

    val ambientFactor = 0f
    val specular = Color.white * 0.8f

    makeSquare(roofForwardLeft, roofForwardRight, roofBackwardRight, roofBackwardLeft, -Vector3.up, black.toMaterial(ambientFactor, specular)) :::
    makeSquare(floorForwardLeft, floorForwardRight, floorBackwardRight, floorBackwardLeft, Vector3.up, white.toMaterial(ambientFactor, specular)) :::
    makeSquare(roofForwardLeft, roofForwardRight, floorForwardRight, floorForwardLeft, -Vector3.forward ,white.toMaterial(ambientFactor, specular)) :::
    makeSquare(roofForwardLeft, floorForwardLeft, floorBackwardLeft, roofBackwardLeft, Vector3.right, red.toMaterial(ambientFactor, specular)) :::
    makeSquare(roofForwardRight, floorForwardRight, floorBackwardRight, roofBackwardRight, -Vector3.right, blue.toMaterial(ambientFactor, specular))
  }


  def makeSquare(a: Vector3, b: Vector3, c: Vector3, d: Vector3, normal: Vector3, material: Material): List[RayIntersectable with Colored] = {
    ColoredTriangle(a, b, c, material, normal, normal, normal) ::
    ColoredTriangle(c, d, a, material, normal, normal, normal) :: Nil
  }

}