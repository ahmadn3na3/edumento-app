package com.edumento.space.model.space.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/16/16. */
public class SpaceShareModel {
	@NotNull
	private Long spaceId;

	private List<SpaceRoleModel> users = new ArrayList<>();

	private List<SpaceRoleModel> groups = new ArrayList<>();

	private boolean unShare;

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	public List<SpaceRoleModel> getUsers() {
		return users;
	}

	public void setUsers(List<SpaceRoleModel> users) {
		this.users = users;
	}

	public boolean isUnShare() {
		return unShare;
	}

	public void setUnShare(boolean unShare) {
		this.unShare = unShare;
	}

	public List<SpaceRoleModel> getGroups() {
		return groups;
	}

	public void setGroups(List<SpaceRoleModel> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "SpaceShareeModel{" + "spaceId=" + spaceId + ", users=" + users + ", groups=" + groups + ", unShare="
				+ unShare + '}';
	}
}
