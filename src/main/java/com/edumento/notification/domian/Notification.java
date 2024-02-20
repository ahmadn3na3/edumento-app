package com.edumento.notification.domian;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.edumento.core.domain.AbstractEntity;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.Target;

/** Created by ayman on 02/03/17. */
@Document(collection = "mint.notifications")
public class Notification extends AbstractEntity {

	@Id
	private String id;

	@Indexed
	private Long userId;

	private String message;
	private String body;

	private ZonedDateTime receivedOn;
	private int notificationCategory;
	private From from;
	private Target target;
	private Boolean received = Boolean.FALSE;
	private Boolean seen = Boolean.FALSE;

	public Notification() {
	}

	public Notification(Long userId, String message, String body, ZonedDateTime receivedOn, int notificationCategory,
			From from, Target target) {
		this.userId = userId;
		this.message = message;
		this.body = body;
		this.receivedOn = receivedOn;
		this.notificationCategory = notificationCategory;
		this.from = from;
		this.target = target;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ZonedDateTime getReceivedOn() {
		return receivedOn;
	}

	public void setReceivedOn(ZonedDateTime receivedOn) {
		this.receivedOn = receivedOn;
	}

	public int getNotificationCategory() {
		return notificationCategory;
	}

	public void setNotificationCategory(int notificationCategory) {
		this.notificationCategory = notificationCategory;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public Boolean isReceived() {
		return received;
	}

	public void setReceived(Boolean received) {
		this.received = received;
	}

	public Boolean isSeen() {
		return seen;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
