package com.edumento.space.model.space.response;

import com.edumento.category.model.CategoryModel;

/** Created by ahmad on 3/15/16. */
public class SpaceSearchModel {
	private Long id;
	private String color;
	private String name;
	private String image;
	private String thumbnail;

	private CategoryModel category;

	private String creatorName;

	private boolean owner;
	private boolean joinRequestsAllowed;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isJoinRequestsAllowed() {
		return joinRequestsAllowed;
	}

	public void setJoinRequestsAllowed(boolean joinRequestsAllowed) {
		this.joinRequestsAllowed = joinRequestsAllowed;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public CategoryModel getCategory() {
		return category;
	}

	public void setCategory(CategoryModel category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "SpaceSearchModel{" + "id=" + id + ", color='" + color + '\'' + ", name='" + name + '\'' + ", image='"
				+ image + '\'' + ", category=" + category + ", creatorName='" + creatorName + '\'' + ", owner=" + owner
				+ '}';
	}
}
