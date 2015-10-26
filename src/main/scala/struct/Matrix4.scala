package struct


case class Matrix4(_1: (Float, Float, Float, Float),
                   _2: (Float, Float, Float, Float),
                   _3: (Float, Float, Float, Float),
                   _4: (Float, Float, Float, Float)) extends Immutable {
  lazy val transpose = Matrix4(
    (_1._1, _2._1, _3._1, _4._1),
    (_1._2, _2._2, _3._2, _4._2),
    (_1._3, _2._3, _3._3, _4._3),
    (_1._4, _2._4, _3._4, _4._4))

  def apply(i: Int) = {
    if( i == 0){
      _1._1
    } else if( i == 1 ){
      _1._2
    }
    else if( i == 2 ){
      _1._3
    }
    else if( i == 3 ){
      _1._4
    }
    else if( i == 4 ){
      _2._1
    }
    else if( i == 5 ){
      _2._2
    }
    else if( i == 6 ){
      _2._3
    }
    else if( i == 7 ){
      _2._4
    }
    else if( i == 8 ){
      _3._1
    }
    else if( i == 9 ){
      _3._2
    }
    else if( i == 10 ){
      _3._3
    }
    else if( i == 11 ){
      _3._4
    }
    else if( i == 12 ){
      _4._1
    }
    else if( i == 13 ){
      _4._2
    }
    else if( i == 14 ){
      _4._3
    }
    else if( i == 15 ){
      _4._4
    } else {
      throw new IllegalArgumentException(s"Illegal number. Must be between 0 and 15, actual:$i")
    }
  }

  def *(f: Float) = Matrix4(
    (f * _1._1, f * _1._2, f * _1._3, f * _1._4),
    (f * _2._1, f * _2._2, f * _2._3, f * _2._4),
    (f * _3._1, f * _3._2, f * _3._3, f * _3._4),
    (f * _4._1, f * _4._2, f * _4._3, f * _4._4))

  def *(v: Vector3) = Vector3(
    v.x * _1._1 + v.y * _2._1 + v.z * _3._1 + _4._1,
    v.x * _1._2 + v.y * _2._2 + v.z * _3._2 + _4._2,
    v.x * _1._3 + v.y * _2._3 + v.z * _3._3 + _4._3)

  // OMG
  def *(m: Matrix4) = Matrix4(
    (_1._1 * m._1._1 + _1._2 * m._2._1 + _1._3 * m._3._1 + _1._4 * m._4._1,
      _1._1 * m._1._2 + _1._2 * m._2._2 + _1._3 * m._3._2 + _1._4 * m._4._2,
      _1._1 * m._1._3 + _1._2 * m._2._3 + _1._3 * m._3._3 + _1._4 * m._4._3,
      _1._1 * m._1._4 + _1._2 * m._2._4 + _1._3 * m._3._4 + _1._4 * m._4._4),
    (_2._1 * m._1._1 + _2._2 * m._2._1 + _2._3 * m._3._1 + _2._4 * m._4._1,
      _2._1 * m._1._2 + _2._2 * m._2._2 + _2._3 * m._3._2 + _2._4 * m._4._2,
      _2._1 * m._1._3 + _2._2 * m._2._3 + _2._3 * m._3._3 + _2._4 * m._4._3,
      _2._1 * m._1._4 + _2._2 * m._2._4 + _2._3 * m._3._4 + _2._4 * m._4._4),
    (_3._1 * m._1._1 + _3._2 * m._2._1 + _3._3 * m._3._1 + _3._4 * m._4._1,
      _3._1 * m._1._2 + _3._2 * m._2._2 + _3._3 * m._3._2 + _3._4 * m._4._2,
      _3._1 * m._1._3 + _3._2 * m._2._3 + _3._3 * m._3._3 + _3._4 * m._4._3,
      _3._1 * m._1._4 + _3._2 * m._2._4 + _3._3 * m._3._4 + _3._4 * m._4._4),
    (_4._1 * m._1._1 + _4._2 * m._2._1 + _4._3 * m._3._1 + _4._4 * m._4._1,
      _4._1 * m._1._2 + _4._2 * m._2._2 + _4._3 * m._3._2 + _4._4 * m._4._2,
      _4._1 * m._1._3 + _4._2 * m._2._3 + _4._3 * m._3._3 + _4._4 * m._4._3,
      _4._1 * m._1._4 + _4._2 * m._2._4 + _4._3 * m._3._4 + _4._4 * m._4._4))


  override def toString: String =
    s"${_1._1} ${_1._2} ${_1._3} ${_1._4}\n" +
      s"${_2._1} ${_2._2} ${_2._3} ${_2._4}\n" +
      s"${_3._1} ${_3._2} ${_3._3} ${_3._4}\n" +
      s"${_4._1} ${_4._2} ${_4._3} ${_4._4}\n"
}

