package org.voegtle.weatherwidget.data;

import android.support.annotation.NonNull;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.util.DateUtil;

import java.util.Date;

public class WeatherData implements Comparable<WeatherData> {
  private LocationIdentifier location;
  private Date timestamp;
  private Float temperature;
  private Float insideTemperature;
  private Float humidity;
  private Float insideHumidity;
  private Float rain;
  private Float rainToday;
  private boolean raining;
  private Float watt;


  public WeatherData(LocationIdentifier location) {
    this.location = location;
  }


  public Float getRainToday() {
    return rainToday;
  }

  public void setRainToday(Float rainToday) {
    this.rainToday = rainToday;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public Float getTemperature() {
    return temperature;
  }

  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }

  public Float getHumidity() {
    return humidity;
  }

  public void setHumidity(Float humidity) {
    this.humidity = humidity;
  }

  public Float getRain() {
    return rain;
  }

  public void setRain(Float rain) {
    this.rain = rain;
  }


  public boolean isRaining() {
    return raining;
  }

  public void setRaining(boolean raining) {
    this.raining = raining;
  }

  public LocationIdentifier getLocation() {
    return location;
  }


  public void setInsideTemperature(Float insideTemperature) {
    this.insideTemperature = insideTemperature;
  }

  public Float getInsideTemperature() {
    return insideTemperature;
  }

  public Float getInsideHumidity() {
    return insideHumidity;
  }

  public void setInsideHumidity(Float insideHumidity) {
    this.insideHumidity = insideHumidity;
  }

  public Float getWatt() {
    return watt;
  }

  public void setWatt(Float watt) {
    this.watt = watt;
  }

  @Override
  public int compareTo(@NonNull WeatherData another) {
    if (getTemperature() == null) {
      return -1;
    }
    if (another.getTemperature() == null) {
      return 1;
    }

    Integer outdated = DateUtil.checkIfOutdated(getTimestamp(), another.getTimestamp());
    if (outdated != null) {
      return outdated;
    }

    return getTemperature().compareTo(another.getTemperature());
  }
}
