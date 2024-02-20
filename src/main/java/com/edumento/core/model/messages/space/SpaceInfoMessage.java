package com.edumento.core.model.messages.space;

import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;

public class SpaceInfoMessage extends SimpleModel {

	private String image;
	private From from;
	private String categoryName;
	private String categoryNameAR;
	private Boolean isPrivate;
	private String chatId;

	public SpaceInfoMessage() {
	}

	public SpaceInfoMessage(Long id, String name, String image, From from, String catgoryName, String categoryNameAR,
			Boolean isPrivate, String chatid) {
		super(id, name);
		this.image = image;
		this.from = from;
		categoryName = catgoryName;
		this.categoryNameAR = categoryNameAR;
		this.isPrivate = isPrivate;
		chatId = chatid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return String.format("SpaceInfoMessage [image=%s, from=%s, categoryName=%s, isPrivate=%s]", image, from,
				categoryName, isPrivate);
	}

	public String getCategoryNameAR() {
		return categoryNameAR;
	}

	public void setCategoryNameAR(String categoryNameAR) {
		this.categoryNameAR = categoryNameAR;
	}
}