object Matrix4 {
  val identity = Matrix4(
    (1, 0, 0, 0),
    (0, 1, 0, 0),
    (0, 0, 1, 0),
    (0, 0, 0, 1))

  def translation(v: Vector3) = Matrix4(
    (1, 0, 0, 0),
    (0, 1, 0, 0),
    (0, 0, 1, 0),
    (v.x, v.y, v.z, 1))

  def scaling(v: Vector3) = Matrix4(
    (v.x, 0, 0, 0),
    (0, v.y, 0, 0),
    (0, 0, v.z, 0),
    (0, 0, 0, 1))

  def scaling(f: Float) = Matrix4(
    (f, 0, 0, 0),
    (0, f, 0, 0),
    (0, 0, f, 0),
    (0, 0, 0, 1))

  import struct.FastMath.{cos, sin}

  def rotaX(a: Float) = {
    val s = sin(a)
    val c = cos(a)
    Matrix4(
      (1, 0, 0, 0),
      (0, c, s, 0),
      (0, -s, c, 0),
      (0, 0, 0, 1))
  }

  def rotaY(a: Float) = {
    val s = sin(a)
    val c = cos(a)
    Matrix4(
      (c, 0, -s, 0),
      (0, 1, 0, 0),
      (s, 0, c, 0),
      (0, 0, 0, 1))
  }

  def rotaZ(a: Float) = {
    val s = sin(a)
    val c = cos(a)
    Matrix4(
      (c, s, 0, 0),
      (-s, c, 0, 0),
      (0, 0, 1, 0),
      (0, 0, 0, 1))
  }

  implicit def asArray(m: Matrix4) = Array(
    m._1._1, m._1._2, m._1._3, m._1._4,
    m._2._1, m._2._2, m._2._3, m._2._4,
    m._3._1, m._3._2, m._3._3, m._3._4,
    m._4._1, m._4._2, m._4._3, m._4._4)

  implicit def asFloatBuffer(m: Matrix4) = {
    import java.nio.FloatBuffer

    val buf = FloatBuffer.allocate(16)
    buf.put(m)
    buf
  }

  // @formatter:off
/*  def rotationYawPitchRoll(yaw: Float, pitch: Float, roll: Float) = {
    val a = yaw
    val b = pitch
    val y = roll

    //rotaX(yaw) * rotaY(pitch) * rotaZ(roll)

    Matrix4(
      (cos(a)*cos(b)                          , sin(a)*cos(b)                                         , -sin(b)                               , 0),
      (cos(a)*sin(b)*sin(y) - sin(a)*cos(y)   , sin(a)*sin(b)*sin(y) + cos(a)*cos(y)                  , cos(b)*sin(y)                         , 0),
      (cos(a)*sin(b)*cos(y) + sin(a) * sin(y) , sin(a)*sin(b)*cos(y) - cos(a)*sin(y)                  , cos(b)*cos(y)                         , 0),
      (0                                      ,0                                                      ,0                                      , 1)
    )

  }*/
// @formatter:on

