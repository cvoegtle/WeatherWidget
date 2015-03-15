package org.voegtle.weatherwidget.data;

public class StatisticsSet {
  Statistics.TimeRange range;
  Float rain;
  Float maxTemperature;
  Float minTemperature;

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

  public Statistics.TimeRange getRange() {
    return range;
  }
}
