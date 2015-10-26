package struct

class Vector2(private var _x: Float, private var _y: Float) {
  def x = _x
  def y = _y

  def +(v: Vector2) = {var c = Vector2(x, y); c += v; c}

  def +=(v: Vector2) = {
    this._x += v.x
    this._y += v.y
    ()
  }

  def -(v: Vector2) = {var c = Vector2(x, y); c -= v; v}

  def -=(v: Vector2) = {
    this._x -= v.x
    this._y -= v.y
    ()
  }

  def /(factor: Float) = {var c = Vector2(x, y); c /= factor; c}

  def /=(factor: Float) = {
    this *= (1 / factor);
    ()
  }

  def *(factor: Float) = {var c = Vector2(x, y); c *= factor; c}

  def *=(factor: Float) = {
    this._x *= factor
    this._y *= factor
    ()
  }

  def unary_- : Vector2 = Vector2(-x, -y)

  def magnitude = (math.sqrt (x * x + y * y).toDouble).toFloat

  def normalised = this / magnitude

  def dot(v: Vector2) = x * v.x + y * v.y

  def project(v: Vector2) = {
    val axis = v.normalised
    axis * (this dot axis)
  }


  override def toString = s"Vector2($x, $y)"
}

object Vector2
{

  def zero = new Vector2(0, 0)

  def unitX = new Vector2(1, 0)

  def unitY = new Vector2(0, 1)

  implicit def Tuple2FloatToVector2(v: (Float, Float)): Vector2 = {
    new Vector2(v._1, v._2)
  }

  def apply(): Vector2 = {
    new Vector2(0, 0)
  }

  def apply(x: Float, y: Float): Vector2 = {
    new Vector2(x, y)
  }

}