package com.edumento.core.constants;

import java.util.Calendar;

public enum WeekDay {
  SUN(Calendar.SUNDAY),
  MON(Calendar.MONDAY),
  TUE(Calendar.TUESDAY),
  WEN(Calendar.WEDNESDAY),
  THU(Calendar.THURSDAY),
  FRI(Calendar.FRIDAY),
  SAT(Calendar.SATURDAY);
  private final int day;

  WeekDay(int day) {
    this.day = day;
  }

  public static WeekDay valueOf(int day) {
    switch (day) {
      case Calendar.SUNDAY:
        return SUN;
      case Calendar.MONDAY:
        return MON;
      case Calendar.TUESDAY:
        return TUE;
      case Calendar.WEDNESDAY:
        return WEN;
      case Calendar.THURSDAY:
        return THU;
      case Calendar.FRIDAY:
        return FRI;
      case Calendar.SATURDAY:
        return SAT;
      default:
        return null;
    }
  }

  public int getDay() {
    return day;
  }
}
