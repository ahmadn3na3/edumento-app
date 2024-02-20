package com.edumento.space.model.space.response;

import com.edumento.content.models.ContentUserModel;
import com.edumento.core.constants.SpaceRole;

/** Created by ahmad on 4/21/16. */
public class SpaceUserModel extends ContentUserModel {

	private SpaceRole spaceRole;

	private int numberOfAnnotation;
	private int numberOfDiscussions;
	private int numberOfAssessments;
	private int numberOfAddedContents;
	private int numberOfSpaceViews;
	private int numberOfDiscussionComments;

	private boolean follow;

	public SpaceRole getSpaceRole() {
		return spaceRole;
	}

	public void setSpaceRole(SpaceRole spaceRole) {
		this.spaceRole = spaceRole;
	}

	public int getNumberOfAnnotation() {
		return numberOfAnnotation;
	}

	public void setNumberOfAnnotation(int numberOfAnnotation) {
		this.numberOfAnnotation = numberOfAnnotation;
	}

	public boolean isFollow() {
		return follow;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	public int getNumberOfDiscussions() {
		return numberOfDiscussions;
	}

	public void setNumberOfDiscussions(int numberOfDiscussions) {
		this.numberOfDiscussions = numberOfDiscussions;
	}

	public int getNumberOfAssessments() {
		return numberOfAssessments;
	}

	public void setNumberOfAssessments(int numberOfAssessments) {
		this.numberOfAssessments = numberOfAssessments;
	}

	public int getNumberOfAddedContents() {
		return numberOfAddedContents;
	}

	public void setNumberOfAddedContents(int numberOfAddedContents) {
		this.numberOfAddedContents = numberOfAddedContents;
	}

	public int getNumberOfSpaceViews() {
		return numberOfSpaceViews;
	}

	public void setNumberOfSpaceViews(int numberOfSpaceViews) {
		this.numberOfSpaceViews = numberOfSpaceViews;
	}

	public int getNumberOfDiscussionComments() {
		return numberOfDiscussionComments;
	}

	public void setNumberOfDiscussionComments(int numberOfdiscussionComments) {
		numberOfDiscussionComments = numberOfdiscussionComments;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var that = (SpaceUserModel) o;

		if (!getId().equals(that.getId())) {
			return false;
		}
		return getUserName().equals(that.getUserName());
	}

	@Override
	public int hashCode() {
		var result = getId().hashCode();
		return 31 * result + getUserName().hashCode();
	}

	@Override
	public String toString() {
		return "SpaceUserModel{" + "spaceRole=" + spaceRole + ", numberOfAnnotation=" + numberOfAnnotation + ", follow="
				+ follow + "} " + super.toString();
	}
}
