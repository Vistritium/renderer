package raytracer

import struct.{FloatUtils, Color, Vector2}

class RegularAntyaliasing(raysPerPixel: Int) extends Antyaliasing {

  override val getCoords: List[Vector2] = {

    val xRays = raysPerPixel / 2
    val yRays = raysPerPixel / 2

    (0 until raysPerPixel).flatMap(i => {
      (0 until raysPerPixel).map(j => {
        val x = FloatUtils.lerp(-1f, 1f, i / xRays)
        val y = FloatUtils.lerp(-1f, 1f, j / yRays)
        new Vector2(x, y)
      })
    }).toList

  }

  override def computeColor(x: List[(Vector2, Color)]): Color = {
    val components = x.map(_._2).foldLeft((0, 0, 0))((sum, next) => (sum._1 + next.red, sum._2 + next.green, sum._3 + next.blue))
    val res = Color(components._1 / x.length, components._2 / x.length, components._3 / x.length)
    res
  }
}
