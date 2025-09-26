package org.voegtle.weatherwidget.location

enum class LocationIdentifier(val id: String) {
  Paderborn("tegelweg8"), BadLippspringe("bali"), Leopoldshoehe("leoxity"), Bonn("forstweg17"), Magdeburg("elb"), Herzogenaurach("herzo"), Freiburg("ochsengasse"), Mobil("instant"), Shenzhen("shenzhen");

  companion object {
    fun getByString(stringIdentifier: String): LocationIdentifier? {
      return entries.firstOrNull { it.id == stringIdentifier }
    }
  }
}