  //can be optimized: http://www.3dgep.com/understanding-the-view-matrix/
  /*  def lookAtRH(from: Vector3, target: Vector3, up: Vector3): Matrix4 = {
      val zaxis = (from - target).normalised
      val xaxis = (up cross zaxis).normalised
      val yaxis = zaxis cross xaxis;

      // @formatter:off
      val orientation = Matrix4(
        ( xaxis.x, yaxis.x, zaxis.x, 0 ),
        ( xaxis.y, yaxis.y, zaxis.y, 0 ),
        ( xaxis.z, yaxis.z, zaxis.z, 0 ),
        (   0,       0,       0,     1 ))


      val translation = Matrix4(
        (   1,      0,      0,   0 ),
        (   0,      1,      0,   0 ),
        (   0,      0,      1,   0 ),
        (-from.x, -from.y, -from.z, 1 ))

      orientation * translation
    }*/
// @formatter:on
  def lookAtLHT(eye: Vector3, target: Vector3, up: Vector3): Matrix4 = {

    val zaxis = (target - eye).normalised
    val xaxis = {
      val xaxis = (up cross zaxis)
      if (xaxis.magnitude == 0) {
        Vector3(1, xaxis.y, xaxis.z)
      } else {
        xaxis
      }
    }
    val yaxis = (zaxis cross xaxis).normalised;

    // Eye angles
    val ex = -(xaxis dot eye)
    val ey = -(yaxis dot eye)
    val ez = -(zaxis dot eye)

    Matrix4(
      (xaxis.x, yaxis.x, zaxis.x, 0f),
      (xaxis.y, yaxis.y, zaxis.y, 0f),
      (xaxis.z, yaxis.z, zaxis.z, 0f),
      (ex, ey, ez, 1)
    )
  }

  def perspectiveFovLH(fov: Float, aspect: Float, znear: Float, zfar: Float): Matrix4 = {
    val tan = 1.0f / Math.tan(fov * 0.5).toFloat

    val result = Array.ofDim[Float](16)

    val v_fixed = true

    if (v_fixed) {
      result(0) = tan / aspect
    }
    else {
      result(0) = tan
    }

    result(1) = 0.0f
    result(2) = 0.0f
    result(3) = 0.0f

    if (v_fixed) {
      result(5) = tan
    }
    else {
      result(5) = tan * aspect
    }

    result(4) = 0.0f
    result(6) = 0.0f
    result(7) = 0.0f
    result(8) = 0.0f
    result(9) = 0.0f
    result(10) = -zfar / (znear - zfar)
    result(11) = 1.0f
    result(12) = 0.0f
    result(13) = 0.0f
    result(15) = 0.0f
    result(14) = (znear * zfar) / (znear - zfar)
    // @formatter:off
    Matrix4(
      (result(0),   result(1),  result(2),  result(3)),
      (result(4),   result(5),  result(6),  result(7)),
      (result(8),   result(9),  result(10),  result(11)),
      (result(12),  result(13), result(14), result(15)))
    // @formatter:on
  }

  def orthoMatrix(width: Float, height: Float, near: Float, far: Float) = {
    Matrix4(
      (2.0f / width, 0, 0, 0),
      (0, 2.0f / height, 0, 0),
      (0, 0, 1 / (far - near), -near / (far - near)),
      (0, 0, 0, 1)
    )
  }

  def rotationYawPitchRoll(yaw: Float, pitch: Float, roll: Float) = {
    Quaternion.rotationYawPitchRoll(yaw, pitch, roll).getMatrix

  }

  def mainroll(args: Array[String]) {
    val yaw = 0
    val pitch = 0
    val roll = 0

    val mine = rotationYawPitchRoll(yaw, pitch, roll)
    val computed = rotaZ(yaw) * rotaY(pitch) * rotaX(roll)

    println(s"mine: \n$mine\ncomputed:\n$computed")


  }
}
