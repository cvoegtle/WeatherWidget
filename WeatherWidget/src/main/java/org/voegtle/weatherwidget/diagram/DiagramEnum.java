package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=22&zx=itnq9cg9itj1"),
  average7days("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=23&zx=nsc624omy43"),
  summerdays("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=24&zx=bmq3fhig2c"),

  paderborn_2days("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=3&zx=jfy3wnfa5exa"),
  paderborn_year("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=4&zx=oqwhqpkqpxqq"),
  paderborn_lastyear("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=21&zx=bju20lesmatj"),

  freiburg_2days("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=7&zx=1geb1qiwcx3b"),
  freiburg_year("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=16&zx=v9yov3sfksqb"),
  freiburg_lastyear("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=25&zx=poa3vq4juvf1");
  private String url;

  private DiagramEnum(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

}
