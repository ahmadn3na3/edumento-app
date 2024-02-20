package com.edumento.core.constants;

import java.util.Calendar;

public enum WeekDay {
	SUN(Calendar.SUNDAY), MON(Calendar.MONDAY), TUE(Calendar.TUESDAY), WEN(Calendar.WEDNESDAY), THU(Calendar.THURSDAY),
	FRI(Calendar.FRIDAY), SAT(Calendar.SATURDAY);

	private final int day;

	WeekDay(int day) {
		this.day = day;
	}

	public static WeekDay valueOf(int day) {
		return switch (day) {
		case Calendar.SUNDAY -> SUN;
		case Calendar.MONDAY -> MON;
		case Calendar.TUESDAY -> TUE;
		case Calendar.WEDNESDAY -> WEN;
		case Calendar.THURSDAY -> THU;
		case Calendar.FRIDAY -> FRI;
		case Calendar.SATURDAY -> SAT;
		default -> null;
		};
	}

	public int getDay() {
		return day;
	}
}
