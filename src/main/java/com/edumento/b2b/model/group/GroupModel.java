package com.edumento.b2b.model.group;

/** Created by ahmad on 3/7/16. */
public class GroupModel extends GroupCreateModel {

	private Long id;
	private Integer userCount;
	private Integer spaceCount;

	public GroupModel() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public Integer getSpaceCount() {
		return spaceCount;
	}

	public void setSpaceCount(Integer spaceCount) {
		this.spaceCount = spaceCount;
	}

}
