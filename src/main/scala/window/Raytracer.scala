package window

import javafx.scene.canvas.GraphicsContext

import common.Config
import raytracer._
import struct._

import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.canvas.Canvas
import scalafx.scene.{Group, Scene}

object Raytracer extends JFXApp {
  val width = 800
  val height = 800


  val canvas = new Canvas(width, height)
  implicit val graphicsContext: GraphicsContext = canvas.getGraphicsContext2D

  val fpsCounter = new FPSCOunter

  val world = World(
    new ColoredSphere(new Vector3(0, 0, 1), 0.4f, Color(0, 0, 255)),
    new ColoredSphere(new Vector3(0.4f, 0, 1.1f), 0.3f, Color(255, 0, 0)),
    ColoredPlane(new Vector3(0, 0, -1), 2, Color(0, 255, 0)))

  val camera = new OrthographicCamera(Vector3(0f, 0f, 0f), Vector3(0, 0, 1).normalised, width, height)
  //val camera = new PerspectiveCamera(Vector3(0f, 0f, 0f), Vector3(0, 0, 1).normalised, 0.7f, width, height)

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
        //val wim = new WritableImage(Config.width, Config.height)
        //canvas.snapshot(null, wim)

        //val xImage = SwingFXUtils.fromFXImage(wim, null)
        //ImageIO.write(xImage, "png", Files.newOutputStream(Paths.get(Config().getString("saveRenderPath") + "render.png"), StandardOpenOption.CREATE))
      } catch {
        case e: Exception =>
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
