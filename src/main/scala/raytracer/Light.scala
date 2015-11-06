package raytracer

import struct.Color

trait Light {

  def getColor(rayHit: RayHit)(implicit camera: Camera): Color

  def isInShadow(rayHit: RayHit)(implicit camera: Camera, world: World): Boolean

}
