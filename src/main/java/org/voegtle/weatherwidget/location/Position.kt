package org.voegtle.weatherwidget.location

data class Position(val latitude: Float = 0.0F, val longitude: Float = 0.0F) {
  private val EARTH_RADIUS = 6371.0 // Approx Earth radius in KM

  operator fun minus(subtract: Position): Position {
    return Position(latitude = this.latitude - subtract.latitude, longitude = this.longitude - subtract.longitude)
  }

  fun distanceTo(position: Position): Double {
    val diff = this - position

    val diffLat = Math.toRadians(diff.latitude.toDouble())
    val diffLong = Math.toRadians(diff.longitude.toDouble())

    val startLat = Math.toRadians(this.latitude.toDouble())
    val endLat = Math.toRadians(position.latitude.toDouble())

    val a = haversin(diffLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(diffLong)
    return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(a))
  }

  fun haversin(value: Double) = Math.pow(Math.sin(value / 2), 2.0)

}
