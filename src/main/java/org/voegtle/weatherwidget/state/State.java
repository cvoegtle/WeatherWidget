package org.voegtle.weatherwidget.state;

import org.voegtle.weatherwidget.util.DateUtil;

import java.util.Date;

public class State {
  private int id;
  private boolean expanded;
  private Date age;
  private String statistics;

  public State(int id) {
    this.id = id;
  }

  public boolean outdated() {
    Date today = DateUtil.getToday();
    return age == null || age.before(today);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }

  public Date getAge() {
    return age;
  }

  public void setAge(Date age) {
    this.age = age;
  }

  public String getStatistics() {
    return statistics;
  }

  public void setStatistics(String statistics) {
    this.statistics = statistics;
  }
}
