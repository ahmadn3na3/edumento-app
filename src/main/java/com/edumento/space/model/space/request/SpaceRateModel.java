package com.edumento.space.model.space.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 4/11/16. */
public class SpaceRateModel {
	@NotNull(message = "error.key.invalid")
	private Long spaceId;

	@NotNull(message = "error.rating.min")
	@Min(value = 1, message = "error.rating.min")
	private Integer rating;

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "SpaceRateModel{" + "spaceId=" + spaceId + ", rating=" + rating + '}';
	}
}
