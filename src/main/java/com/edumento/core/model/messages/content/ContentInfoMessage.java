package com.edumento.core.model.messages.content;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.edumento.core.constants.ContentType;
import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;

public class ContentInfoMessage extends SimpleModel {
	private ContentType contentType;
	private Long spaceId;
	private String spaceName;
	private String categoryName;
	private From from;

	public ContentInfoMessage() {
	}

	public ContentInfoMessage(Long id, String name, ContentType contentType, Long spaceId, String spaceName,
			String categoryName, From from) {
		super(id, name);
		this.contentType = contentType;
		this.spaceId = spaceId;
		this.spaceName = spaceName;
		this.categoryName = categoryName;
		this.from = from;
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

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("contentType", contentType).append("spaceId", spaceId)
				.append("spaceName", spaceName).append("categoryName", categoryName).append("from", from)
				.append(super.toString()).toString();
	}
}
