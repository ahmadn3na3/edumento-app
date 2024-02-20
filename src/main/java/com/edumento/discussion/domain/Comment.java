package com.edumento.discussion.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.edumento.core.constants.CommentType;
import com.edumento.core.domain.AbstractEntity;

/** Created by ayman on 25/08/16. */
@Document
public class Comment extends AbstractEntity {

	@Id
	private String id;
	private String body;
	private Boolean likeEnabled = Boolean.FALSE;
	@Indexed
	private Long userId;
	private String userName;
	private String userFullName;
	private String userThumbnail;
	private Integer votes = 0;
	@Indexed
	private CommentType type;
	@Indexed
	private Long spaceId;
	@Indexed
	private String parentId;
	@DBRef
	private List<Like> likes = new ArrayList<>();

	public Comment() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}

	public Boolean getLikeEnabled() {
		return likeEnabled;
	}

	public void setLikeEnabled(Boolean likeEnabled) {
		this.likeEnabled = likeEnabled;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getUserThumbnail() {
		return userThumbnail;
	}

	public void setUserThumbnail(String userThumbnail) {
		this.userThumbnail = userThumbnail;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
		this.type = type;
	}

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var comment = (Comment) o;

		if ((getId() != null ? !getId().equals(comment.getId()) : comment.getId() != null)
				|| !getUserId().equals(comment.getUserId())) {
			return false;
		}
		return getParentId().equals(comment.getParentId());
	}

	@Override
	public int hashCode() {
		var result = getId() != null ? getId().hashCode() : 0;

		result = 31 * result + getUserId().hashCode();
		if (null != getParentId()) {
			result = 31 * result + getParentId().hashCode();
		}
		return result;
	}
}
