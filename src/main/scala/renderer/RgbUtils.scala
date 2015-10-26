package renderer

import struct.Color

object RgbUtils {

  def fillArrayWithColour(array: Array[Byte], red: Byte, green: Byte, blue: Byte) = {
    var i = 0;
    while(i < array.length){
      array.update(i, red)
      array.update(i + 1, green)
      array.update(i + 2, blue)
      i = i + 3
    }
  }

  def fillArrayWithColour(array: Array[Byte], color: Color): Unit ={
    fillArrayWithColour(array, color.redByte, color.greenByte, color.blueByte)
  }



}
