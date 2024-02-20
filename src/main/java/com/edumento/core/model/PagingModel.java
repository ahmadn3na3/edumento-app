package com.edumento.core.model;

import java.time.ZonedDateTime;

import com.edumento.core.constants.SortDirection;
import com.edumento.core.constants.SortField;

/** Created by ahmad on 4/17/16. */
public class PagingModel {
	private Integer page = 0;
	private Integer pageSize = 20;
	private SortDirection sortDirection = SortDirection.DESCENDING;
	private SortField sortProperty = SortField.CREATION_DATE;
	private Long lastRecordId = 0L;
	private ZonedDateTime lastRequestDate;

	public PagingModel() {
	}

	public PagingModel(SortField sortProperty) {
		this.sortProperty = sortProperty;
	}

	public PagingModel(Integer pageSize, SortField sortProperty) {
		this.pageSize = pageSize;
		this.sortProperty = sortProperty;
	}

	public PagingModel(SortField sortProperty, SortDirection sortDirection) {
		this.sortProperty = sortProperty;
		this.sortDirection = sortDirection;
	}

	public PagingModel(Integer pageSize, SortDirection sortDirection, SortField sortProperty) {
		this.pageSize = pageSize;
		this.sortDirection = sortDirection;
		this.sortProperty = sortProperty;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		if (page > 0) {
			this.page = page - 1;
		} else {
			this.page = 0;
		}
	}

	public Integer getPageSize() {

		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public SortField getSortProperty() {
		return sortProperty;
	}

	public void setSortProperty(SortField sortProperty) {
		this.sortProperty = sortProperty;
	}

	public Long getLastRecordId() {
		return lastRecordId;
	}

	public void setLastRecordId(Long lastRecordId) {
		this.lastRecordId = lastRecordId;
	}

	public ZonedDateTime getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(ZonedDateTime lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	@Override
	public String toString() {
		return "PagingModel{" + "page=" + page + ", pageSize=" + pageSize + ", sortDirection=" + sortDirection
				+ ", sortProperty=" + sortProperty + ", lastRecordId=" + lastRecordId + ", lastRequestDate="
				+ lastRequestDate + '}';
	}
}
