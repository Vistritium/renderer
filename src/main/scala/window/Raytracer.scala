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
import window.RendererApp._

import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.canvas.Canvas
import scalafx.scene.{Group, Scene}

object Raytracer extends JFXApp {

  val conf = ConfigFactory.parseResources("raytracer.conf")

  val model = BabylonImporter.importModel(getClass.getClassLoader.getResourceAsStream("models/monkey.babylon"))

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

  val world = World.fromMeshes(model:_*)

  val cameraPos = new Vector3(conf.getDouble("camera.pos.x").toFloat, conf.getDouble("camera.pos.y").toFloat, conf.getDouble("camera.pos.z").toFloat)
  val cameraTarget = new Vector3(conf.getDouble("camera.target.x").toFloat, conf.getDouble("camera.target.y").toFloat, conf.getDouble("camera.target.z").toFloat)


  val antyaliasing = if (classOf[RegularAntyaliasing].getSimpleName.equalsIgnoreCase(conf.getString("camera.antyaliasing"))) {
    new RegularAntyaliasing(conf.getInt(s"${classOf[RegularAntyaliasing].getSimpleName}.raysPerPixelSquared"))
  } else {
    NoAntyaliasing
  }

  val camera = if (classOf[OrthographicCamera].getSimpleName.equalsIgnoreCase(conf.getString("camera.type"))) {
    new OrthographicCamera(cameraPos, cameraTarget, width, height, antyaliasing)
  } else {
    new PerspectiveCamera(cameraPos, cameraTarget, conf.getDouble(s"${classOf[PerspectiveCamera].getSimpleName}.centerDistance").toFloat, width, height, antyaliasing)
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
}
