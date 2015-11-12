package raytracer

import common.ProgressCounter
import struct.{Color, FloatUtils, Ray, Vector3}

class PerspectiveCamera(override var position: Vector3, override var target: Vector3, centerDistance: Float, override var width: Int,
                        override var height: Int, antyaliasing: Antyaliasing = new RegularAntyaliasing(4), override val light: Option[Light] = None) extends Camera {
  val direction = (target - position).normalised

  override protected def draw(world: World): Unit = {

    val e = position + direction * centerDistance

    val cameraW = direction.normalised
    val cameraU = (Vector3.up cross cameraW).normalised
    val cameraV = -(cameraW cross cameraU).normalised

    val cameraUSinglePixel = cameraU * pixelWidth
    val cameraVSinglePixel = cameraV * pixelHeight

    val cameraUHalfPixels = cameraUSinglePixel * 0.5f * width
    val cameraVHalfPixels = cameraVSinglePixel * 0.5f * height

    def middleRay(middlePoint: Vector3): Option[Color] = {

      val coords = antyaliasing.getCoords
      val colors = coords.flatMap(coord => {
        val currRayOrigin = middlePoint + cameraUSinglePixel * 0.5f * coord.x + cameraVSinglePixel * 0.5f * coord.y
        val currRay = Ray(position, (currRayOrigin - position).normalised)
        val hit = world.intersects(currRay)
        hit.hit.map(x => (coord, getColorForRayhit(hit, world)))
      })

      if (colors.nonEmpty) {
        Some(antyaliasing.computeColor(colors))
      } else {
        None
      }

    }

    val progress = new ProgressCounter(width * height)
    (0 until width).par.foreach(i => {
      (0 until height).foreach(j => {

        if(j == 567 && i == 544){
          System.nanoTime()
        }

        val iFactor = FloatUtils.lerp(-1f, 1f, i.toFloat / width)
        val jFactor = FloatUtils.lerp(-1f, 1f, j.toFloat / height)

        val point = e + cameraUHalfPixels * iFactor + cameraVHalfPixels * jFactor

        val maybeColor = middleRay(point)

        if (maybeColor.isDefined) {
          putPixel(i, j, maybeColor.get)
        }
        progress.bumpAndMaybePrintProgress()
      })
    })
  }
}
