package org.voegtle.weatherwidget.diagram

enum class DiagramEnum constructor(val id: Int, val url: String) {
  temperature7days(1, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=291472484&format=image"),
  average7days(2, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1780492499&format=image"),
  winterdays(3, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=386238328&format=image"),
  winterdays2021(4, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1463953487&format=image"),
  summerdays(5, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1885598364&format=image"),
  summerdays2021(6, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=971600707&format=image"),
  rain(7, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=477091892&format=image"),
  pb_bali_leo(8, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=738029596&format=image"),

  paderborn_2days(9, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=426012227&format=image"),
  paderborn_30days(10, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=183626445&format=image"),
  paderborn_year(11, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=747368525&format=image"),
  paderborn_lastyear(12, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2118924146&format=image"),

  freiburg_2days(13, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=145042526&format=image"),
  freiburg_wind(14, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1045869484&format=image"),
  freiburg_30days(15, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1650739963&format=image"),
  freiburg_year(16, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1963429675&format=image"),
  freiburg_lastyear(17, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1557105940&format=image"),

  bonn_2days(18, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=529970705&format=image"),
  bonn_wind(19, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=914845123&format=image"),
  bonn_30days(20, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1771661938&format=image"),
  bonn_year(21, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=2014590801&format=image"),
  bonn_lastyear(22, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1706278998&format=image"),

  bali_7days(23, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=376041681&format=image"),
  bali_paderborn(24, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1754363161&format=image"),
  bali_wind(25, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1959064763&format=image"),
  bali_humidity(26, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1408859930&format=image"),
  bali_30days(27, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1067873495&format=image"),
  bali_lastyear(28, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=583402693&format=image"),

  mobil_7days(29, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1380559031&format=image"),
  mobil_freiburg(30, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1683337276&format=image"),

  leo_30days(31, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1610295076&format=image"),
  leo_lastyear(32, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=322622774&format=image"),
  leo_regen(33, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1205500547&format=image"),
  leo_barometer(34, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1474051069&format=image"),
  leo_power(35, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=26392282&format=image"),
  leo_wind(36, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=75598496&format=image"),
  leo_solar_average(37, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1485103805&format=image"),
  leo_solar_production(38, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1189452944&format=image"),

  herzo_regen(39, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1655654633&format=image"),
  herzo_wind(40, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1843697553&format=image"),
  herzo_lastyear(41, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2068675477&format=image"),
  herzo_30days(42, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=951457656&format=image"),
  family_weather(43, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1089204796&format=image"),

  magdeburg_regen(44, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2090578754&format=image"),
  magdeburg_wind(45, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1951689780&format=image"),
  magdeburg_humidity(46, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=808641441&format=image"),
  magdeburg_paderborn_freiburg(47, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=439313787&format=image"),
  magedburg_30days(48, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=446471860&format=image"),
  magedburg_lastyear(49, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=968375379&format=image"),

  shenzhen_7days(50, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=1981128132&format=image"),
  shenzhen_30days(51, "https://wetterimages.appspot.com/weatherstation/image?sheet=1&oid=1526059248&format=image"),
  shenzhen_lastyear(52, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=2094649277&format=image"),

  paderborn20_solarradiation(53, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=443476029&format=image"),
  paderborn20_barometer(54, "https://wetterimages.appspot.com/weatherstation/image?sheet=2&oid=445461471&format=image");

  val filename: String
    get() = "$this.png"

  companion object {

    fun byId(id: Int): DiagramEnum? {
      return DiagramEnum.values().firstOrNull { it.id == id }
    }
  }
}
