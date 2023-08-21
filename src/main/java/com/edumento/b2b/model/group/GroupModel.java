package com.edumento.b2b.model.group;

import com.edumento.b2b.model.organization.SimpleOrganizationModel;
import com.edumento.core.model.SimpleModel;

/** Created by ahmad on 3/7/16. */
public class GroupModel extends GroupCreateModel {

	private Long id;
	private Integer userCount;
	private SimpleOrganizationModel organization;
	private SimpleModel foundation;
	private Integer spaceCount;

	public GroupModel() {}

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
