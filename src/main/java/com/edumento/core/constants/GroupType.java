package com.edumento.core.constants;

/** Created by ahmad on 3/7/16. */
public enum GroupType {
	ORGANIZATION("ORGANIZATION"), SPACE("SPACE"), ORGANIZATION_USER("ORGANIZATION_USER"), SPACE_USER("SPACE_USER");

	private final String value;

	GroupType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
