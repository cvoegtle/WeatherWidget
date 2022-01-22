package uk.co.senab.photoview.data

data class FloatPosition(val x: Float, val y: Float) {
  operator fun plus(delta: FloatPosition) = FloatPosition(x + delta.x, y + delta.y)
  operator fun minus(delta: FloatPosition) = FloatPosition(x - delta.x, y - delta.y)

  fun distance(): Double = Math.sqrt((x * x + y * y).toDouble())
}