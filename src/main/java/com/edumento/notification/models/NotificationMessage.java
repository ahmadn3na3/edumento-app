package com.edumento.notification.models;

import java.util.Objects;

import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.notification.domian.Notification;

/** Created by ayman on 20/09/16. */
public class NotificationMessage extends BaseNotificationMessage {

	private String id;
	private Long userId;
	private String message;
	private String body;

	public NotificationMessage() {
	}

	public NotificationMessage(BaseNotificationMessage baseNotificationMessage) {
		setNotificationCategory(baseNotificationMessage.getNotificationCategory());
		setDate(baseNotificationMessage.getDate());
		setTarget(baseNotificationMessage.getTarget());
		setFrom(baseNotificationMessage.getFrom());
	}

	public NotificationMessage(Notification notification) {
		id = notification.getId();
		setMessage(notification.getMessage());
		setUserId(notification.getUserId());
		setFrom(notification.getFrom());
		setTarget(notification.getTarget());
		setDate(notification.getReceivedOn());
		setNotificationCategory(notification.getNotificationCategory());
		setBody(notification.getBody());
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
		final var prime = 31;
		var result = super.hashCode();
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (message == null ? 0 : message.hashCode());
		return prime * result + (userId == null ? 0 : userId.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj) || getClass() != obj.getClass()) {
			return false;
		}
		var other = (NotificationMessage) obj;
		if (!Objects.equals(id, other.id)) {
			return false;
		}
		if (!Objects.equals(message, other.message)) {
			return false;
		}
		if (userId == null) {
			return other.userId == null;
		} else {
			return userId.equals(other.userId);
		}
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
