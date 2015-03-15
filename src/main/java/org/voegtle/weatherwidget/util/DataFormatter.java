package org.voegtle.weatherwidget.util;

import android.content.res.Resources;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DataFormatter {
  private DecimalFormat numberFormat;
  private Resources res;

  public DataFormatter() {
    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");
  }

  public DataFormatter(Resources res) {
    this();
    this.res = res;
  }

  public String formatForActivity(WeatherData data) {
    StringBuilder builder = new StringBuilder();
    builder.append(res.getString(R.string.temperature)).append(" ");
    builder.append(formatTemperature(data.getTemperature()));
    if (data.getInsideTemperature() != null) {
      builder.append(" / ");
      builder.append(formatTemperature(data.getInsideTemperature()));
    }
    builder.append("\n");

    builder.append(res.getString(R.string.humidity)).append(" ").append(formatPercent(data.getHumidity()));
    if (data.getInsideHumidity() != null) {
      builder.append(" / ").append(formatPercent(data.getInsideHumidity()));
    }
    if (data.getRain() != null) {
      builder.append("\n");
      builder.append(res.getString(R.string.rain_last_hour)).append(" ").append(formatRain(data.getRain()));
    }
    if (data.getRainToday() != null) {
      builder.append("\n");
      builder.append(res.getString(R.string.rain_today)).append(" ").append(formatRain(data.getRainToday()));
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

  public String formatRain(Float rain) {
    if (rain != null) {
      return numberFormat.format(rain) + "l";
    } else {
      return "";
    }
  }

  public String formatPercent(Float val) {
    if (val != null) {
      return numberFormat.format(val) + "%";
    } else {
      return "";
    }
  }

}
