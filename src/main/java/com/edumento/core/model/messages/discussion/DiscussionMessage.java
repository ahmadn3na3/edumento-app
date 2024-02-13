package com.edumento.core.model.messages.discussion;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.edumento.core.constants.DiscussionType;
import com.edumento.core.model.messages.From;

public class DiscussionMessage {
	private String id;
	private String title;
	private String body;
	private Long spaceId;
	private String spaceName;
	private String categoryName;
	private String resourceUrl;
	private From from;
	private DiscussionType type;

	public DiscussionMessage() {
	}

	public DiscussionMessage(String id, String title, Long spaceId, String spaceName, String categoryName, From from,
			DiscussionType type) {
		this.id = id;
		this.title = title;
		this.spaceId = spaceId;
		this.spaceName = spaceName;
		this.categoryName = categoryName;
		this.from = from;
		this.type = type;
	}

	public DiscussionMessage(String id, String title, Long spaceId, String spaceName, String categoryName, From from,
			String resourceUrl, String body, DiscussionType type) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.spaceId = spaceId;
		this.spaceName = spaceName;
		this.categoryName = categoryName;
		this.from = from;
		this.resourceUrl = resourceUrl;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public DiscussionType getType() {
		return type;
	}

	public void setType(DiscussionType type) {
		this.type = type;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("title", title).append("spaceId", spaceId)
				.append("spaceName", spaceName).append("categoryName", categoryName).append("from", from).toString();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
