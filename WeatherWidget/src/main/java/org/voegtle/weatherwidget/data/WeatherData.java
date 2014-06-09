package org.voegtle.weatherwidget.data;

import java.util.Date;

/**
 * Created by cv on 28.05.13.
 */
public class WeatherData {
  private String location;
  private Date timestamp;
  private Float temperature;
  private Float humidity;
  private Float rain;
  private Float rainToday;
  private boolean raining;


  public Float getRainToday() {
    return rainToday;
  }

  public void setRainToday(Float rainToday) {
    this.rainToday = rainToday;
  }

  public WeatherData() {
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

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }



}
