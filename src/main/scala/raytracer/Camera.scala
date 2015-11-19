package raytracer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.{PixelFormat, PixelWriter}

import renderer.RgbUtils
import struct.{Ray, ColoredSphere, Color, Vector3}

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

    rayHit.hitObj.asInstanceOf[Colored].getMaterialType match {
      case Diffuse => {
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
      case Reflection => {
        if(rayHit.iteration > 5){
          Color(0, 255, 0)
        } else {
          val hitPointNormal = rayHit.hitObj.asInstanceOf[Colored].getNormal(rayHit.hit.get)

          val l = -(rayHit.hit.get - position).normalised
          val n = hitPointNormal.normalised

          val someVec = l - (hitPointNormal * 2) * (n dot l)
          val v =  (rayHit.ray.origin - rayHit.hit.get).normalised

          val h = (l + v).normalised

          val rayDir = -someVec.normalised
          val ray = Ray(rayHit.hit.get, rayDir, 0f, rayHit.iteration + 1)
          val intersects = world.intersects(ray)
          intersects.hit.map(x => getColorForRayhit(intersects, world)).getOrElse(Color.black)
        }
      }
      case Refraction(refractionIndex) => {
        if(rayHit.iteration > 2){
          Color(0, 255, 0)
        } else {
          var n = rayHit.hitObj.asInstanceOf[Colored].getNormal(rayHit.hit.get)

          val d = rayHit.ray.direction

          var (eta1, eta2) = rayHit.iteration match {
            case x if x % 2 == 0 => (1f, refractionIndex)
            case x =>  (refractionIndex, 1f)
          }

/*          var cosPhi1 = (d * -1) dot n
          if(cosPhi1 <= 0){
            cosPhi1 = -cosPhi1
            n = n * -1
            val temp = eta1
            eta1 = eta2
            eta2 = temp
            println("Inverting")
          }

          val quotient = eta1 / eta2
          val radikand = 1f - quotient * quotient * (1 - cosPhi1 * cosPhi1)
          if(radikand < 0){
            Color(255, 0, 0)
          } else {
            val rd = d + (n * (2f * cosPhi1 ))

            val cosPhi2 = Math.sqrt(radikand).toFloat
            val rt = (d * quotient) - (n * (cosPhi2 - quotient * cosPhi1))
            val r0 = Math.pow((eta1 - eta2) / (eta1 + eta2), 2).toFloat
            val r = r0 + (1f - r0) * Math.pow(1.0 - cosPhi1, 5).toFloat
            val t = 1 - r
            if(t < 0){
              throw new RuntimeException(s"???? t = $t")
            }

            val trollDirection = -n

            val rayDirection = trollDirection.normalised
            val ray = Ray(rayHit.hit.get + rayDirection, rayDirection, 0f, rayHit.iteration + 1)
            val intersects = world.intersects(ray)

            if(ray.iteration == 2){
              println(s"Last point: \n${rayHit.hit} current point: \n${intersects.hit}\n dir: ${rayDirection}")
            }

            intersects.hit.map(x => getColorForRayhit(intersects, world)).getOrElse(Color.black)
          }*/


          val refract1 = refract(rayHit.ray, rayHit.hit.get, n, refractionIndex)
          val rayDirection = refract1.normalised
          val ray = Ray(rayHit.hit.get + rayDirection * 0.01f, rayDirection, 0f, rayHit.iteration + 1)
          val intersects = world.intersects(ray)

          intersects.hit.map(x => getColorForRayhit(intersects, world)).getOrElse(Color.black)
        }
      }
    }



  }

  def refract(r: Ray, intersection: Vector3, normal: Vector3, refractionIndex: Float): Vector3 = {

    val unitNormal = normal.normalised
    val ray = r.direction

    val cos_theta1 = -((unitNormal dot ray) / ray.magnitude).toFloat
    val sin_theta1 = Math.sqrt(1-cos_theta1*cos_theta1).toFloat
    val sin_theta2 = sin_theta1/refractionIndex
    val cos_theta2 = Math.sqrt(1.0f - sin_theta2 * sin_theta2)
    val negUnitNormal = -unitNormal
    val projection = negUnitNormal * (ray.magnitude * cos_theta2).toFloat
    val temp = unitNormal * (unitNormal dot ray)
    val lowerHorizontal = temp * (1.0f / refractionIndex)
    val refraction = lowerHorizontal + projection

    refraction
  }


/*  def refract(n1: Float, n2: Float, n: Vector3, l: Vector3): Vector3 = {
    val cosThetaI = (-n) dot l
    val sqrtMe = 1f - Math.pow(n1 / n2, 2.0).toFloat * (1.0 - Math.pow(cosThetaI, 2.0).toFloat)

    val refract = if(cosThetaI > 0){
      val cosTheta2 = Math.sqrt(sqrtMe).toFloat;

      if(cosThetaI > 0){
        l.*(n1 - n2).+(n, (n1 / n2) * cosThetaI - cosTheta2)
      } else {
        throw new RuntimeException("ops")
      }

    } else {
      ???
    }

    refract
  }*/

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
