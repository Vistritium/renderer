package struct

class Ray private(val origin: Vector3, val direction: Vector3, val length: Float) {


/*  def intersectsSphere(sphere: Sphere): Boolean = {
    val x = sphere.center.x - this.origin.x
    val y = sphere.center.y - this.origin.y
    val z = sphere.center.z - this.origin.z
    val pyth = (x * x) + (y * y) + (z * z)
    val rr = sphere.radius * sphere.radius
    if (pyth <= rr) {
      true
    } else {
      val dot = (x * this.direction.x) + (y * this.direction.y) + (z * this.direction.z)
      if (dot < 0.0) {
        false
      } else {
        val temp = pyth - (dot * dot)
        temp <= rr
      }
    }
  }*/

  def intersectsSphere(sphere: Sphere): Option[Vector3] = {
    val eye = origin
    val dst = sphere.center - eye
    var t = dst dot direction

    if( t <= 0){
        None
    } else {
        val d = t * t - dst.dot(dst) + sphere.radius * sphere.radius
      if(d < 0){
        None
      } else {

        t = t - Math.sqrt(d).toFloat

        val x = eye.x + t * direction.x
        val y = eye.y + t * direction.y
        val z = eye.z + t * direction.z

        Some(Vector3(x, y, z))

      }
    }



    }

  def intersectsPlane(plane: Plane): Option[Vector3] = {
    val t: Float = -(plane.normal.x* origin.x + plane.normal.y*origin.y + plane.normal.z*origin.z + plane.w) /
      (plane.normal.x * direction.x + plane.normal.y * direction.y + plane.normal.z * direction.z )

    val hit = t < 0
    if(hit)
    {
      None
    } else {
      Some(origin + direction.normalised * t)
    }

  }
}

object Ray {
  def apply(origin: Vector3, destination: Vector3, length: Float = 0.0f) = {
    new Ray(origin, destination, if(length != 0.0f) length else Float.MaxValue)
  }
}
