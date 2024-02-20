package com.edumento.user.model.user;

import com.edumento.user.constant.UserType;

/** Created by ayman on 16/06/16. */
public class UserSearchModel {
	private Long roleId;
	private UserType userType;
	private Long organizationId;
	private Long foundationId;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
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
		return "UserSearchModel{" + "roleId=" + roleId + ", userType=" + userType + ", organizationId=" + organizationId
				+ ", foundationId=" + foundationId + '}';
	}
}
