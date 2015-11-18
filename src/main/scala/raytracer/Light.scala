package raytracer

import struct.{Vector3, Color}

trait Light {

  def getDiffuseAmbientSpecular(rayHit: RayHit, hitNormal: Vector3)(implicit camera: Camera): (Color, Color, Color)

  def isInShadow(rayHit: RayHit)(implicit camera: Camera, world: World): Boolean

}

object Light {

  def computeNDotL(vertex: Vector3, normal: Vector3, lightPosition: Vector3): Float = {
    val lightDirection = lightPosition - vertex

    val normalized = normal.normalised
    val lightDirNormalized = lightDirection.normalised

    Math.max(0, normalized dot lightDirNormalized)
  }

}
