package com.edumento.user.model.account;

import com.edumento.user.model.user.UserCreateModel;

import jakarta.validation.constraints.Size;

public class KeyAndPasswordDTO {

	private String key;

	@Size(min = UserCreateModel.PASSWORD_MIN_LENGTH, max = UserCreateModel.PASSWORD_MAX_LENGTH, message = "error.password.length")
	private String newPassword;

	public KeyAndPasswordDTO() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public String toString() {
		return String.format("KeyAndPasswordDTO{key='%s', newPassword='%s'}", key, newPassword);
	}
}
