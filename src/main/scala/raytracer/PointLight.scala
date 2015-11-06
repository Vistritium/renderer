package raytracer

import struct._

class PointLight(val position: Vector3, lightDiffuse: Color = Color.white, lightAmbient: Color = Color.white, lightSpecular: Color = Color(255, 255, 255)) extends Light {

  override def getColor(rayHit: RayHit)(implicit camera: Camera): Color = {


    val hitPosition = rayHit.hit.get
    val hitTriangle = rayHit.hitObj.asInstanceOf[ColoredTriangle]

    val (hitPointNormal, texUv) = barycentricInterpolation(hitTriangle.a, hitTriangle.b, hitTriangle.c, hitTriangle.na, hitTriangle.nb, hitTriangle.nc, hitPosition, hitTriangle.texA, hitTriangle.texB, hitTriangle.texC)

    val c = computeNDotL(hitPosition, hitPointNormal, position)

    //val cameraPos = camera.position

    val objColor = {
      rayHit.hitObj.asInstanceOf[ColoredTriangle].material.texture match {
        case None => rayHit.hitObj.asInstanceOf[ColoredTriangle].material.diffuse
        case Some(img) => {
          val b = try{
            img.getRGB((texUv.x * (img.getWidth - 1)).toInt, (texUv.y * (img.getHeight - 1)).toInt)
          } catch {
            case x: ArrayIndexOutOfBoundsException => {

              throw x
            }
          }

          Color((b & 0x00ff0000) >> 16, (b & 0x0000ff00) >> 8, (b & 0x000000ff))
        }
      }

    }

    val diffColor = objColor * c
    val specColor = getSpecular(rayHit, hitPointNormal, camera)
    val ambient = rayHit.hitObj.asInstanceOf[ColoredTriangle].material.ambient

    diffColor * lightDiffuse + specColor * lightSpecular + ambient * lightAmbient

  }

  //http://answers.unity3d.com/questions/383804/calculate-uv-coordinates-of-3d-point-on-plane-of-m.html
  def barycentricInterpolation(p1: Vector3, p2: Vector3, p3: Vector3, uv1: Vector3, uv2: Vector3, uv3: Vector3, f: Vector3, texUv1: Vector2, texUv2: Vector2, texUv3: Vector2): (Vector3, Vector2) = {

    val f1 = p1 - f
    val f2 = p2 - f
    val f3 = p3 - f
    // calculate the areas and factors (order of parameters doesn't matter):
    val a = ((p1-p2) cross (p1-p3)).magnitude // main triangle area a
    val a1 = (f2 cross f3).magnitude / a; // p1's triangle area / a
    val a2 = (f3 cross f1).magnitude / a; // p2's triangle area / a
    val a3 = (f1 cross f2).magnitude / a; // p3's triangle area / a
    // find the uv corresponding to point f (uv1/uv2/uv3 are associated to p1/p2/p3):
    val uv = uv1 * a1 + uv2 * a2 + uv3 * a3
    val texUv = texUv1 * a1 + texUv2 * a2 + texUv3 * a3



    (uv, Vector2(FloatUtils.clamp(texUv.x), FloatUtils.clamp(texUv.y)))
  }


  def computeNDotL(vertex: Vector3, normal: Vector3, lightPosition: Vector3): Float = {
    val lightDirection = lightPosition - vertex

    val normalized = normal.normalised
    val lightDirNormalized = lightDirection.normalised

    Math.max(0, normalized dot lightDirNormalized)
  }

  def getSpecular(rayHit: RayHit, hitPointNormal: Vector3, camera: Camera): Color = {

    val l = -(rayHit.hit.get - position).normalised
    val n = hitPointNormal.normalised
    val v =  (rayHit.ray.origin - rayHit.hit.get).normalised

    val h = (l + v).normalised

    val NdotH = n dot h

    val intensity = Math.pow(Math.max(NdotH, 0).toFloat, 16f ).toFloat

    rayHit.hitObj.asInstanceOf[ColoredTriangle].material.specular * intensity

  }

  override def isInShadow(rayHit: RayHit)(implicit camera: Camera, world: World): Boolean = world.intersects(Ray(position, rayHit.hit.get - position , 0f)).hitObj != rayHit.hitObj

}
