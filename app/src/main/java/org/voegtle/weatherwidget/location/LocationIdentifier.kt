package org.voegtle.weatherwidget.location

enum class LocationIdentifier(private val id: String) {
  Paderborn("tegelweg8"), BadLippspringe("bali"), Leopoldshoehe("leoxity"), Bonn("forstweg17"), Magdeburg("elb"), Herzogenaurach("herzo"), Freiburg("ochsengasse"), Mobil("mobil"), Shenzhen("SZ");

  companion object {
    fun getByString(stringIdentifier: String): LocationIdentifier? {
      return values().firstOrNull { it.id == stringIdentifier }
    }
  }
}
