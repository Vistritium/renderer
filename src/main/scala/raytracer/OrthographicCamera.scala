package raytracer

import struct.{Color, FloatUtils, Ray, Vector3}

class OrthographicCamera(position: Vector3, direction: Vector3, override var width: Int, override var height: Int, antyaliasing: Antyaliasing = new RegularAntyaliasing(2)) extends Camera {

  override protected def draw(world: World): Unit = {

    val cameraW = direction.normalised
    val cameraU = (Vector3.up cross cameraW).normalised
    val cameraV = (cameraW cross cameraU).normalised

    val pixelWidth = 2.0f / width
    val pixelHeight = 2.0f / height

    val cameraUSinglePixel = cameraU * pixelWidth
    val cameraVSinglePixel = cameraV * pixelHeight

    val cameraUHalfPixels = cameraUSinglePixel * 0.5f * width
    val cameraVHalfPixels = cameraVSinglePixel * 0.5f * height

    def middleRay(ray: Ray): Option[Color] = {

      val rayOrigin = ray.origin

      val coords = antyaliasing.getCoords
      val colors = coords.flatMap(coord => {
        val currRayOrigin = rayOrigin + cameraUSinglePixel * 0.5f * coord.x + cameraVSinglePixel * 0.5f * coord.y
        val currRay = Ray(currRayOrigin, ray.direction)
        val hit = world.intersects(currRay)
        hit.hit.map(x => (coord, hit.hitObj.asInstanceOf[Colored].color))
      })

      if (colors.nonEmpty) {
        Some(antyaliasing.computeColor(colors))
      } else {
        None
      }

/*      val intersects = world.intersects(ray)
      intersects.hit.map(x => intersects.hitObj.asInstanceOf[Colored].color)*/

    }

    var i = 0
    while (i < width) {
      var j = 0
      while (j < height) {

        val iFactor = FloatUtils.lerp(-1f, 1f, i.toFloat / width)
        val jFactor = FloatUtils.lerp(-1f, 1f, j.toFloat / height)

        val point = position + cameraUHalfPixels * iFactor + cameraVHalfPixels * jFactor

        val ray = Ray(point, cameraW)

        val color = middleRay(ray)

        if (color.isDefined) {
          putPixel(i, j, color.get)
        }

        j = j + 1
      }
      i = i + 1
    }
  }


}
