package raytracer

import struct.{Vector3, Color}

trait Colored {
    def color(vector: Vector3): Color

    def diffuseAmbientSpecularNormal(hit: Vector3): (Color, Color, Color, Vector3)

    def getMaterialType: MaterialType

    def getNormal(hit: Vector3): Vector3
}

object Colored {

  def apply(globalColor: Color): Colored = new Colored {
    override def color(vector: Vector3 = Vector3.zero): Color = globalColor

    override def diffuseAmbientSpecularNormal(hit: Vector3): (Color, Color, Color, Vector3) = (globalColor, globalColor * 0.3f, Color.black, Vector3.up)

    override def getMaterialType: MaterialType = Diffuse

    override def getNormal(hit: Vector3): Vector3 = Vector3.unitX
  }

}

