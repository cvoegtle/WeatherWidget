package org.voegtle.weatherwidget.location;

public enum LocationIdentifier {
  Paderborn("tegelweg8"), BadLippspringe("bali"), Leopoldshoehe("leoxity"), Bonn("forstweg17"), Herzogenaurach("herzo"), Freiburg("ochsengasse"), Mobil("mobil");
  private final String id;

  LocationIdentifier(String id) {
    this.id = id;
  }

  public static LocationIdentifier getByString(String stringIdentifier) {
    for (LocationIdentifier identifier : values()) {
      if (identifier.id.equals(stringIdentifier)) {
        return identifier;
      }
    }
    return null;
  }
}
