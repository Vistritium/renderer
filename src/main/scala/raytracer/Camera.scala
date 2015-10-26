package raytracer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.{PixelFormat, PixelWriter}

import renderer.RgbUtils
import struct.Color

trait Camera {

  var width: Int
  var height: Int


  protected var buffer: Array[Byte] = Array.fill[Byte](width * height * 3)(0x0)
  var clearColor: Color = Color.apply(0, 0, 0)


  def render(world: World)(implicit graphicsContext: GraphicsContext) = {

    RgbUtils.fillArrayWithColour(buffer, clearColor)
    draw(world)
    val pw: PixelWriter = graphicsContext.getPixelWriter
    pw.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance, buffer, 0, width * 3)
  }

  protected def draw(world: World)

  protected def putPixel(x: Int, y: Int, color: Color) = {
    val index = x + y * width
    val index3 = index * 3

    buffer.update(index3, color.redByte)
    buffer.update(index3 + 1, color.greenByte)
    buffer.update(index3 + 2, color.blueByte)
  }


}
