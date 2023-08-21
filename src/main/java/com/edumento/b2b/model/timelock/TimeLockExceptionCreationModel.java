package com.edumento.b2b.model.timelock;

import java.time.ZonedDateTime;
import com.edumento.core.constants.LockStatus;

public class TimeLockExceptionCreationModel extends TimeModel {
  private String name;
  private ZonedDateTime fromDate;
  private ZonedDateTime toDate;
  private LockStatus lockStatus;

  public ZonedDateTime getFromDate() {
    return fromDate;
  }

  public void setFromDate(ZonedDateTime fromDate) {
    this.fromDate = fromDate;
  }

  public ZonedDateTime getToDate() {
    return toDate;
  }

  public void setToDate(ZonedDateTime toDate) {
    this.toDate = toDate;
  }

  public LockStatus getLockStatus() {
    return lockStatus;
  }

  public void setLockStatus(LockStatus lockStatus) {
    this.lockStatus = lockStatus;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "TimeLockExceptionCreationModel{" + "fromDate=" + fromDate + ", toDate=" + toDate
        + ", lockStatus=" + lockStatus + "} " + super.toString();
  }
}
