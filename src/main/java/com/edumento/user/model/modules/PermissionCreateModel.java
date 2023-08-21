package com.edumento.user.model.modules;

import com.edumento.user.constant.UserType;
import com.edumento.user.domain.Module;

public class PermissionCreateModel {
	private String name;
	private String keyCode;
	private Integer code;
	private UserType type;

	public PermissionCreateModel() {
		// TODO Auto-generated constructor stub
	}

	public PermissionCreateModel(String name, String keyCode, Integer code) {
		super();
		this.name = name;
		this.keyCode = keyCode;
		this.code = code;
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PermissionCreateModel that = (PermissionCreateModel) o;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
