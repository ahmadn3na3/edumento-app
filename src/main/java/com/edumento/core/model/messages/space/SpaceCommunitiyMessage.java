package com.edumento.core.model.messages.space;

import java.util.List;

import com.edumento.core.model.messages.BaseNotificationMessage;

/** Created by ahmad on 2/7/17. */
public class SpaceCommunitiyMessage extends BaseNotificationMessage {

  private List<String> userNames;

  public List<String> getUserNames() {
    return userNames;
  }

  public void setUserNames(List<String> userNames) {
    this.userNames = userNames;
  }
}
