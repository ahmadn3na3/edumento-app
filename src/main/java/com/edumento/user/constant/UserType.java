package com.edumento.user.constant;

public enum UserType {
	SYSTEM_ADMIN, USER, SUPER_ADMIN;

	public String getAuthority() {
		return name();
	}
}
