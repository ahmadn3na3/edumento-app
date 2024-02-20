package com.edumento.b2b.model.role;

import com.edumento.user.constant.UserType;

/** Created by ayman on 21/06/16. */
public class RoleByModel {
	private Long organizationId;
	private Long foundationId;
	private UserType userType;

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public Long getFoundationId() {
		return foundationId;
	}

	public void setFoundationId(Long foundationId) {
		this.foundationId = foundationId;
	}

	@Override
	public String toString() {
		return "RoleByModel{" + "organizationId=" + organizationId + ", foundationId=" + foundationId + ", userType="
				+ userType + '}';
	}
}
