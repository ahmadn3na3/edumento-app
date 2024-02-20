package com.edumento.core.model.messages.category;

import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;

public class CategoryMessageInfo extends SimpleModel {

	private Long organizationId;
	private Long foundationId;
	private From from;

	public CategoryMessageInfo() {
	}

	public CategoryMessageInfo(Long id, String name, Long organizationId, Long foundationId, From from) {
		super(id, name);
		this.organizationId = organizationId;
		this.foundationId = foundationId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public Long getFoundationId() {
		return foundationId;
	}

	public void setFoundationId(Long foundationId) {
		this.foundationId = foundationId;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}
}
