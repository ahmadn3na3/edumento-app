package com.edumento.space.model.community;

import java.util.HashSet;
import java.util.Set;

import com.edumento.b2b.model.group.GroupModel;
import com.edumento.user.model.user.UserInfoModel;

/** Created by ahmad on 7/25/16. */
public class CommunityListModel {
	Set<com.edumento.user.model.user.UserInfoModel> userInfoModels = new HashSet<>();
	Set<com.edumento.b2b.model.group.GroupModel> groupModels = new HashSet<>();

	public Set<UserInfoModel> getUserInfoModels() {
		return userInfoModels;
	}

	public void setUserInfoModels(Set<UserInfoModel> userInfoModels) {
		this.userInfoModels = userInfoModels;
	}

	public Set<GroupModel> getGroupModels() {
		return groupModels;
	}

	public void setGroupModels(Set<GroupModel> groupModels) {
		this.groupModels = groupModels;
	}

	@Override
	public String toString() {
		return "CommunityListModel{" + "userInfoModels=" + userInfoModels + ", groupModels=" + groupModels + '}';
	}
}
