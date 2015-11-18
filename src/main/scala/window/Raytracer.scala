package window

import java.nio.file.{Files, Paths, StandardOpenOption}
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritableImage
import javax.imageio.ImageIO

import com.typesafe.config.ConfigFactory
import common.Config
import raytracer._
import struct._

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.canvas.Canvas
import scalafx.scene.{Group, Scene}

object Raytracer extends JFXApp {

  val conf = ConfigFactory.parseResources("raytracer.conf")

  val mode = 2

  val model: List[RayIntersectable with Colored] = mode match {
    case 1 => {
      val modelToImportStr = conf.getString("import.model")
      val model = BabylonImporter.importModel(getClass.getClassLoader.getResourceAsStream(s"models/$modelToImportStr.babylon"), "models")
      World.meshesToObjs(model.toList)
    }
    case 2 => {
      val sphere = new ColoredSphere(new Vector3(0, 1, 0), 1f, Color(255, 255, 255))
      List(sphere)
    }
  }



  val width = conf.getInt("width")
  val height = conf.getInt("height")

  val canvas = new Canvas(width, height)
  implicit val graphicsContext: GraphicsContext = canvas.getGraphicsContext2D

  val fpsCounter = new FPSCOunter

  /*  val world = World(
      new ColoredSphere(new Vector3(0, 0, 1), 0.4f, Color(0, 0, 255)),
      new ColoredSphere(new Vector3(0.4f, 0, 1.1f), 0.3f, Color(255, 0, 0)),
      ColoredPlane.fromPoints(new Vector3(0, 0, 2), new Vector3(2, 0, 2), new Vector3(0, 2, 2), Color(0, 255, 0)))*/


  private val triangleVal = 0.5f
/*  val world = World(
    ColoredTriangle(new Vector3(-triangleVal, -triangleVal, 2), new Vector3(-triangleVal, triangleVal, 2), new Vector3(triangleVal, 0, 2), Color(255, 255, 0)),
    ColoredPlane.fromPoints(new Vector3(0, 2, 3), new Vector3(2, 0, 3), new Vector3(0, 0, 3), Color(0, 255, 0))
  )*/

  val material = new Material(Color(10, 10, 10), Color(100, 100, 100), Color(255, 255, 255), None)
  val floor = new raytracer.Mesh(ArrayBuffer(ColoredTriangle(new Vector3(-100f, -1, -100f), new Vector3(100, -1, -100), new Vector3(0, -1, 100), material)))
  val world = World.fromObjects(model ::: World.meshesToObjs(List(floor)):_*)

  val cameraPos = new Vector3(conf.getDouble("camera.pos.x").toFloat, conf.getDouble("camera.pos.y").toFloat, conf.getDouble("camera.pos.z").toFloat)
  val cameraTarget = new Vector3(conf.getDouble("camera.target.x").toFloat, conf.getDouble("camera.target.y").toFloat, conf.getDouble("camera.target.z").toFloat)
  val lights = readLights

  val antyaliasing = if (classOf[RegularAntyaliasing].getSimpleName.equalsIgnoreCase(conf.getString("camera.antyaliasing"))) {
    new RegularAntyaliasing(conf.getInt(s"${classOf[RegularAntyaliasing].getSimpleName}.raysPerPixelSquared"))
  } else {
    NoAntyaliasing
  }

  val camera = if (classOf[OrthographicCamera].getSimpleName.equalsIgnoreCase(conf.getString("camera.type"))) {
    new OrthographicCamera(cameraPos, cameraTarget, width, height, antyaliasing, lights)
  } else {
    new PerspectiveCamera(cameraPos, cameraTarget, conf.getDouble(s"${classOf[PerspectiveCamera].getSimpleName}.centerDistance").toFloat, width, height, antyaliasing, lights)
  }
  //val camera =

  val group = new Group(canvas)

  stage = new PrimaryStage {
    width = Raytracer.this.width + 15
    height = Raytracer.this.height + 37
    scene = new Scene {
      root = group
    }

    Platform.runLater({
      looped()

      try {
        val wim = new WritableImage(Raytracer.this.width, Raytracer.this.height)
        canvas.snapshot(null, wim)

        val xImage = SwingFXUtils.fromFXImage(wim, null)
        val fileOutputStream = Files.newOutputStream(Paths.get(Config().getString("saveRenderPath") + "render.png"), StandardOpenOption.CREATE)
        ImageIO.write(xImage, "png", fileOutputStream)
        fileOutputStream.close()


      } catch {
        case e: Exception => println(e.getMessage)
      }

    })
  }


  //noinspection ZeroIndexToHead
  def looped(): Unit = {

    camera.render(world)



    if (!Thread.interrupted()) {
      Platform.runLater({
        //looped()
      })
    }

  }

  def readLights: List[raytracer.Light] = {

    val lightPosConf = conf.getConfig("lights")

    @tailrec
    def iter(i: Int, lights: List[raytracer.Light]): List[raytracer.Light] = {
      val path = s"light$i"
      if(!lightPosConf.hasPath(path)) lights  else {
        val lightObj = lightPosConf.getConfig(path)
        val x: Float = lightObj.getDouble("x").toFloat
        val y: Float = lightObj.getDouble("y").toFloat
        val z: Float = lightObj.getDouble("z").toFloat

        val light = if(lightObj.hasPath("red")){
          val red = lightObj.getInt("red")
          val green = lightObj.getInt("green")
          val blue = lightObj.getInt("blue")
          new raytracer.PointLight(new Vector3(x, y, z), Color(red, green, blue))
        }  else {
          new raytracer.PointLight(new Vector3(x, y, z))
        }

        iter(i + 1, light :: lights)
      }
    }
    iter(1, Nil)
  }

}
