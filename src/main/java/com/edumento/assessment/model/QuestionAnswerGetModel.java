package com.edumento.assessment.model;

/** Created by ayman on 18/10/16. */
public class QuestionAnswerGetModel extends AssessmentQuestionCreateModel {

	private String userAnswer;
	private Float grade;

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public Float getGrade() {
		return grade;
	}

	public void setGrade(Float grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "QuestionAnswerGetModel{" + "userAnswer='" + userAnswer + '\'' + ", grade=" + grade + "} "
				+ super.toString();
	}
}
