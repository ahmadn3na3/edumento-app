package com.edumento.space.model.space.response;

import java.util.HashSet;
import java.util.Set;

/** Created by ahmad on 5/11/16. */
public class SpaceListingUpdateModel {
	private Set<SpaceListingModel> updatesSpaces = new HashSet<>();
	private Set<SpaceListingModel> joinedSpaces = new HashSet<>();
	private Set<SpaceListingModel> newSpaces = new HashSet<>();
	private Set<Long> deletedSpaces = new HashSet<>();
	private Set<Long> unSharedSpaces = new HashSet<>();

	public Set<SpaceListingModel> getNewSpaces() {
		return newSpaces;
	}

	public void setNewSpaces(Set<SpaceListingModel> newSpaces) {
		this.newSpaces = newSpaces;
	}

	public Set<SpaceListingModel> getUpdatesSpaces() {
		return updatesSpaces;
	}

	public void setUpdatesSpaces(Set<SpaceListingModel> updatesSpaces) {
		this.updatesSpaces = updatesSpaces;
	}

	public Set<SpaceListingModel> getJoinedSpaces() {
		return joinedSpaces;
	}

	public void setJoinedSpaces(Set<SpaceListingModel> joinedSpaces) {
		this.joinedSpaces = joinedSpaces;
	}

	public Set<Long> getDeletedSpaces() {
		return deletedSpaces;
	}

	public void setDeletedSpaces(Set<Long> deletedSpaces) {
		this.deletedSpaces = deletedSpaces;
	}

	public Set<Long> getUnSharedSpaces() {
		return unSharedSpaces;
	}

	public void setUnSharedSpaces(Set<Long> unSharedSpaces) {
		this.unSharedSpaces = unSharedSpaces;
	}

	@Override
	public String toString() {
		return "SpaceListingUpdateModel{" + "updatesSpaces=" + updatesSpaces + ", joinedSpaces=" + joinedSpaces
				+ ", newSpaces=" + newSpaces + ", deletedSpaces=" + deletedSpaces + ", unSharedSpaces=" + unSharedSpaces
				+ '}';
	}
}
