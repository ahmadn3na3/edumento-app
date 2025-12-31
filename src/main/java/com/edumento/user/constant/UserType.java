package com.edumento.user.constant;

public enum UserType {
	SYSTEM_ADMIN, USER, SUPER_ADMIN, ADMIN;

	public String getAuthority() {
		return name();
	}
}
