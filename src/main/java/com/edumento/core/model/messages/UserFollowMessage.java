package com.edumento.core.model.messages;

import com.edumento.core.model.messages.user.UserInfoMessage;
import java.io.Serializable;

public class UserFollowMessage implements Serializable {

  /** */
  private static final long serialVersionUID = -7494329947330927633L;

  private UserInfoMessage userInfoMessage;
  private UserInfoMessage followerInfoMessage;

  public UserFollowMessage() {
    // TODO Auto-generated constructor stub
  }

  public UserFollowMessage(UserInfoMessage userInfoMessage, UserInfoMessage followerInfoMessage) {
    this.userInfoMessage = userInfoMessage;
    this.followerInfoMessage = followerInfoMessage;
  }

  public UserInfoMessage getUserInfoMessage() {
    return userInfoMessage;
  }

  public void setUserInfoMessage(UserInfoMessage userInfoMessage) {
    this.userInfoMessage = userInfoMessage;
  }

  public UserInfoMessage getFollowerInfoMessage() {
    return followerInfoMessage;
  }

  public void setFollowerInfoMessage(UserInfoMessage followerInfoMessage) {
    this.followerInfoMessage = followerInfoMessage;
  }
}
