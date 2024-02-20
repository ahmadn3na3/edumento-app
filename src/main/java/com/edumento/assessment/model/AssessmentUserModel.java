package com.edumento.assessment.model;

import com.edumento.content.models.ContentUserModel;
import com.edumento.core.constants.AssessmentStatus;

/** Created by ahmad on 10/18/16. */
public class AssessmentUserModel extends ContentUserModel {
	private Float totalGrade;
	private Integer fullGrade;
	private Float percentage;
	private String message;

	private AssessmentStatus assessmentStatus = AssessmentStatus.NOT_STARTED;

	public Float getTotalGrade() {
		return totalGrade;
	}

	public void setTotalGrade(Float totalGrade) {
		this.totalGrade = totalGrade;
	}

	public Integer getFullGrade() {
		return fullGrade;
	}

	public void setFullGrade(Integer fullGrade) {
		this.fullGrade = fullGrade;
	}

	public AssessmentStatus getAssessmentStatus() {
		return assessmentStatus;
	}

	public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
		this.assessmentStatus = assessmentStatus;
	}

	public Float getPercentage() {
		return percentage;
	}

	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "AssessmentUserModel{" + "totalGrade=" + totalGrade + ", fullGrade=" + fullGrade + ", percentage="
				+ percentage + ", message='" + message + '\'' + ", assessmentStatus=" + assessmentStatus + '}';
	}
}
