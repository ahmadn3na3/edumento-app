package com.edumento.user.model.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.edumento.b2b.model.organization.SimpleOrganizationModel;
import com.edumento.b2b.model.timelock.TimeModel;
import com.edumento.core.model.SimpleModel;
import com.edumento.user.domain.User;

/** Created by ahmad on 2/17/16. */
public class UserModel extends UserInfoModel {
	private SimpleOrganizationModel organization;
	private SimpleModel foundation;
	private List<SimpleModel> roles;
	private List<SimpleModel> groups;
	private Long spacesCount = 0L;
	private Map<String, List<TimeModel>> dayModels = new HashMap<>();
	private String school;

	public UserModel(User user) {
		super(user);
		if (user.getOrganization() != null) {
			this.organization = new SimpleOrganizationModel(
					user.getOrganization().getId(),
					user.getOrganization().getName(),
					user.getOrganization().getOrgId());
		}
		if (user.getFoundation() != null) {
			this.foundation = new SimpleModel(user.getFoundation().getId(), user.getFoundation().getName());
		}

		this.roles = user.getRoles().stream().map(role -> new SimpleModel(role.getId(), role.getName()))
				.collect(Collectors.toList());
		this.groups = user.getGroups()
				.stream()
				.map(groups -> new SimpleModel(groups.getId(), groups.getName()))
				.collect(Collectors.toList());
		this.spacesCount = user.getSpaces().stream().filter(space -> !space.isDeleted()).count();
		this.school = user.getSchool();
	}

	/**
	 * @return the school
	 */
	public String getSchool() {
		return school;
	}

	/**
	 * @param school the school to set
	 */
	public void setSchool(String school) {
		this.school = school;
	}

	public List<SimpleModel> getRoles() {
		return roles;
	}

	public void setRoles(List<SimpleModel> roles) {
		this.roles = roles;
	}

	public List<SimpleModel> getGroups() {
		return groups;
	}

	public void setGroups(List<SimpleModel> groups) {
		this.groups = groups;
	}

	public Long getSpacesCount() {
		return spacesCount;
	}

	public void setSpacesCount(Long spacesCount) {
		this.spacesCount = spacesCount;
	}

	/**
	 * @return the organization
	 */
	public SimpleOrganizationModel getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(SimpleOrganizationModel organization) {
		this.organization = organization;
	}

	/**
	 * @return the foundation
	 */
	public SimpleModel getFoundation() {
		return foundation;
	}

	/**
	 * @param foundation the foundation to set
	 */
	public void setFoundation(SimpleModel foundation) {
		this.foundation = foundation;
	}

	/**
	 * @return the dayModels
	 */
	public Map<String, List<TimeModel>> getDayModels() {
		return dayModels;
	}

	/**
	 * @param dayModels the dayModels to set
	 */
	public void setDayModels(Map<String, List<TimeModel>> dayModels) {
		this.dayModels = dayModels;
	}

	
}
