package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days(1, "http://wetterimages.appspot.com/weatherstation/image?oid=22&zx=itnq9cg9itj1"),
  average7days(2, "http://wetterimages.appspot.com/weatherstation/image?oid=23&zx=juk5ebnhgov3"),
  winterdays(3, "http://wetterimages.appspot.com/weatherstation/image?oid=26&zx=3goxceuvpnz7"),
  summerdays(4, "http://wetterimages.appspot.com/weatherstation/image?oid=24&zx=bmq3fhig2c"),

  paderborn_2days(5, "http://wetterimages.appspot.com/weatherstation/image?oid=3&zx=jfy3wnfa5exa"),
  paderborn_year(6, "http://wetterimages.appspot.com/weatherstation/image?oid=4&zx=oqwhqpkqpxqq"),
  paderborn_lastyear(7, "http://wetterimages.appspot.com/weatherstation/image?oid=21&zx=bju20lesmatj"),

  freiburg_2days(8, "http://wetterimages.appspot.com/weatherstation/image?oid=7&zx=1geb1qiwcx3b"),
  freiburg_year(9, "http://wetterimages.appspot.com/weatherstation/image?oid=16&zx=v9yov3sfksqb"),
  freiburg_lastyear(10, "http://wetterimages.appspot.com/weatherstation/image?oid=25&zx=3l67kc4xl7vx"),

  bonn_2days(11, "http://wetterimages.appspot.com/weatherstation/image?oid=29&zx=uf33j7c2qrmv"),
  bonn_year(12, "http://wetterimages.appspot.com/weatherstation/image?oid=30&zx=8m9h7ygvo9cp"),
  bonn_lastyear(13, "http://wetterimages.appspot.com/weatherstation/image?oid=28&zx=bmqfp6za64c3");

  private String url;
  private int id;

  private DiagramEnum(int id, String url) {
    this.id = id;
    this.url = url;
  }

  public int getId() {
    return id;
  }

  public String getUrl() {
    return url;
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
