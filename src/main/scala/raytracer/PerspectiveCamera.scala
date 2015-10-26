package raytracer

import struct.{Ray, FloatUtils, Vector3}

class PerspectiveCamera(position: Vector3, direction: Vector3, centerDistance: Float, override var width: Int, override var height: Int) extends Camera {

  override protected def draw(world: World): Unit = {

    val e = position + direction * centerDistance

    val cameraW = direction.normalised
    val cameraU = (Vector3.up cross cameraW).normalised
    val cameraV = (cameraW cross cameraU).normalised

    var i = 0
    while (i < width) {
      var j = 0
      while (j < height) {

        val iFactor = FloatUtils.lerp(-1f, 1f, i.toFloat / width)
        val jFactor = FloatUtils.lerp(-1f, 1f, j.toFloat / height)

        val point = e + cameraU * iFactor + cameraV * jFactor

        val ray = Ray(position, (point - position).normalised)
        val hit = world.intersects(ray)

        if (hit.hit.isDefined) {
          putPixel(i, j, hit.hitObj.asInstanceOf[Colored].color)
        }

        j = j + 1
      }
      i = i + 1
    }
  }


}
