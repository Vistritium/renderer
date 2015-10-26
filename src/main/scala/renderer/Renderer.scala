package renderer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.{PixelFormat, PixelWriter}

import common.Config
import struct._


class Renderer(width: Int, height: Int, graphicsContext: GraphicsContext) {

  var debugNow = false

  var buffer: Array[Byte] = Array.fill[Byte](width * height * 3)(0x0)
  //var backBuffer: Array[Byte] = Array(currentData.toList: _*)
  var depthBuffer: Array[Float] = Array.fill(width * height)(0.0f)

  val lightPosition = Config.light

  RgbUtils.fillArrayWithColour(buffer, Color(0, 0, 0))

  def render(meshes: List[Mesh], camera: Camera): Unit = {

    clear()

    val viewMatrix = Matrix4.lookAtLHT(camera.position, camera.target, Vector3.up)
    val projectionMatrix = Matrix4.perspectiveFovLH(0.78f, this.width.toFloat / this.height.toFloat, 0.01f, 1.0f)

    meshes.foreach(mesh => {

      val worldMatrix = Matrix4.rotationYawPitchRoll(mesh.rotation.y, mesh.rotation.x, mesh.rotation.z) * Matrix4.translation(mesh.position)
      val transformMatrix = worldMatrix * viewMatrix * projectionMatrix

      var i = 0
      while (i < mesh.faces.length) {
        val face = mesh.faces(i)

        /*        println(s"face $i")*/

        val vertexA = mesh.vertices(face.a)
        val vertexB = mesh.vertices(face.b)
        val vertexC = mesh.vertices(face.c)

        val pixelA = project(vertexA, transformMatrix, worldMatrix)
        val pixelB = project(vertexB, transformMatrix, worldMatrix)
        val pixelC = project(vertexC, transformMatrix, worldMatrix)

        //val color = 0.25f + (i % mesh.faces.length) * 0.75f / mesh.faces.length
        val color = Color(255, 255, 255)

        //if(pixelC.coordinates.x == 320.0f && (pixelC.coordinates.y > 168.2f && pixelC.coordinates.y < 168.201f) && pixelC.coordinates.z < 1.009){
        if(i == 794){
          System.nanoTime()
          debugNow = true
        } else {
          debugNow = false
        }

        drawTriangle(pixelA, pixelB, pixelC, color)

        i = i + 1
      }


    })



    val pw: PixelWriter = graphicsContext.getPixelWriter

    pw.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance, buffer, 0, width * 3)

  }

  def clear(): Unit = {
    RgbUtils.fillArrayWithColour(buffer, Color(0, 0, 0))
    var i = 0
    while (i < depthBuffer.length) {
      depthBuffer.update(i, Float.MaxValue)
      i = i + 1
    }

  }

  def putPixel(x: Int, y: Int, z: Float, color: Color) = {

    val index = x + y * width
    val index3 = index * 3

    /*    val s = if(depthBuffer(index) < z) {
          "discarded"
        } else "painted"

        println(s"$x $y : $z   $s because ${depthBuffer(index)}")*/

    if (depthBuffer(index) >= z) {
      depthBuffer.update(index, z)

      buffer.update(index3, color.redByte)
      buffer.update(index3 + 1, color.greenByte)
      buffer.update(index3 + 2, color.blueByte)
    }

  }

  def drawPoint(point: Vector3, color: Color) = {
    if(debugNow){
      System.nanoTime()
    }
    if (point.x >= 0 && point.y >= 0 && point.x < width && point.y < height) {
      putPixel(point.x.toInt, point.y.toInt, point.z, color)
    }
  }

  def processScanLine(data: ScanLineData, va: Vertex, vb: Vertex, vc: Vertex, vd: Vertex, color: Color) {
    var _color = color;

    val pa = va.coordinates
    val pb = vb.coordinates
    val pc = vc.coordinates
    val pd = vd.coordinates

    val gradient1 = if (pa.y != pb.y) (data.currentY - pa.y) / (pb.y - pa.y) else 1
    val gradient2 = if (pc.y != pd.y) {
      (data.currentY - pc.y) / (pd.y - pc.y)
    } else 1

    val sx = FloatUtils.lerp(pa.x, pb.x, gradient1).toInt
    val ex = FloatUtils.lerp(pc.x, pd.x, gradient2).toInt

    val z1 = FloatUtils.lerp(pa.z, pb.z, gradient1)
    val z2 = FloatUtils.lerp(pc.z, pd.z, gradient2)

    val snl = FloatUtils.lerp(data.ndotla, data.ndotlb, gradient1)
    val enl = FloatUtils.lerp(data.ndotlc, data.ndotld, gradient2)

    var x = sx

    if(debugNow){
      System.nanoTime()
    }

    while (x < ex) {
      val gradient = (x - sx) / (ex - sx).toFloat
      val z = FloatUtils.lerp(z1, z2, gradient)
      val ndot1 = FloatUtils.lerp(snl, enl, gradient)

      if(debugNow){
        System.nanoTime()
      }

/*      if (x == 310 && data.currentY > 140 && data.currentY < 145 && (ndot1 > 0.8f || ndot1 < 0.4f)) {
        _color = Color(255, 0, 0)
        System.nanoTime()
      }*/

      drawPoint(new Vector3(x, data.currentY, z), _color * ndot1)
      x = x + 1
    }
  }

  def computeNDotL(vertex: Vector3, normal: Vector3, lightPosition: Vector3): Float = {
    val lightDirection = lightPosition - vertex

    val normalized = normal.normalised
    val lightDirNormalized = lightDirection.normalised

    Math.max(0, normalized dot lightDirNormalized)
  }

  def drawTriangle(_v1: Vertex, _v2: Vertex, _v3: Vertex, color: Color) = {
    var v1 = _v1
    var v2 = _v2
    var v3 = _v3

    if (v1.coordinates.y > v2.coordinates.y) {
      val temp = v2
      v2 = v1
      v1 = temp
    }
    if (v2.coordinates.y > v3.coordinates.y) {
      val temp = v2
      v2 = v3
      v3 = temp
    }
    if (v1.coordinates.y > v2.coordinates.y) {
      val temp = v2
      v2 = v1
      v1 = temp
    }



   /* if(p2.x == 320.0f && (p2.y > 168.2f && p2.y < 168.201f) && p2.z < 1.009){
      System.nanoTime()
    }*/

    val p1 = v1.coordinates;
    val p2 = v2.coordinates;
    val p3 = v3.coordinates;

    if(debugNow){
      System.nanoTime()
    }

    val nl1 = computeNDotL(v1.worldCoordinates, v1.normal, lightPosition)
    val nl2 = computeNDotL(v2.worldCoordinates, v2.normal, lightPosition)
    val nl3 = computeNDotL(v3.worldCoordinates, v3.normal, lightPosition)

    // inverse slopes
    var dP1P2: Float = 0
    var dP1P3: Float = 0

    // http://en.wikipedia.org/wiki/Slope
    if (p2.y - p1.y > 0) {
      dP1P2 = (p2.x - p1.x) / (p2.y - p1.y)
    } else {
      dP1P2 = 0
    }

    if (p3.y - p1.y > 0) {
      dP1P3 = (p3.x - p1.x) / (p3.y - p1.y)
    } else {
      dP1P3 = 0
    }

    if(debugNow){
      System.nanoTime()
    }

    if (dP1P2 > dP1P3) {
      var y = p1.y.toInt
      while (y <= p3.y.toInt) {
        if (y < p2.y) {
          val data = new ScanLineData(y, nl1, nl3, nl1, nl2)
          if(debugNow){
            System.nanoTime()
          }
          this.processScanLine(data, v1, v3, v1, v2, color)
        } else {
          val data = new ScanLineData(y, nl1, nl3, nl2, nl3)
          if(debugNow){
            System.nanoTime()
          }
          this.processScanLine(data, v1, v3, v2, v3, color)
        }
        y = y + 1
      }
    }

    else {
      var y = p1.y.toInt
      while (y <= p3.y.toInt) {
        if (y < p2.y) {
          val data = new ScanLineData(y, nl1, nl2, nl1, nl3)
          if(debugNow){
            System.nanoTime()
          }
          this.processScanLine(data, v1, v2, v1, v3, color)
        } else {
          val data = new ScanLineData(y, nl2, nl3, nl1, nl3)
          if(debugNow){
            System.nanoTime()
          }
          this.processScanLine(data, v2, v3, v1, v3, color)
        }
        y = y + 1
      }
    }
  }


  def project(target: Vertex, transformationMatrix: Matrix4, world: Matrix4) = {
    val point2d = Vector3.transformCoordinates(target.coordinates, transformationMatrix)
    val point3dWorld = Vector3.transformCoordinates(target.coordinates, world)
    val normal3dWorld = Vector3.transformCoordinates(target.normal, world)

    val x = point2d.x * width + width / 2.0f
    val y = -point2d.y * height + height / 2.0f
    new Vertex(new Vector3(x, y, point2d.z), point3dWorld, normal3dWorld)
  }


}
