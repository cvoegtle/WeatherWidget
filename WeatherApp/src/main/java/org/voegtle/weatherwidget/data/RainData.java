package org.voegtle.weatherwidget.data;

public class RainData {
  private Float rainLastHour;
  private Float rainToday;
  private Float rainYeasterday;
  private Float rainLastWeek;
  private Float rain30Days;

  public RainData() {
  }

  public Float getRainLastHour() {
    return rainLastHour;
  }

  public void setRainLastHour(Float rainLastHour) {
    this.rainLastHour = rainLastHour;
  }

  public Float getRainToday() {
    return rainToday;
  }

  public void setRainToday(Float rainToday) {
    this.rainToday = rainToday;
  }

  public Float getRainYeasterday() {
    return rainYeasterday;
  }

  public void setRainYeasterday(Float rainYeasterday) {
    this.rainYeasterday = rainYeasterday;
  }

  public Float getRainLastWeek() {
    return rainLastWeek;
  }

  public void setRainLastWeek(Float rainLastWeek) {
    this.rainLastWeek = rainLastWeek;
  }

  public Float getRain30Days() {
    return rain30Days;
  }

  public void setRain30Days(Float rain30Days) {
    this.rain30Days = rain30Days;
  }
}
