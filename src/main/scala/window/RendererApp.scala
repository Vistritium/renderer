package window

import java.nio.file.{StandardOpenOption, Files, Paths}
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritableImage
import javax.imageio.ImageIO

import common.Config
import renderer.{BabylonImporter, Renderer}
import struct.Vector3

import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.canvas.Canvas
import scalafx.scene.{Group, Scene}

object RendererApp extends JFXApp {
  val width = Config().getInt("width")
  val height = Config().getInt("height")

  val canvas = new Canvas(width, height)
  val graphicsContext: GraphicsContext = canvas.getGraphicsContext2D

  val camera = Config.camera
  var renderer = new Renderer(width, height, graphicsContext)

  val meshes = BabylonImporter.importModel(getClass.getClassLoader.getResourceAsStream("models/monkey.babylon")) //ObjImporter.importObj(Paths.get(getClass.getClassLoader.getResource("models/monkey.obj").toURI))

/*  meshes.foreach(x => {
    x.rotation = new Vector3(Math.PI.toFloat, 0, 0)
  })*/
  //meshes(0).vertices.map(v => s"${v.coordinates.x} ${v.coordinates.y} ${v.coordinates.z} normal ${v.normal.x} ${v.normal.y} ${v.normal.z}").foreach(println)

  val fpsCounter = new FPSCOunter

  val group = new Group(canvas)

  stage = new PrimaryStage {
    width = RendererApp.this.width + 15
    height = RendererApp.this.height + 37
    scene = new Scene {
      root = group
    }

    Platform.runLater({
      looped()

      try {
        val wim = new WritableImage(Config.width, Config.height)
        canvas.snapshot(null, wim)

        val xImage = SwingFXUtils.fromFXImage(wim, null)
        ImageIO.write(xImage, "png", Files.newOutputStream(Paths.get(Config().getString("saveRenderPath") + "render.png"), StandardOpenOption.CREATE))
      } catch {
        case e: Exception =>
      }
    })
  }


  //noinspection ZeroIndexToHead
  def looped(): Unit = {

    renderer.render(meshes.toList, camera)

    val defaultMesh = meshes(0)

    fpsCounter.tick()
    stage.setTitle(s"FPS ${fpsCounter.getFPS}")
    defaultMesh.rotation = defaultMesh.rotation + Vector3(0f, 0.01f, 0)

    if (!Thread.interrupted()) {
      Platform.runLater({
        looped()
      })
    }

  }
}


