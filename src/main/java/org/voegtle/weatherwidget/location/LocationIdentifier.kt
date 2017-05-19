package org.voegtle.weatherwidget.location

enum class LocationIdentifier private constructor(private val id: String) {
    Paderborn("tegelweg8"), BadLippspringe("bali"), Leopoldshoehe("leoxity"), Bonn("forstweg17"), Magdeburg("elb"), Herzogenaurach("herzo"), Freiburg("ochsengasse"), Mobil("mobil"), Shenzhen("SZ");


    companion object {

        fun getByString(stringIdentifier: String): LocationIdentifier? {
            for (identifier in values()) {
                if (identifier.id == stringIdentifier) {
                    return identifier
                }
            }
            return null
        }
    }
}
