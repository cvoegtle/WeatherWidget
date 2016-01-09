package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days(1, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=291472484&format=image"),
  average7days(2, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2099860984&format=image"),
  winterdays(3, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=44781036&format=image"),
  summerdays(4, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=308371431&format=image"),
  pb_bali_leo(5, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=738029596&format=image"),

  paderborn_2days(6, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=426012227&format=image"),
  paderborn_30days(7, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=183626445&format=image"),
  paderborn_year(8, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=747368525&format=image"),
  paderborn_lastyear(9, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2118924146&format=image"),

  freiburg_2days(10, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=145042526&format=image"),
  freiburg_wind(11, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1045869484&format=image"),
  freiburg_30days(12, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1650739963&format=image"),
  freiburg_year(13, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1963429675&format=image"),
  freiburg_lastyear(14, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1557105940&format=image"),

  bonn_2days(15, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=529970705&format=image"),
  bonn_wind(16, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=914845123&format=image"),
  bonn_30days(17, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1771661938&format=image"),
  bonn_year(18, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=2014590801&format=image"),
  bonn_lastyear(19, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1706278998&format=image"),

  bali_7days(20, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=376041681&format=image"),
  bali_paderborn(21, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1754363161&format=image"),
  bali_wind(22, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1959064763&format=image"),
  bali_humidity(23, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1408859930&format=image"),

  mobil_paderborn(24, "https://wetterimages.appspot.com/weatherstation/image?oid=34&zx=hnij97x1rn3p"),
  mobil_regen(25, "https://wetterimages.appspot.com/weatherstation/image?oid=35&zx=9lmgfvyeawi1"),
  mobil_wind(26, "https://wetterimages.appspot.com/weatherstation/image?oid=36&zx=cz5tavhaf7p1"),

  leo_paderborn(27, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1747103714&format=image"),
  leo_regen(28, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1205500547&format=image"),
  leo_wind(29, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=75598496&format=image"),
  leo_solar(30, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=255192281&format=image"),
  leo_solar_average(31, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1485103805&format=image"),
  leo_solar_production(32, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1189452944&format=image"),

  herzo_regen(33, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1655654633&format=image"),
  herzo_wind(34, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1843697553&format=image"),
  herzo_paderborn_freiburg(35, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1089204796&format=image"),

  magdeburg_regen(36, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2090578754&format=image"),
  magdeburg_humidity(37, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=808641441&format=image"),
  magdeburg_paderborn_freiburg(38, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=439313787&format=image");

  private String url;
  private int id;

  DiagramEnum(int id, String url) {
    this.id = id;
    this.url = url;
  }

  public int getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public String getFilename() {
      return this.toString() + ".png";
  }

  public static DiagramEnum byId(int id) {
    for (DiagramEnum diagramEnum : DiagramEnum.values()) {
      if (diagramEnum.getId() == id) {
        return diagramEnum;
      }
    }
    return null;
  }
}
