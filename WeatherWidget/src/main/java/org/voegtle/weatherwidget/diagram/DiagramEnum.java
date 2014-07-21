package org.voegtle.weatherwidget.diagram;

public enum DiagramEnum {
  temperature7days("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=22&zx=itnq9cg9itj1"),
  average7days("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=23&zx=nsc624omy43"),
  summerdays("https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=24&zx=bmq3fhig2c");

  private String url;

  private DiagramEnum(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public static DiagramEnum byName(String diagramName) {
    for (DiagramEnum diagramEnum : values()) {
      if (diagramEnum.toString().equals(diagramName)) {
        return diagramEnum;
      }
    }
    return null;
  }
}
