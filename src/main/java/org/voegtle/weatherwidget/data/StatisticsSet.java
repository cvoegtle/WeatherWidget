package org.voegtle.weatherwidget.data;

public class StatisticsSet {
  Statistics.TimeRange range;
  Float rain;
  Float maxTemperature;
  Float minTemperature;
  Float kwh;

  public StatisticsSet(Statistics.TimeRange range) {
    this.range = range;
  }

  public Float getRain() {
    return rain;
  }

  public void setRain(Float rain) {
    this.rain = rain;
  }

  public Float getMaxTemperature() {
    return maxTemperature;
  }

  public void setMaxTemperature(Float maxTemperature) {
    this.maxTemperature = maxTemperature;
  }

  public Float getMinTemperature() {
    return minTemperature;
  }

  public void setMinTemperature(Float minTemperature) {
    this.minTemperature = minTemperature;
  }

  public Float getKwh() {
    return kwh;
  }

  public void setKwh(Float kwh) {
    this.kwh = kwh;
  }

  public Statistics.TimeRange getRange() {
    return range;
  }
}
