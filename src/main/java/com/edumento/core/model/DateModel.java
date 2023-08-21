package com.edumento.core.model;

import java.time.ZonedDateTime;
import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 5/15/16. */
public class DateModel {
  @NotNull private ZonedDateTime date;

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "DateModel{" + "date=" + date + '}';
  }
}
