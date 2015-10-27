package raytracer

import struct.{Color, Vector2}

trait Antyaliasing {
  
  def getCoords: List[Vector2]
  def computeColor(x: List[(Vector2, Color)]): Color


}

object NoAntyaliasing extends Antyaliasing {
  override val getCoords: List[Vector2] = List(new Vector2(0, 0))

  override def computeColor(x: List[(Vector2, Color)]): Color = x.head._2
}
