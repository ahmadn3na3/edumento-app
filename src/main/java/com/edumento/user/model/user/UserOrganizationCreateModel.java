package com.edumento.user.model.user;

import java.util.List;

/** Created by ahmad on 2/29/16. */
public class UserOrganizationCreateModel extends UserCreateModel {

	private Long roleId;
	private Long organizationId;
	private Long foundationId;
	private List<Long> groups;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public List<Long> getGroups() {
		return groups;
	}

	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}

	public Long getFoundationId() {
		return foundationId;
	}

	public void setFoundationId(Long foundationId) {
		this.foundationId = foundationId;
	}

	@Override
	public String toString() {
		return "UserOrganizationCreateModel{" + "roleId=" + roleId + ", organizationId=" + organizationId + ", groups="
				+ groups + "} " + super.toString();
	}
}
