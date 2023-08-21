package com.edumento.user.model.account;

import com.edumento.user.model.user.UserCreateModel;

/** Created by ahmad on 2/17/16. */
public class FoundationRegesiterAccountModel extends UserCreateModel {
	private Long foundationId;
	private Long organizationId;
	private Long groupId;
	private Long roleId;

	public Long getFoundationId() {
		return foundationId;
	}

	public void setFoundationId(Long foundationId) {
		this.foundationId = foundationId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organiatonId) {
		this.organizationId = organiatonId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
