package raytracer

import struct.{Color, Vector2}

trait Antyaliasing {
  
  def getCoords: List[Vector2]
  def computeColor(x: List[(Vector2, Color)]): Color


}
