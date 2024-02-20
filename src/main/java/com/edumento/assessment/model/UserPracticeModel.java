package com.edumento.assessment.model;

import com.edumento.core.constants.AssessmentStatus;

/** Created by ayman on 06/06/17. */
public class UserPracticeModel {
	private Long userId;
	private Long assessmentId;
	private AssessmentStatus assessmentStatus;
	private QuestionAnswerModel questionAnswerModels;

	public UserPracticeModel() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(Long assessmentId) {
		this.assessmentId = assessmentId;
	}

	public AssessmentStatus getAssessmentStatus() {
		return assessmentStatus;
	}

	public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
		this.assessmentStatus = assessmentStatus;
	}

	public QuestionAnswerModel getQuestionAnswerModels() {
		return questionAnswerModels;
	}

	public void setQuestionAnswerModels(QuestionAnswerModel questionAnswerModels) {
		this.questionAnswerModels = questionAnswerModels;
	}
}
