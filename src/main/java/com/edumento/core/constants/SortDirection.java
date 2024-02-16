package com.edumento.core.constants;

import org.springframework.data.domain.Sort;

public enum SortDirection {
	ASCENDING(Sort.Direction.ASC), DESCENDING(Sort.Direction.DESC);

	private final Sort.Direction value;

	SortDirection(Sort.Direction value) {
		this.value = value;
	}

	public Sort.Direction getValue() {
		return value;
	}
}


