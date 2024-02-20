package com.edumento.core.model.messages.assessment;

import java.util.Date;

import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.constants.AssessmentType;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.user.UserInfoMessage;

public class AssessmentSubmitMessage extends AssessementsInfoMessage {

	private AssessmentStatus assessmentStatus;
	private Boolean isOwner = Boolean.FALSE;
	private UserInfoMessage userSolved;
	private UserInfoMessage owner;

	public AssessmentSubmitMessage() {
		// TODO Auto-generated constructor stub
	}

	public AssessmentSubmitMessage(Long id, String name, AssessmentType assessmentType, From from, Long spaceId,
			Date startDateTime, Date dueDateTime, String spaceName, String categoryName,
			AssessmentStatus assessmentStatus, Boolean isOwner, UserInfoMessage userSolved, UserInfoMessage owner) {
		super(id, name, assessmentType, from, spaceId, startDateTime, dueDateTime, spaceName, categoryName, null);
		this.assessmentStatus = assessmentStatus;
		this.isOwner = isOwner;
		this.userSolved = userSolved;
		this.owner = owner;
	}

	public AssessmentStatus getAssessmentStatus() {
		return assessmentStatus;
	}

	public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
		this.assessmentStatus = assessmentStatus;
	}

	public Boolean getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
	}

	public UserInfoMessage getOwner() {
		return owner;
	}

	public void setOwner(UserInfoMessage owner) {
		this.owner = owner;
	}

	public UserInfoMessage getUserSolved() {
		return userSolved;
	}

	public void setUserSolved(UserInfoMessage userSolved) {
		this.userSolved = userSolved;
	}

	@Override
	public String toString() {
		return String.format("AssessmentSubmitMessage [assessmentStatus=%s, isOwner=%s, userSolved=%s, owner=%s]",
				assessmentStatus, isOwner, userSolved, owner);
	}
}
