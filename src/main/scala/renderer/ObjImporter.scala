package renderer

import java.nio.file.Path
import java.util

import com.owens.oobjloader.builder.{VertexNormal, VertexGeometric, Face, Build}
import com.owens.oobjloader.parser.Parse
import struct.{Vertex, Vector3, Mesh}

import scala.collection.mutable.ArrayBuffer

object ObjImporter {


  def importObj(path: Path): Mesh = {

    val builder = new Build();
    val obj: Parse = new Parse(builder, path.toAbsolutePath.toString)

    val faces: util.ArrayList[Face] = builder.faces

    val vertices: util.ArrayList[VertexGeometric] = builder.verticesG
    val normals: util.ArrayList[VertexNormal] = builder.verticesN


    new Mesh("", new Vector3(0, 0, 0), new Vector3(0, 0, 0), verticesMapper(vertices, normals), facesMapper(faces, vertices, builder.faceToIndices))

  }

  private def verticesMapper(vertices: util.ArrayList[VertexGeometric], normals: util.ArrayList[VertexNormal]): ArrayBuffer[Vertex] = {
    require(vertices.size() == normals.size(), s"verticles and normals size must be identical. Actual: ${vertices.size()} ${normals.size()}")
    var i = 0

    val result = new ArrayBuffer[Vertex](vertices.size())
    while (i < vertices.size()) {
      val vertice = vertices.get(i)

      val normal = normals.get(i)
      val ourVertice = new Vector3(vertice.x, vertice.y, vertice.z)
      val ourNormal = new Vector3(normal.x, normal.y, normal.z)

      val vertex = new Vertex(ourVertice, ourVertice, ourNormal)

      result.insert(i, vertex)

      i = i + 1;
    }

    result
  }

  private def facesMapper(faces: util.ArrayList[Face], vertices: util.ArrayList[VertexGeometric], faceToIndices: util.Map[Face, Array[Int]]): ArrayBuffer[struct.Face] = {
    import scala.collection.JavaConversions._

    faces.map(face => {
      require(face.vertices.size() == 3, s"Invalid number of vertices, must be 3, actual: ${face.vertices.size()}")
      val indices = faceToIndices.get(face)
      val a = face.vertices.get(0).index
      val b = face.vertices.get(1).index
      val c = face.vertices.get(2).index
      new struct.Face(a, b, c)

    }).to[ArrayBuffer]
  }


}
