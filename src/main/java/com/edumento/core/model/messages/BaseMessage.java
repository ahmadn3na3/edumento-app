package com.edumento.core.model.messages;

import com.edumento.core.constants.notification.EntityAction;

/** Created by ahmad on 5/24/17. */
public class BaseMessage {
  private EntityAction entityAction;
  private Object entityId;
  private String userName;
  private String dataModel;

  public BaseMessage() {}

  public BaseMessage(
      EntityAction entityAction, Object entityId, String userName, String dataModel) {
    this.entityAction = entityAction;
    this.entityId = entityId;
    this.userName = userName;
    this.dataModel = dataModel;
  }

  public EntityAction getEntityAction() {
    return entityAction;
  }

  public void setEntityAction(EntityAction entityAction) {
    this.entityAction = entityAction;
  }

  public Object getEntityId() {
    return entityId;
  }

  public void setEntityId(Object entityId) {
    this.entityId = entityId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getDataModel() {
    return dataModel;
  }

  public void setDataModel(String dataModel) {
    this.dataModel = dataModel;
  }

  @Override
  public String toString() {
    return "BaseMessage{"
        + "entityAction="
        + entityAction
        + ", entityId="
        + entityId
        + ", userName='"
        + userName
        + '\''
        + ", dataModel="
        + dataModel
        + '}';
  }
}
