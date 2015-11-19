package struct

class Vector3(private var _x: Float, private var _y: Float, private var _z: Float) {
  def x = _x

  def y = _y

  def z = _z

  def +(v: Vector3) = {
    var c = Vector3(x, y, z);
    c += v;
    c
  }

  def +=(v: Vector3) = {
    this._x += v.x
    this._y += v.y
    this._z += v.z
    ()
  }

  def -(v: Vector3) = {
    var c = Vector3(x, y, z);
    c -= v;
    c
  }

  def -=(v: Vector3) = {
    this._x -= v.x
    this._y -= v.y
    this._z -= v.z
    ()
  }

  def /(factor: Float) = {
    var c = Vector3(x, y, z);
    c /= factor;
    c
  }

  def /=(factor: Float) = {
    this *= (1 / factor);
    ()
  }

  def *(factor: Float) = {
    var c = Vector3(x, y, z);
    c *= factor;
    c
  }

  def *=(factor: Float) = {
    this._x *= factor
    this._y *= factor
    this._z *= factor
    ()
  }

  def cross(v: Vector3) = {
    Vector3(
      v.z * this.y - this.z * v.y,
      this.z * v.x - v.z * this.x,
      v.y * this.x - this.y * v.x
    )
  }

  def unary_- : Vector3 = Vector3(-x, -y, -z)

  def magnitude = math.sqrt(x * x + y * y + z * z).toFloat

  def normalised = if(magnitude != 0) this / magnitude else this

  def dot(v: Vector3): Float = x * v.x + y * v.y + z * v.z

  def project(v: Vector3) = {
    val axis = v.normalised
    axis * (this dot axis)
  }

  override def toString: String = s"Vector3(x: $x, y: $y, z: $z)"
}

object Vector3 {

  def zero = new Vector3(0, 0, 0)

  def unitX = new Vector3(1, 0, 0)

  def unitY = new Vector3(0, 1, 0)

  def unitZ = new Vector3(0, 0, 1)

  def up = new Vector3(0, 1, 0)

  def forward = new Vector3(0, 0, 1)

  def right = new Vector3(1, 0, 0)
  implicit def Tuple2FloatToVector3(v: (Float, Float, Float)): Vector3 = {
    new Vector3(v._1, v._2, v._3)
  }

  def apply(): Vector3 = {
    new Vector3(0, 0, 0)
  }

  def apply(x: Float, y: Float, z: Float): Vector3 = {
    new Vector3(x, y, z)
  }

  def lerp(start: Vector3, end: Vector3, amount: Float): Vector3 = {
    val x = start.x + ((end.x - start.x) * amount)
    val y = start.y + ((end.y - start.y) * amount)
    val z = start.z + ((end.z - start.z) * amount)
    new Vector3(x, y, z)
  }

  def distance(x: Vector3, y: Vector3): Float = {
    Math.sqrt(Vector3.distanceSquared(x,y)).toFloat
  }

  def distanceSquared(x: Vector3, y: Vector3): Float = {
    val xRes = x.x - y.x
    val yRes = x.y - y.y
    val zRes = x.z - y.z
    (xRes * xRes) + (yRes * yRes) + (zRes * zRes)
  }

  def transformCoordinates(vector: Vector3, transformation: Matrix4): Vector3 = {
    val x = (vector.x * transformation(0)) + (vector.y * transformation(4)) + (vector.z * transformation(8)) + transformation(12);
    val y = (vector.x * transformation(1)) + (vector.y * transformation(5)) + (vector.z * transformation(9)) + transformation(13);
    val z = (vector.x * transformation(2)) + (vector.y * transformation(6)) + (vector.z * transformation(10)) + transformation(14);
    val w = (vector.x * transformation(3)) + (vector.y * transformation(7)) + (vector.z * transformation(11)) + transformation(15);
    return new Vector3(x / w, y / w, z / w);
  }


}
