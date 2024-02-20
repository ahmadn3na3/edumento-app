package com.edumento.core.model.messages;

import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.CurrentUserDetail;

/** Created by ayman on 22/01/17. */
public class From extends SimpleModel {
	private String image;
	private String chatId;

	public From() {
	}

	public From(UserInfoMessage userInfoMessage) {
		this(userInfoMessage.getId(), userInfoMessage.getName(), userInfoMessage.getImage(),
				userInfoMessage.getChatId());
	}

	public From(CurrentUserDetail userDetail) {
		setId(userDetail.getId());
		setName(userDetail.getFullName());
		setImage(userDetail.getImage());
	}

	public From(long id, String name) {
		super(id, name);
	}

	public From(long id, String name, String image, String chatId) {
		this(id, name);
		this.image = image;
		setChatId(chatId);
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return String.format("From [id=%s, name=%s, image=%s]", getId(), getName(), image);
	}
}
