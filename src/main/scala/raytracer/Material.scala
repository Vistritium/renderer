package raytracer

import java.awt.image.BufferedImage

import struct.Color

sealed class MaterialType
case object Diffuse extends MaterialType
case object Reflection extends MaterialType
case class Refraction(refractionIndex: Float) extends MaterialType

class Material(val ambient: Color, val diffuse: Color, val specular: Color, val texture: Option[BufferedImage], val materialType: MaterialType) {

  def this(ambient: Color, diffuse: Color, specular: Color, texture: Option[BufferedImage] = None) = this(ambient, diffuse, specular, texture, Diffuse)


}
