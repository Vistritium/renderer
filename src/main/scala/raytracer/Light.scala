package raytracer

import struct.Color

trait Light {

  def getDiffuse(rayHit: RayHit)(implicit camera: Camera): Color

  def getSpecular(rayHit: RayHit)(implicit camera: Camera): Color

  def isInShadow(rayHit: RayHit)(implicit camera: Camera, world: World): Boolean

}
