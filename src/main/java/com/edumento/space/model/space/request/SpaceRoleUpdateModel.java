package com.edumento.space.model.space.request;

import com.edumento.core.constants.SpaceRole;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 6/12/16. */
public class SpaceRoleUpdateModel {

  @NotNull private Long userId;
  @NotNull private Long spaceId;
  @NotNull private SpaceRole spaceRole;

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public SpaceRole getSpaceRole() {
    return spaceRole;
  }

  public void setSpaceRole(SpaceRole spaceRole) {
    this.spaceRole = spaceRole;
  }

  @Override
  public String toString() {
    return String.format(
        "SpaceRoleUpdateModel{userId=%d, spaceId=%d, spaceRole=%s}", userId, spaceId, spaceRole);
  }
}
