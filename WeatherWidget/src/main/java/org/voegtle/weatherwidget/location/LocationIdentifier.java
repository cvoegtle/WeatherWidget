package org.voegtle.weatherwidget.location;

public enum LocationIdentifier {
  Paderborn, Freiburg, Bonn;

  public static LocationIdentifier getByString(String stringIdentifier) {
    for (LocationIdentifier id : values()) {
      if (id.toString().equals(stringIdentifier)) {
        return id;
      }
    }
    return null;
  }
}
