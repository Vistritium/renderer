package raytracer

import struct.{Plane, Ray, Sphere, Vector3}


object Main {

  def main(args: Array[String]) {


    val sphere = new Sphere(new Vector3(0, 0, 0), 10)
    val r1Origin = new Vector3(0, 0, -20)
    val r1 = Ray(r1Origin, (sphere.center - r1Origin).normalised)
    val r2 = Ray(r1.origin, Vector3.unitY)

    val r1IntersectsSphere = r1.intersectsSphere(sphere)
    println(s"r1 intersects sphere result: $r1IntersectsSphere" )
    val r2IntersectsSphere = r2.intersectsSphere(sphere)
    println(s"r2 intersects sphere result: $r2IntersectsSphere" )

    val r3 = Ray(r1Origin + Vector3.up * 10f, Vector3.unitZ)
    val r3Intersects = r3.intersectsSphere(sphere).get
    println(s"r3 intersects sphere in point $r3Intersects")

    //val plane = Plane(Vector3.lerp(Vector3.unitY, Vector3.unitZ, 0.5f), 0)
    val plane = Plane(new Vector3(0, 1, 1), 0)
    val r2IntersectsPlane = r2.intersectsPlane(plane)
    println(s"r2 intersects plane result: $r2IntersectsPlane")

    val plane2 = Plane(new Vector3(0, -1, 0), 1)
    val r4 = Ray(Vector3.up * 10, Vector3(0, -1, 0), 99)

    println(r4.intersectsPlane(plane2))



  }

}
