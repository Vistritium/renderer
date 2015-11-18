package raytracer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.{PixelFormat, PixelWriter}

import renderer.RgbUtils
import struct.{Color, Vector3}

trait Camera {

  var width: Int
  var height: Int

  var position: Vector3
  var target: Vector3

  val boxWidth = 2f

  val pixelWidth = boxWidth / width
  val pixelHeight = boxWidth / height

  val lights: List[Light]

  protected var buffer: Array[Byte] = Array.fill[Byte](width * height * 3)(0x0)
  var clearColor: Color = Color.apply(0, 0, 0)


  def render(world: World)(implicit graphicsContext: GraphicsContext) = {

    val beginingTime = System.nanoTime()

    RgbUtils.fillArrayWithColour(buffer, clearColor)
    draw(world)
    val pw: PixelWriter = graphicsContext.getPixelWriter
    pw.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance, buffer, 0, width * 3)

    val endingTime = System.nanoTime()

    println(s"Rendering time: ${(endingTime - beginingTime) / 1000000000.0} seconds")
  }

  def getColorForRayhit(rayHit: RayHit, world: World): Color = {
    val (objDiff, objAmbient, objSpec, hitPointNormal) = rayHit.hitObj.asInstanceOf[Colored].diffuseAmbientSpecularNormal(rayHit.hit.get)
    lights match {
      case Nil => {
        objDiff
      } //rayHit.hitObj.asInstanceOf[ColoredTriangle].color(rayHit.hit.get) + rayHit.hitObj.asInstanceOf[ColoredTriangle].material.ambient
      case x => {

        val eachLightColors = x.map(light => {
          val isInShadow = light.isInShadow(rayHit)(this, world)

          val (lightDiff, lightSpec, lightAmbient) = light.getDiffuseAmbientSpecular(rayHit, hitPointNormal)(this)

          if (isInShadow) {
            (Color.black, lightAmbient * objAmbient, Color.black)
          } else {
            (lightDiff * objDiff, lightSpec * objSpec, lightAmbient * objAmbient)
          }
        })

        val combined = eachLightColors.reduce((left, right) => {
          (left._1 + right._1, left._2 + right._2, left._3 + right._3)
        })

        combined._1 + combined._2 + combined._3
      }
    }

  }

  protected def draw(world: World)

  protected def putPixel(x: Int, y: Int, color: Color) = {
    val index = x + y * width
    val index3 = index * 3

    if (x > 510 && y > 510) {
      System.nanoTime()
    }

    buffer.update(index3, color.redByte)
    buffer.update(index3 + 1, color.greenByte)
    buffer.update(index3 + 2, color.blueByte)
  }


}
