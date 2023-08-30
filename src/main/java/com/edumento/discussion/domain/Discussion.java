package com.edumento.discussion.domain;

import com.edumento.core.constants.DiscussionType;
import com.edumento.core.domain.AbstractEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/** Created by ayman on 25/08/16. */
@Document(collection = "mint.discussions")
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Discussion that = (Discussion) o;

		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null)
			return false;
		if (!getSpaceId().equals(that.getSpaceId()))
			return false;
		if (!getOwnerId().equals(that.getOwnerId()))
			return false;
		if (!getTitle().equals(that.getTitle()))
			return false;
		return getBody().equals(that.getBody());
	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + getSpaceId().hashCode();
		result = 31 * result + getOwnerId().hashCode();
		result = 31 * result + getTitle().hashCode();
		result = 31 * result + getBody().hashCode();
		return result;
	}

	/**
	 * @return the type
	 */
	public DiscussionType getType() {
		if (this.type == null) {
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
