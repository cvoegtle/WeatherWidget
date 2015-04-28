package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days(1, "http://wetterimages.appspot.com/weatherstation/image?oid=22&zx=itnq9cg9itj1"),
  average7days(2, "http://wetterimages.appspot.com/weatherstation/image?oid=23&zx=juk5ebnhgov3"),
  winterdays(3, "http://wetterimages.appspot.com/weatherstation/image?oid=26&zx=3goxceuvpnz7"),
  summerdays(4, "http://wetterimages.appspot.com/weatherstation/image?oid=24&zx=bmq3fhig2c"),
  pb_bali_leo(5, "http://wetterimages.appspot.com/weatherstation/image?oid=37&zx=8shatbpp94um"),

  paderborn_2days(6, "http://wetterimages.appspot.com/weatherstation/image?oid=3&zx=jfy3wnfa5exa"),
  paderborn_year(7, "http://wetterimages.appspot.com/weatherstation/image?oid=4&zx=oqwhqpkqpxqq"),
  paderborn_lastyear(8, "http://wetterimages.appspot.com/weatherstation/image?oid=21&zx=bju20lesmatj"),

  freiburg_2days(9, "http://wetterimages.appspot.com/weatherstation/image?oid=7&zx=1geb1qiwcx3b"),
  freiburg_year(10, "http://wetterimages.appspot.com/weatherstation/image?oid=16&zx=v9yov3sfksqb"),
  freiburg_lastyear(11, "http://wetterimages.appspot.com/weatherstation/image?oid=25&zx=3l67kc4xl7vx"),

  bonn_2days(12, "http://wetterimages.appspot.com/weatherstation/image?oid=29&zx=uf33j7c2qrmv"),
  bonn_year(13, "http://wetterimages.appspot.com/weatherstation/image?oid=30&zx=8m9h7ygvo9cp"),
  bonn_lastyear(14, "http://wetterimages.appspot.com/weatherstation/image?oid=28&zx=bmqfp6za64c3"),

  bali_7days(15, "http://wetterimages.appspot.com/weatherstation/image?oid=33&zx=hqigco4cuzse"),
  bali_paderborn(16, "http://wetterimages.appspot.com/weatherstation/image?oid=32&zx=ony2kc4wm9nx"),
  bali_wind(17, "http://wetterimages.appspot.com/weatherstation/image?&oid=39&zx=64jdtbhkhdf"),

  mobil_paderborn(18, "http://wetterimages.appspot.com/weatherstation/image?oid=34&zx=hnij97x1rn3p"),
  mobil_regen(19, "http://wetterimages.appspot.com/weatherstation/image?oid=35&zx=9lmgfvyeawi1"),
  mobil_wind(20, "http://wetterimages.appspot.com/weatherstation/image?oid=36&zx=cz5tavhaf7p1"),
  mobil_solar(21, "http://wetterimages.appspot.com/weatherstation/image?oid=38&zx=78w1b627jesf"),

  leo_paderborn(22, "http://wetterimages.appspot.com/weatherstation/image?oid=40&zx=zf3qhidwp0d3"),
  leo_regen(23, "http://wetterimages.appspot.com/weatherstation/image?oid=41&zx=thizdb5vljeh"),
  leo_wind(24, "http://wetterimages.appspot.com/weatherstation/image?oid=42&zx=yodi027ovehq"),
  leo_solar(25, "http://wetterimages.appspot.com/weatherstation/image?oid=43&zx=3qzlsg27ha0v");

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

  public static DiagramEnum byId(int id) {
    for (DiagramEnum diagramEnum : DiagramEnum.values()) {
      if (diagramEnum.getId() == id) {
        return diagramEnum;
      }
    }
    return null;
  }
}
