package com.edumento.discussion.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.edumento.core.domain.AbstractEntity;

/** Created by ayman on 18/08/16. */
@Document
public class Like extends AbstractEntity {
	@Id
	private String id;
	private Boolean liked = Boolean.FALSE;
	@Indexed
	private Long userId;
	private String userName;
	@Indexed
	private String parentId;

	public Boolean getLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var like = (Like) o;

		if ((getId() != null ? !getId().equals(like.getId()) : like.getId() != null)
				|| !getUserId().equals(like.getUserId())) {
			return false;
		}
		return getParentId().equals(like.getParentId());
	}

	@Override
	public int hashCode() {
		var result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + getUserId().hashCode();
		return 31 * result + getParentId().hashCode();
	}
}
