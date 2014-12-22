package org.voegtle.weatherwidget.preferences;

import org.voegtle.weatherwidget.R;

public enum ColorScheme {
  dark("dark", R.style.AppTheme),
  light("light", R.style.AppThemeLight);

  private String key;
  private final int theme;

  ColorScheme(String key, int theme) {
    this.key = key;
    this.theme = theme;
  }

  public int getTheme() {
    return theme;
  }

  static ColorScheme byKey(String key) {
    for (ColorScheme colorScheme : values()) {
      if (colorScheme.key.equals(key)) {
        return colorScheme;
      }
    }
    return null;
  }

}
