package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days(1, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=22&zx=itnq9cg9itj1"),
  average7days(2, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=23&zx=nsc624omy43"),
  summerdays(3, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=24&zx=bmq3fhig2c"),

  paderborn_2days(4, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=3&zx=jfy3wnfa5exa"),
  paderborn_year(5, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=4&zx=oqwhqpkqpxqq"),
  paderborn_lastyear(6, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=21&zx=bju20lesmatj"),

  freiburg_2days(7, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=7&zx=1geb1qiwcx3b"),
  freiburg_year(8, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=16&zx=v9yov3sfksqb"),
  freiburg_lastyear(9, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=25&zx=3l67kc4xl7vx");

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
