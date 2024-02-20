package com.edumento.assessment.model;

import java.util.ArrayList;
import java.util.List;

/** Created by ahmad on 7/13/16. */
public class AssessmentsUpdatesModel {

	List<AssessmentModel> deletedAssessments = new ArrayList<>();
	List<AssessmentModel> updatedAssessments = new ArrayList<>();
	List<AssessmentModel> newAssessments = new ArrayList<>();

	public List<AssessmentModel> getDeletedAssessments() {
		return deletedAssessments;
	}

	public void setDeletedAssessments(List<AssessmentModel> deletedAssessments) {
		this.deletedAssessments = deletedAssessments;
	}

	public List<AssessmentModel> getUpdatedAssessments() {
		return updatedAssessments;
	}

	public void setUpdatedAssessments(List<AssessmentModel> updatedAssessments) {
		this.updatedAssessments = updatedAssessments;
	}

	public List<AssessmentModel> getNewAssessments() {
		return newAssessments;
	}

	public void setNewAssessments(List<AssessmentModel> newAssessments) {
		this.newAssessments = newAssessments;
	}

	@Override
	public String toString() {
		return "AssessmentsUpdatesModel{" + "deletedAssessments=" + deletedAssessments.toString()
				+ ", updatedAssessments=" + updatedAssessments.toString() + ", newAssessments="
				+ newAssessments.toString() + '}';
	}
}
