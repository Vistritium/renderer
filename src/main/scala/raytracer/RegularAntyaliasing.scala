package raytracer

import struct.{FloatUtils, Color, Vector2}

class RegularAntyaliasing(raysPerPixelSquared: Int) extends Antyaliasing {

  override val getCoords: List[Vector2] = {

    val xRays = raysPerPixelSquared / 2
    val yRays = raysPerPixelSquared / 2

    val result = (1 until xRays + 1).flatMap(i => {
      (1 until yRays + 1).map(j => {
        val x = FloatUtils.lerp(-1f, 1f, i.toFloat / (xRays.toFloat + 1))
        val y = FloatUtils.lerp(-1f, 1f, j.toFloat / (yRays.toFloat + 1))
        new Vector2(x, y)
      })
    }).toList
    result

  }

  override def computeColor(x: List[(Vector2, Color)]): Color = {
    val components = x.map(_._2).foldLeft((0, 0, 0))((sum, next) => (sum._1 + next.red, sum._2 + next.green, sum._3 + next.blue))
    val res = Color(components._1 / x.length, components._2 / x.length, components._3 / x.length)
    res
  }
}
