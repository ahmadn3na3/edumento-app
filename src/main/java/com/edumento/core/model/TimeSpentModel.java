package com.edumento.core.model;

import java.util.Date;

/** Created by ahmad on 22/02/17. */
public class TimeSpentModel {
  private Date startDateTime;
  private Date endTime;
  private Long timeSpent;

  public Date getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(Date startDateTime) {
    this.startDateTime = startDateTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Long getTimeSpent() {
    return timeSpent;
  }

  public void setTimeSpent(Long timeSpent) {
    this.timeSpent = timeSpent;
  }
}
