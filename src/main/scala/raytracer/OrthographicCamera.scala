package raytracer

import struct.{Color, FloatUtils, Ray, Vector3}

class OrthographicCamera(override var position: Vector3, override var target: Vector3, override var width: Int,
                         override var height: Int, antyaliasing: Antyaliasing = new RegularAntyaliasing(2), override val light: Option[Light] = None) extends Camera {
  val direction = (target - position).normalised

  override protected def draw(world: World): Unit = {

    val cameraW = direction.normalised
    val cameraU = (Vector3.up cross cameraW).normalised
    val cameraV = -(cameraW cross cameraU).normalised

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

        hit.hit.map(x => (coord, getColorForRayhit(hit, world)))
      })

      if (colors.nonEmpty) {
        Some(antyaliasing.computeColor(colors))
      } else {
        None
      }

/*      val intersects = world.intersects(ray)
      intersects.hit.map(x => intersects.hitObj.asInstanceOf[Colored].color)*/

    }

    (0 until width).par.foreach(i => {
      (0 until height).par.foreach(j => {
        val iFactor = FloatUtils.lerp(-1f, 1f, i.toFloat / width)
        val jFactor = FloatUtils.lerp(-1f, 1f, j.toFloat / height)

        val point = position + cameraUHalfPixels * iFactor + cameraVHalfPixels * jFactor

        val ray = Ray(point, cameraW)

        val color = middleRay(ray)

        if (color.isDefined) {
          putPixel(i, j, color.get)
        }
      })
    })

/*    var i = 0
    while (i < width) {
      var j = 0
      while (j < height) {



        j = j + 1
      }
      i = i + 1
    }*/
  }


}
