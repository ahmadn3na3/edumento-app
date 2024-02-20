package com.edumento.b2b.model.timelock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.edumento.core.constants.WeekDay;

public class DayModel {
	private WeekDay day;
	private List<TimeModel> timeModels = new ArrayList<>();

	public DayModel() {
	}

	public DayModel(WeekDay weekDay, String times) {
		day = weekDay;
		timeModels.addAll(Arrays.asList(times.split(",")).stream().map(new Function<String, TimeModel>() {
			@Override
			public TimeModel apply(String s) {
				var time = s.split(">");
				return new TimeModel(time[0], time[1]);
			}
		}).collect(Collectors.toList()));
	}

	/** @return the day */
	public WeekDay getDay() {
		return day;
	}

	/** @param day the day to set */
	public void setDay(WeekDay day) {
		this.day = day;
	}

	/** @return the timeModels */
	public List<TimeModel> getTimeModels() {
		return timeModels;
	}

	/** @param timeModels the timeModels to set */
	public void setTimeModels(List<TimeModel> timeModels) {
		this.timeModels = timeModels;
	}

	@Override
	public String toString() {
		return "DayModel{" + "day=" + day + ", timeModels=" + timeModels + '}';
	}
}
