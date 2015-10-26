package common

import java.nio.file.{Paths, Files}

import com.typesafe.config.ConfigFactory
import struct.{Vector3, Camera}

object Config {

  private val customPath = Paths.get("custom.conf")
  val launchConfig = Files.exists(customPath)
  println(s"custom.conf $launchConfig")
  

  val conf = ConfigFactory.load()

  def apply() = conf

  val width = Config().getInt("width")
  val height = Config().getInt("height")

  val camera = new Camera(new Vector3(conf.getDouble("camera.pos.x").toFloat, conf.getDouble("camera.pos.y").toFloat, conf.getDouble("camera.pos.z").toFloat), new Vector3(conf.getDouble("camera.target.x").toFloat, conf.getDouble("camera.target.y").toFloat, conf.getDouble("camera.target.z").toFloat))

  val light = new Vector3(conf.getDouble("light.pos.x").toFloat, conf.getDouble("light.pos.y").toFloat, conf.getDouble("light.pos.z").toFloat)

}
