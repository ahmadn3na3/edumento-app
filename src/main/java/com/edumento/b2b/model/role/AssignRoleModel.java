package com.edumento.b2b.model.role;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 4/4/16. */
public class AssignRoleModel {
	@NotNull()
	private Long roleId;
	@NotNull()
	private Long userId;

	public AssignRoleModel() {
	}

	public AssignRoleModel(Long roleId, Long userId) {
		this.roleId = roleId;
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "AssignRoleModel{" + "roleId=" + roleId + ", userId=" + userId + '}';
	}
}
