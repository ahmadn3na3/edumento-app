package com.edumento.notification.models;

/** Created by ayman on 01/08/17. */
public class UserRoleModel {
	private Long roleId;
	private Long userId;

	public UserRoleModel() {
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

	public void setUserId(Long userIdl) {
		userId = userIdl;
	}

	@Override
	public String toString() {
		return "UserRoleModel{" + "roleId=" + roleId + ", userId=" + userId + '}';
	}
}
