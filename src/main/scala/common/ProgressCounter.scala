package common

class ProgressCounter(val bumps: Int) {

  var currentBumps = 0

  val interval = bumps / 100

  def bump() = synchronized {
    currentBumps = currentBumps + 1
  }

  def getProgress(): Float = {
    100 * currentBumps / bumps.toFloat
  }

  def bumpAndMaybePrintProgress(): Unit = {
    synchronized {
      currentBumps = currentBumps + 1
      if(currentBumps % interval == 0){
        println(s"Progress: ${getProgress()}%")
      }
    }
  }

}
