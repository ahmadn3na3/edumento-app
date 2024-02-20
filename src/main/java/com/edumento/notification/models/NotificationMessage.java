package com.edumento.notification.models;

import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.notification.domian.Notification;

/** Created by ayman on 20/09/16. */
public class NotificationMessage extends BaseNotificationMessage {

  private String id;
  private Long userId;
  private String message;
  private String body;


  public NotificationMessage() {}

  public NotificationMessage(BaseNotificationMessage baseNotificationMessage) {
    this.setNotificationCategory(baseNotificationMessage.getNotificationCategory());
    this.setDate(baseNotificationMessage.getDate());
    this.setTarget(baseNotificationMessage.getTarget());
    this.setFrom(baseNotificationMessage.getFrom());
  }

  public NotificationMessage(Notification notification) {
	this.id = notification.getId();
    this.setMessage(notification.getMessage());
    this.setUserId(notification.getUserId());
    this.setFrom(notification.getFrom());
    this.setTarget(notification.getTarget());
    this.setDate(notification.getReceivedOn());
    this.setNotificationCategory(notification.getNotificationCategory());
    this.setBody(notification.getBody());
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return String.format("NotificationMessage [id=%s, userId=%s, message=%s]", id, userId, message);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
		return true;
	}
    if (!super.equals(obj) || (getClass() != obj.getClass())) {
		return false;
	}
    NotificationMessage other = (NotificationMessage) obj;
    if (id == null) {
      if (other.id != null) {
		return false;
	}
    } else if (!id.equals(other.id)) {
		return false;
	}
    if (message == null) {
      if (other.message != null) {
		return false;
	}
    } else if (!message.equals(other.message)) {
		return false;
	}
    if (userId == null) {
        return other.userId == null;
    } else return userId.equals(other.userId);
  }

public String getBody() {
	return body;
}

public void setBody(String body) {
	this.body = body;
}
}
