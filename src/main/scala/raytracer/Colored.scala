package raytracer

import struct.Color

trait Colored {
    def color: Color
}

object Colored {

  def apply(color: Color): Colored = new Colored {
    override val color: Color = color
  }

}

