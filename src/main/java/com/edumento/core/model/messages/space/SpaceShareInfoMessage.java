package com.edumento.core.model.messages.space;

import java.util.HashSet;
import java.util.Set;
import com.edumento.core.model.messages.From;

public class SpaceShareInfoMessage extends SpaceInfoMessage {

  private Set<Long> userIds = new HashSet<>();

  public SpaceShareInfoMessage() {
    // TODO Auto-generated constructor stub
  }

  public SpaceShareInfoMessage(
      Long id,
      String name,
      String image,
      From from,
      String catgoryName,
      String catgoryNameAR,
      Boolean isPrivate,
      Set<Long> userIds,
      String chatId) {
    super(id, name, image, from, catgoryName, catgoryNameAR, isPrivate, chatId);
    this.userIds = userIds;
  }

  public Set<Long> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<Long> userIds) {
    this.userIds = userIds;
  }
}
