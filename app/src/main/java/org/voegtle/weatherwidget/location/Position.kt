package org.voegtle.weatherwidget.location

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Position(val latitude: Float = 0.0F, val longitude: Float = 0.0F) {

  operator fun minus(subtract: Position): Position {
    return Position(latitude = this.latitude - subtract.latitude, longitude = this.longitude - subtract.longitude)
  }

  fun distanceTo(position: Position): Double {
    val diff = this - position

    val diffLat = Math.toRadians(diff.latitude.toDouble())
    val diffLong = Math.toRadians(diff.longitude.toDouble())

    val startLat = Math.toRadians(this.latitude.toDouble())
    val endLat = Math.toRadians(position.latitude.toDouble())

    val a = haversin(diffLat) + cos(startLat) * cos(endLat) * haversin(diffLong)
    return 2 * EARTH_RADIUS * asin(sqrt(a))
  }

  private fun haversin(value: Double) = sin(value / 2).pow(2.0)

  companion object {
    private const val EARTH_RADIUS = 6371.0 // Approx Earth radius in KM
  }

}
