package com.edumento.user.constant;

public enum UserType {
	SYSTEM_ADMIN, USER, FOUNDATION_ADMIN, ADMIN, SUPER_ADMIN;

	public String getAuthority() {
		return name();
	}
}
