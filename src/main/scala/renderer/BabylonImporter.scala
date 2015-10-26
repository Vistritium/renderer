package renderer

import java.io.{InputStream, InputStreamReader}

import org.json.simple.{JSONArray, JSONObject, JSONValue}
import struct.{Face, Mesh, Vector3, Vertex}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object BabylonImporter {

  def importModel(inputStream: InputStream): ArrayBuffer[Mesh] = {

    val reader = new InputStreamReader(inputStream);

    val json = JSONValue.parse(reader).asInstanceOf[JSONObject]

    var meshIndex = 0

    val meshesCount = json.get("meshes").asInstanceOf[JSONArray].size()

    val meshes = new Array[Mesh](meshesCount)

    while (meshIndex < meshesCount) {

      val currMesh = json.get("meshes").asInstanceOf[JSONArray].get(meshIndex).asInstanceOf[JSONObject]
      val verticesArray = currMesh.get("vertices").asInstanceOf[JSONArray].iterator().toArray[Any].map(_.toString.toFloat)
      val indicesArray = currMesh.get("indices").asInstanceOf[JSONArray].iterator().toArray[Any].map(_.toString.toInt)
      val uvCount = currMesh.get("uvCount").toString.toFloat

      val verticesStep = uvCount match {
        case 0 => 6
        case 1 => 8
        case 2 => 10
        case _ => 1
      }

      val verticesCount = verticesArray.length / verticesStep
      val facesCount = indicesArray.length / 3

      val verticles = {
        var i = 0
        val res = new Array[Vertex](verticesCount)
        while (i < verticesCount) {

          val x = verticesArray(i * verticesStep)
          val y = verticesArray(i * verticesStep + 1)
          val z = verticesArray(i * verticesStep + 2)

          val nx = verticesArray(i * verticesStep + 3)
          val ny = verticesArray(i * verticesStep + 4)
          val nz = verticesArray(i * verticesStep + 5)

          val coords = new Vector3(x, y, z)
          val normal = new Vector3(nx, ny, nz)

          res.update(i, new Vertex(coords, coords, normal))

          i = i + 1
        }
        res
      }

      val faces = {
        var i = 0
        val res = new Array[Face](facesCount)
        while (i < facesCount) {

          val a = indicesArray(i * 3)
          val b = indicesArray(i * 3 + 1)
          val c = indicesArray(i * 3 + 2)


          res.update(i, new Face(a, b, c))

          i = i + 1
        }
        res
      }

      val posJson = currMesh.get("position").asInstanceOf[JSONArray]
      val xPos = posJson.get(0).toString.toFloat
      val yPos = posJson.get(1).toString.toFloat
      val zPos = posJson.get(2).toString.toFloat
      val pos = new Vector3(xPos, yPos, zPos)

      val rotJson = currMesh.get("rotation").asInstanceOf[JSONArray]
      val xRot = rotJson.get(0).toString.toFloat
      val yRot = rotJson.get(1).toString.toFloat
      val zRot = rotJson.get(2).toString.toFloat
      val rot = new Vector3(xRot, yRot, zRot)


      meshes.update(meshIndex, new Mesh(meshIndex.toString, pos, rot, verticles.to[ArrayBuffer], faces.to[ArrayBuffer]))
      meshIndex = meshIndex + 1
    }


    meshes.to[ArrayBuffer]
  }

}
