package window

import struct.{Face, Vector3, Mesh}

import scala.collection.mutable.ArrayBuffer

object Defaults {

  val defaultMesh = Mesh("mesh", Vector3(0, 0, 0f), Vector3(0.5f, 0f, 0), ArrayBuffer[Vector3](
    Vector3(-1, 1, 1),
    Vector3(1, 1, 1),
    Vector3(-1, -1, 1),
    Vector3(1, -1, 1),
    Vector3(-1, 1, -1),
    Vector3(1, 1, -1),
    Vector3(1, -1, -1),
    Vector3(-1, -1, -1)
  ), ArrayBuffer[Face](
    new Face (0, 1, 2 ),
    new Face (1, 2, 3 ),
    new Face (1, 3, 6 ),
    new Face (1, 5, 6 ),
    new Face (0, 1, 4 ),
    new Face (1, 4, 5 ),

    new Face (2, 3, 7 ),
    new Face (3, 6, 7 ),
    new Face (0, 2, 7 ),
    new Face (0, 4, 7 ),
    new Face (4, 5, 6 ),
    new Face (4, 6, 7 )
  ))

}
