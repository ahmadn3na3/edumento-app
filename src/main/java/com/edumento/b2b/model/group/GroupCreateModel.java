package com.edumento.b2b.model.group;

import java.util.ArrayList;
import java.util.List;

import com.edumento.core.constants.Gender;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/8/16. */
public class GroupCreateModel {

	@NotNull(message = "error.group.name.null")
	private String name;

	private Gender gender;
	private Long organizationId;
	private Long foundationId;

	private List<String> tags = new ArrayList<>();

	private List<String> canAccess = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getCanAccess() {
		return canAccess;
	}

	public void setCanAccess(List<String> canAccess) {
		this.canAccess = canAccess;
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

	@Override
	public String toString() {
		return "GroupCreateModel{" + "name='" + name + '\'' + ", gender=" + gender + ", organizationId="
				+ organizationId + ", foundationId=" + foundationId + ", tags=" + tags + ", canAccess=" + canAccess
				+ '}';
	}
}
