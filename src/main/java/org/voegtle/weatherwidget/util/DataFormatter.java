package org.voegtle.weatherwidget.util;

import android.content.res.Resources;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.RainData;
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
    builder.append(numberFormat.format(data.getTemperature())).append("°C");
    if (data.getInsideTemperature() != null) {
      builder.append(" / ");
      builder.append(numberFormat.format(data.getInsideTemperature())).append("°C");
    }
    builder.append("\n");

    builder.append(res.getString(R.string.humidity)).append(" ").append(numberFormat.format(data.getHumidity())).append("%");
    if (data.getInsideHumidity() != null) {
      builder.append(" / ").append(numberFormat.format(data.getInsideHumidity())).append("%");
    }
    if (data.getRain() != null) {
      builder.append("\n");
      builder.append(res.getString(R.string.rain_last_hour)).append(" ").append(numberFormat.format(data.getRain())).append(res.getString(R.string.liter));
    }
    if (data.getRainToday() != null) {
      builder.append("\n");
      builder.append(res.getString(R.string.rain_today)).append(" ").append(numberFormat.format(data.getRainToday())).append(res.getString(R.string.liter));
    }
    return builder.toString();
  }

  public String formatWidgetLine(WeatherLocation location, WeatherData data, boolean detailed) {
    StringBuilder weatherData = new StringBuilder(location.getShortName() + " "
        + formatTemperature(data));
    if (detailed) {
      weatherData.append(" | ");
      weatherData.append(numberFormat.format(data.getHumidity()));
      weatherData.append("%");
      if (data.getRainToday() != null) {
        weatherData.append(" | ");
        weatherData.append(numberFormat.format(data.getRainToday()));
        weatherData.append("l");
      }
    }

    return weatherData.toString();
  }

  public String formatTemperature(WeatherData data) {
    String formattedTemperature;
    Float temperature = data.getTemperature();
    if (temperature != null) {
      formattedTemperature = numberFormat.format(temperature) + "°C";
    } else {
      formattedTemperature = "-";
    }
    return formattedTemperature;
  }

  public String formatRainData(RainData rainData) {
    final StringBuilder builder = new StringBuilder();

    builder.append(buildRainString(res.getString(R.string.rain_yesterday), rainData.getRainYeasterday())).append("\n");
    builder.append(buildRainString(res.getString(R.string.rain_last_week), rainData.getRainLastWeek())).append("\n");
    builder.append(buildRainString(res.getString(R.string.rain_30_days), rainData.getRain30Days()));

    return builder.toString();
  }

  private String buildRainString(String text, Float value) {
    StringBuilder builder = new StringBuilder();

    builder.append(text);
    builder.append(" ");
    if (value != null) {
      builder.append(numberFormat.format(value));
      builder.append(res.getString(R.string.liter));
    }
    return builder.toString();
  }
}
