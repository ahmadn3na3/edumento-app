package com.edumento.core.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** Created by ayman on 14/03/17. */
public class PageRequestModel extends PageRequest {

	private PageRequestModel() {
		super(0, Integer.MAX_VALUE, Sort.unsorted());
	}

	public static PageRequest getPageRequestModel(Integer page, Integer size) {
		return getPageRequestModel(page, size, Sort.unsorted());
	}

	public static PageRequest getPageRequestModel(Integer page, Integer size, Sort sort) {
		var pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.unsorted());
		if (null != page && null != size && sort != null) {
			pageRequest = PageRequest.of(page > 0 ? page : 0, size, sort);

		} else if (null != page && null != size) {
			pageRequest = PageRequest.of(page > 0 ? page : 0, size);
		} else if (null == page && null == size && sort != null) {
			pageRequest = PageRequest.of(0, Integer.MAX_VALUE, sort);
		}
		return pageRequest;
	}
}
