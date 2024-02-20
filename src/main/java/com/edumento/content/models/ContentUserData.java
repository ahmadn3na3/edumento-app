package com.edumento.content.models;

import com.edumento.core.model.TimeSpentModel;

/** Created by ahmad on 7/20/16. */
public class ContentUserData {
	private Boolean favorite;

	private TimeSpentModel timeSpent;

	public Boolean getFavorite() {
		return favorite;
	}

	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}

	public TimeSpentModel getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(TimeSpentModel timeSpent) {
		this.timeSpent = timeSpent;
	}

	@Override
	public String toString() {
		return "ContentUserData{" + "favorite=" + favorite + ", timeSpent=" + timeSpent + '}';
	}
}
