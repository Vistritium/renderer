package raytracer

import struct.{Vector3, Color}

trait Colored {
    def color(vector: Vector3): Color
}

object Colored {

  def apply(globalColor: Color): Colored = new Colored {
    override def color(vector: Vector3 = Vector3.zero): Color = globalColor
  }

}

