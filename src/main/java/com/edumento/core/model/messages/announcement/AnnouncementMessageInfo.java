package com.edumento.core.model.messages.announcement;

import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;

public class AnnouncementMessageInfo extends SimpleModel {

	private From from;
	private String announcementId;

	public AnnouncementMessageInfo() {
	}

	public AnnouncementMessageInfo(Long id, String announcementId, String name, From from) {
		super(id, name);
		this.from = from;
		this.announcementId = announcementId;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public String getAnnouncementId() {
		return announcementId;
	}

	public void setAnnouncementId(String announcementId) {
		this.announcementId = announcementId;
	}
}
