package struct

import scala.collection.mutable.ArrayBuffer

class Mesh(val name: String = "", var position: Vector3, var rotation: Vector3, var vertices: ArrayBuffer[Vertex], var faces: ArrayBuffer[Face]) {

}

class Face(val a: Int, val b: Int, val c: Int)
class Vertex(var coordinates: Vector3, var worldCoordinates: Vector3, var normal: Vector3)
class ScanLineData(var currentY: Int, var ndotla: Float, var ndotlb: Float, var ndotlc: Float, var ndotld: Float)

object  Mesh {

  def apply(name: String = "", position: Vector3, rotation: Vector3, vertices: ArrayBuffer[Vector3], faces: ArrayBuffer[Face]) = new Mesh(name, position, rotation, vertices.map(x => new Vertex(x, x, x)), faces)

}