package com.edumento.discussion.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.edumento.core.constants.DiscussionType;
import com.edumento.core.domain.AbstractEntity;

/** Created by ayman on 25/08/16. */
@Document()
public class Discussion extends AbstractEntity {
	@Id
	private String id;

	@Indexed
	private Long spaceId;

	@Indexed
	private Long ownerId;
	@Indexed
	private DiscussionType type;
	private String userName;
	private String thumbnail;
	private String title;

	private String body;

	private String resourceUrl;
	private Long contentId;

	@DBRef
	private List<Comment> comments = new ArrayList<>();

	public Discussion() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var that = (Discussion) o;

		if ((getId() != null ? !getId().equals(that.getId()) : that.getId() != null)
				|| !getSpaceId().equals(that.getSpaceId()) || !getOwnerId().equals(that.getOwnerId())
				|| !getTitle().equals(that.getTitle())) {
			return false;
		}
		return getBody().equals(that.getBody());
	}

	@Override
	public int hashCode() {
		var result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + getSpaceId().hashCode();
		result = 31 * result + getOwnerId().hashCode();
		result = 31 * result + getTitle().hashCode();
		return 31 * result + getBody().hashCode();
	}

	/**
	 * @return the type
	 */
	public DiscussionType getType() {
		if (type == null) {
			return DiscussionType.DISCUSSION;
		}
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(DiscussionType type) {
		this.type = type;
	}
}
