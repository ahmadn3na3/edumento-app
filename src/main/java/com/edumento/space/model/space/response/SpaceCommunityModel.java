package com.edumento.space.model.space.response;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Created by ahmad on 5/24/16. */
public class SpaceCommunityModel {
	private Set<SpaceUserModel> joinedUsers = new HashSet<>();
	private Set<SpaceUserModel> topAnnotators = new HashSet<>();
	private Map<String, Set<SpaceUserModel>> groups = new HashMap<>();

	public Set<SpaceUserModel> getJoinedUsers() {
		return joinedUsers;
	}

	public void setJoinedUsers(Set<SpaceUserModel> joinedUsers) {
		this.joinedUsers = joinedUsers;
	}

	public Map<String, Set<SpaceUserModel>> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Set<SpaceUserModel>> groups) {
		this.groups = groups;
	}

	public Set<SpaceUserModel> getTopAnnotators() {
		return topAnnotators;
	}

	public void setTopAnnotators(Set<SpaceUserModel> topAnnotators) {
		this.topAnnotators = topAnnotators;
	}

	@Override
	public String toString() {
		return String.format("SpaceCommunityModel{joinedUsers=%s, topAnnotators=%s, groups=%s}", joinedUsers,
				topAnnotators, groups);
	}
}
