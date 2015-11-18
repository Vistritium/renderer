package struct

import raytracer.Colored

class ColoredSphere(override val min: Vector3, override val max: Vector3, val color: Color) extends Sphere(min, max) with Colored {

  def this(center: Vector3, length: Float, color: Color) = {
    this(center - Vector3.up * length, center + Vector3.up * length, color)
  }

  override def project(projection: Matrix4): ColoredSphere = {

    val projectedMin = Vector3.transformCoordinates(min, projection)
    val projectedMax = Vector3.transformCoordinates(max, projection)

    new ColoredSphere(projectedMin, projectedMax, color)
  }

  override def color(vector: Vector3): Color = color

  override def diffuseAmbientSpecularNormal(hit: Vector3): (Color, Color, Color, Vector3) = (color, color * 0.3f, Color.black, (hit - center).normalised)
}

