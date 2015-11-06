package raytracer

import java.io.{InputStream, InputStreamReader}
import javax.imageio.ImageIO

import org.json.simple.{JSONArray, JSONObject, JSONValue}
import struct._

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object BabylonImporter {

  def importModel(inputStream: InputStream, texturesPath: String): ArrayBuffer[Mesh] = {

    val random = new Random(578295708257210858l)

    val reader = new InputStreamReader(inputStream);

    val json = JSONValue.parse(reader).asInstanceOf[JSONObject]


    var materialIndex = 0
    val materialCount = json.get("materials").asInstanceOf[JSONArray].size()
    val materials = mutable.Map[String, Material]()

    while(materialIndex < materialCount){

      val currMaterial = json.get("materials").asInstanceOf[JSONArray].get(materialIndex).asInstanceOf[JSONObject]

      val matId = currMaterial.get("id").toString

      val ambient = {
        val ambient = currMaterial.get("ambient").asInstanceOf[JSONArray]

        val red = ambient.get(0).toString.toFloat
        val green = ambient.get(1).toString.toFloat
        val blue = ambient.get(2).toString.toFloat

        Color(red, green, blue)
      }

      val diffuse = {
        val diffuse = currMaterial.get("diffuse").asInstanceOf[JSONArray]

        val red = diffuse.get(0).toString.toFloat
        val green = diffuse.get(1).toString.toFloat
        val blue = diffuse.get(2).toString.toFloat

        Color(red, green, blue)
      }

      val specular = {
        val specular = currMaterial.get("specular").asInstanceOf[JSONArray]

        val red = specular.get(0).toString.toFloat
        val green = specular.get(1).toString.toFloat
        val blue = specular.get(2).toString.toFloat

        Color(red, green, blue)
      }

      val diffuseTexture = {
        val diffuseTexture = currMaterial.get("diffuseTexture").asInstanceOf[JSONObject]

        val name = diffuseTexture.get("name").toString

        val textureImageRes = getClass.getClassLoader.getResource(s"$texturesPath/$name")
        val texture = ImageIO.read(textureImageRes)

        texture
      }

      val material = new Material(ambient, diffuse, specular, Some(diffuseTexture))

      materials += (matId -> material)

      materialIndex = materialIndex + 1
    }


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

      val worldProjection = Matrix4.rotationYawPitchRoll(xRot, yRot, zRot) * Matrix4.translation(pos)

      val verticles = {
        var i = 0
        val res = new Array[Vertex](verticesCount)
        while (i < verticesCount) {

          val x = verticesArray(i * verticesStep)
          val y = verticesArray(i * verticesStep + 1)
          val z = verticesArray(i * verticesStep + 2)
          val coords = new Vector3(x, y, z)
          val projectedCoords = Vector3.transformCoordinates(coords, worldProjection)

          val nx = verticesArray(i * verticesStep + 3)
          val ny = verticesArray(i * verticesStep + 4)
          val nz = verticesArray(i * verticesStep + 5)
          val normal = new Vector3(nx, ny, nz)
          val projectedNormal = Vector3.transformCoordinates(normal, worldProjection)

          val uv = if(uvCount > 0){
            val u = verticesArray(i * verticesStep + 6)
            val v = verticesArray(i * verticesStep + 7)
            Vector2(u, v)
          } else {
            Vector2.zero
          }

          res.update(i, new Vertex(projectedCoords, projectedCoords, projectedNormal, uv))

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

      val triangles = faces.map(face => {

        val a = verticles(face.a)
        val b = verticles(face.b)
        val c = verticles(face.c)


        //val material = new Material(Color(10, 10, 10), Color(100, 100, 100), Color(255, 255, 255), None)
        val materialId = currMesh.get("materialId").toString
        val material = materials(materialId)
        //ColoredTriangle(a.coordinates, b.coordinates, c.coordinates, Color(random.nextInt(200)+50, 0, random.nextInt(200)+50), a.normal, b.normal, c.normal)
        ColoredTriangle(a.coordinates, b.coordinates, c.coordinates, material, a.normal, b.normal, c.normal, a.uv, b.uv, c.uv)
      })





      meshes.update(meshIndex, new Mesh(triangles.to[ArrayBuffer]))
      meshIndex = meshIndex + 1
    }

    //    ArrayBuffer[Mesh](meshes(4))
    meshes.to[ArrayBuffer]
  }

}
