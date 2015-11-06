package raytracer

import java.awt.image.BufferedImage

import struct.Color


class Material(val ambient: Color, val diffuse: Color, val specular: Color, texture: Option[BufferedImage] = None) {



}
