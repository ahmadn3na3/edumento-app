package com.edumento.b2b.model.timelock;

public class TimeModel {

  private String fromTime;
  private String toTime;

  public TimeModel() {}

  public TimeModel(String fromTime, String toTime) {
    this.fromTime = fromTime;
    this.toTime = toTime;
  }

  public String getFromTime() {
    return fromTime;
  }

  public void setFromTime(String fromTime) {
    this.fromTime = fromTime;
  }

  public String getToTime() {
    return toTime;
  }

  public void setToTime(String toTime) {
    this.toTime = toTime;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("%s>%s", fromTime, toTime);
  }
}
