package raytracer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.{PixelFormat, PixelWriter}

import raytracer.Light
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

  val light: Option[Light]


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
    light match {
      case None => rayHit.hitObj.asInstanceOf[ColoredTriangle].color(rayHit.hit.get) + rayHit.hitObj.asInstanceOf[ColoredTriangle].material.ambient
      case Some(x) => {
        val isInShadow = x.isInShadow(rayHit)(this, world)
        val (diff, ambient, spec) = x.getDiffuseAmbientSpecular(rayHit)(this)
        if(isInShadow) ambient else diff + spec + ambient
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
