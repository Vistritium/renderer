package raytracer

import struct.Color

class PointLight extends Light {

  override def getDiffuse(rayHit: RayHit)(implicit camera: Camera): Color = {

    val objColor = rayHit.hitObj.asInstanceOf[Colored].color

    val cameraPos = camera.position

    ???

  }

  override def getSpecular(rayHit: RayHit)(implicit camera: Camera): Color = ???

  override def isInShadow(rayHit: RayHit)(implicit camera: Camera, world: World): Boolean = false

}
