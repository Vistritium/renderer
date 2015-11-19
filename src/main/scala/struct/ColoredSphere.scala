package struct

import raytracer.{MaterialType, Colored, Material}

class ColoredSphere(override val min: Vector3, override val max: Vector3, val material: Material) extends Sphere(min, max) with Colored {

  val (xRes, yRes) = material.texture match {
    case None => (0, 0)
    case Some(x) => (x.getWidth, x.getHeight)
  }



  def this(center: Vector3, length: Float, material: Material) = {
    this(center - Vector3.up * length, center + Vector3.up * length, material)
  }

  override def project(projection: Matrix4): ColoredSphere = {

    val projectedMin = Vector3.transformCoordinates(min, projection)
    val projectedMax = Vector3.transformCoordinates(max, projection)

    new ColoredSphere(projectedMin, projectedMax, material)
  }

  override def color(vector: Vector3): Color = material.diffuse

  override def diffuseAmbientSpecularNormal(hit: Vector3): (Color, Color, Color, Vector3) = {
    val hitNormal = (hit - center).normalised
    material.texture match {
      case None => (material.diffuse, material.ambient, material.specular, hitNormal)
      case Some(x) => {

        val localHit = hit - center

        val theta = Math.acos(localHit.y).toFloat
        var phi = Math.atan2(localHit.x, localHit.z).toFloat + Math.PI + 2.0f

        if(phi < 0.0f){
          phi = phi + Math.PI.toFloat * 2.0f
        } else if(phi > Math.PI.toFloat * 2.0f){
          phi = phi - Math.PI.toFloat * 2.0f
        }

        val u = phi * FloatUtils.invTWO_PI
        val v = 1.0f - theta * FloatUtils.invPI

        val column = (xRes - 1) - ((xRes - 1) * u).toInt
        val row = (yRes - 1) - ((yRes - 1) * v).toInt

        val intColor = x.getRGB(column, row)
        val texColor = Color.fromInt(intColor)

        (texColor, texColor * material.ambient, material.specular, hitNormal)
      }
    }

  }

  override def getMaterialType: MaterialType = material.materialType

  override def getNormal(hit: Vector3): Vector3 = (hit - center).normalised
}

