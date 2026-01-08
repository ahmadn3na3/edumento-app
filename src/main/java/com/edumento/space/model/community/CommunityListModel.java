package com.edumento.space.model.community;

import com.edumento.user.model.user.UserInfoModel;
import java.util.HashSet;
import java.util.Set;

/** Created by ahmad on 7/25/16. */
public class CommunityListModel {
  Set<UserInfoModel> userInfoModels = new HashSet<>();

  public Set<UserInfoModel> getUserInfoModels() {
    return userInfoModels;
  }

  public void setUserInfoModels(Set<UserInfoModel> userInfoModels) {
    this.userInfoModels = userInfoModels;
  }

  @Override
  public String toString() {
    return "CommunityListModel{" + "userInfoModels=" + userInfoModels + '}';
  }
}
