package com.edumento.b2b.model.role;

import java.util.HashMap;
import java.util.Map;

import com.edumento.user.constant.UserType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/29/16. */
public class RoleCreateModel {
	@NotNull
	@NotEmpty
	private String name;
	private Map<String, Byte> permission = new HashMap<>();
	private UserType type;
	private Long foundationId;
	private Long organizationId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Byte> getPermission() {
		return permission;
	}

	public void setPermission(Map<String, Byte> permission) {
		this.permission = permission;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public Long getFoundationId() {
		return foundationId;
	}

	public void setFoundationId(Long foundationId) {
		this.foundationId = foundationId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	@Override
	public String toString() {
		return "RoleCreateModel{" + "name='" + name + '\'' + ", permission=" + permission + ", type=" + type
				+ ", foundationId=" + foundationId + ", organizationId=" + organizationId + '}';
	}
}
