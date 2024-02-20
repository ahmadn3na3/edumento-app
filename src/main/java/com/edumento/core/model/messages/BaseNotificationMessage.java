package com.edumento.core.model.messages;

import java.time.ZonedDateTime;
import java.util.Objects;

/** Created by ahmad on 2/8/17. */
public class BaseNotificationMessage {

  private ZonedDateTime date = ZonedDateTime.now();
  private int notificationCategory;
  private From from;
  private Target target;

  public BaseNotificationMessage() {}

  public BaseNotificationMessage(
      ZonedDateTime date, int notificationCategory, From from, Target target) {
    this.date = date;
    this.notificationCategory = notificationCategory;
    this.from = from;
    this.target = target;
  }

  public BaseNotificationMessage(int notificationCategory, From from, Target target) {

    this.notificationCategory = notificationCategory;
    this.from = from;
    this.target = target;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public int getNotificationCategory() {
    return notificationCategory;
  }

  public void setNotificationCategory(int notificationCategory) {
    this.notificationCategory = notificationCategory;
  }

  public From getFrom() {
    return from;
  }

  public void setFrom(From from) {
    this.from = from;
  }

  public Target getTarget() {
    return target;
  }

  public void setTarget(Target target) {
    this.target = target;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
		return true;
	}
    if (o == null || getClass() != o.getClass()) {
		return false;
	}

    BaseNotificationMessage that = (BaseNotificationMessage) o;

    if ((notificationCategory != that.notificationCategory) || (!Objects.equals(date, that.date)) || !from.equals(that.from)) {
		return false;
	}
    return target.equals(that.target);
  }

  @Override
  public int hashCode() {
    int result = date != null ? date.hashCode() : 0;
    result = 31 * result + notificationCategory;
    result = 31 * result + from.hashCode();
    result = 31 * result + target.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "BaseNotificationMessage{"
        + "date="
        + date
        + ", notificationCategory="
        + notificationCategory
        + ", from="
        + from
        + ", target="
        + target
        + '}';
  }
}
