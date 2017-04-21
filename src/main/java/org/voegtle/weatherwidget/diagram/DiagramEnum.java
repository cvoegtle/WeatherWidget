package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days(1, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=291472484&format=image"),
  average7days(2, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1780492499&format=image"),
  winterdays(3, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=252844556&format=image"),
  summerdays(4, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1031038123&format=image"),
  summerdays2016(5, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=308371431&format=image"),
  rain(6, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=477091892&format=image"),
  pb_bali_leo(7, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=738029596&format=image"),

  paderborn_2days(8, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=426012227&format=image"),
  paderborn_30days(9, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=183626445&format=image"),
  paderborn_year(10, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=747368525&format=image"),
  paderborn_lastyear(11, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2118924146&format=image"),

  freiburg_2days(12, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=145042526&format=image"),
  freiburg_wind(13, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1045869484&format=image"),
  freiburg_30days(14, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1650739963&format=image"),
  freiburg_year(15, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1963429675&format=image"),
  freiburg_lastyear(16, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1557105940&format=image"),

  bonn_2days(17, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=529970705&format=image"),
  bonn_wind(18, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=914845123&format=image"),
  bonn_30days(19, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1771661938&format=image"),
  bonn_year(20, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=2014590801&format=image"),
  bonn_lastyear(21, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1706278998&format=image"),

  bali_7days(22, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=376041681&format=image"),
  bali_paderborn(23, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1754363161&format=image"),
  bali_wind(24, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1959064763&format=image"),
  bali_humidity(25, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1408859930&format=image"),
  bali_30days(26, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1067873495&format=image"),
  bali_lastyear(27, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=583402693&format=image"),

  mobil_7days(28, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1380559031&format=image"),

  leo_30days(29, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1610295076&format=image"),
  leo_lastyear(30, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=322622774&format=image"),
  leo_regen(31, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1205500547&format=image"),
  leo_wind(32, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=75598496&format=image"),
  leo_solar(33, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=255192281&format=image"),
  leo_solar_average(34, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1485103805&format=image"),
  leo_solar_production(35, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1189452944&format=image"),

  herzo_regen(36, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1655654633&format=image"),
  herzo_wind(37, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1843697553&format=image"),
  herzo_lastyear(38, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2068675477&format=image"),
  herzo_30days(39, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=951457656&format=image"),
  family_weather(40, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1089204796&format=image"),

  magdeburg_regen(41, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2090578754&format=image"),
  magdeburg_humidity(42, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=808641441&format=image"),
  magdeburg_paderborn_freiburg(43, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=439313787&format=image"),
  magedburg_30days(44, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=446471860&format=image"),
  magedburg_lastyear(45, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=968375379&format=image"),

  shenzhen_7days(46, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1981128132&format=image");

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
