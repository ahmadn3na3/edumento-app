package com.edumento.assessment.model;

/** Created by ayman on 04/07/16. */
public class QuestionAnswerModel {

	private String id;

	private Long questionId;

	private Long userId;

	private String userAnswer;

	private Float grade = 0.0f;

	private Boolean skipped = Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Float getGrade() {
		return grade;
	}

	public void setGrade(Float grade) {
		this.grade = grade;
	}

	public Boolean isSkipped() {
		return skipped;
	}

	public void setSkipped(Boolean skipped) {
		this.skipped = skipped;
	}

	@Override
	public String toString() {
		return "QuestionAnswerModel{" + "id='" + id + '\'' + ", questionId=" + questionId + ", userId=" + userId
				+ ", userAnswer='" + userAnswer + '\'' + ", grade=" + grade + '}';
	}
}
