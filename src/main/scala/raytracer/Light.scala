package raytracer

import struct.Color

trait Light {

  def getDiffuseAmbientSpecular(rayHit: RayHit)(implicit camera: Camera): (Color, Color, Color)

  def isInShadow(rayHit: RayHit)(implicit camera: Camera, world: World): Boolean

}
