package org.voegtle.weatherwidget.util;

import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DataFormatter {
  private DecimalFormat numberFormat;

  public DataFormatter() {
    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");
  }


  public String formatTemperatureForActivity(WeatherData data) {
    StringBuilder builder = new StringBuilder();
    builder.append(formatTemperature(data.getTemperature()));
    if (data.getInsideTemperature() != null) {
      builder.append(" / ");
      builder.append(formatTemperature(data.getInsideTemperature()));
    }
    return builder.toString();
  }

  public String formatHumidityForActivity(WeatherData data) {
    StringBuilder builder = new StringBuilder();
    builder.append(formatPercent(data.getHumidity()));
    if (data.getInsideHumidity() != null) {
      builder.append(" / ");
      builder.append(formatPercent(data.getInsideHumidity()));
    }
    return builder.toString();
  }

  public String formatWidgetLine(WeatherLocation location, WeatherData data, boolean detailed) {
    StringBuilder weatherData = new StringBuilder(location.getShortName() + " "
        + formatTemperature(data));
    if (detailed) {
      weatherData.append(" | ");
      weatherData.append(formatPercent(data.getHumidity()));
      if (data.getRainToday() != null) {
        weatherData.append(" | ");
        weatherData.append(formatRain(data.getRainToday()));
      }
    }

    return weatherData.toString();
  }

  public String formatTemperature(WeatherData data) {
    String formattedTemperature;
    Float temperature = data.getTemperature();
    if (temperature != null) {
      formattedTemperature = formatTemperature(temperature);
    } else {
      formattedTemperature = "-";
    }
    return formattedTemperature;
  }

  public String formatTemperature(Float temperature) {
    if (temperature != null) {
      return numberFormat.format(temperature) + "°C";
    } else {
      return "";
    }
  }

  public String formatKwh(Float kwh) {
    if (kwh != null) {
      return numberFormat.format(kwh) + "kWh";
    } else {
      return "";
    }
  }

  public String formatRain(Float rain) {
    if (rain != null) {
      return numberFormat.format(rain) + "l";
    } else {
      return "";
    }
  }

  private String formatPercent(Float val) {
    if (val != null) {
      return numberFormat.format(val) + "%";
    } else {
      return "";
    }
  }

  public String formatWind(Float val) {
    if (val != null) {
      return numberFormat.format(val) + "km/h";
    } else {
      return "";
    }

  }

  public String formatWatt(Float watt) {
    if (watt != null) {
      return numberFormat.format(watt) + "W";
    } else {
      return "";
    }
  }
}
