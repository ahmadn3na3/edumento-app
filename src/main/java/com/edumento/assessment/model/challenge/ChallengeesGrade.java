package com.edumento.assessment.model.challenge;


import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.model.SimpleModel;

public class ChallengeesGrade extends SimpleModel {

	private Float totalGrade = 0.0f;
	private boolean creator;
	private AssessmentStatus status;
	
	/** created by A.Alsayed on 5-2-2019
	 * for getting user challenges API 
	**/
	private String school;
	private String thumbnail;
	
	public ChallengeesGrade() {
		
	}
	
	public ChallengeesGrade(Long id,String name, Float totalGrade) {
		super(id,name);
		this.totalGrade = totalGrade;
	}
	public Float getTotalGrade() {
		return totalGrade;
	}
	public void setTotalGrade(Float totalGrade) {
		this.totalGrade = totalGrade;
	}

	public boolean isCreator() {
		return creator;
	}

	public void setCreator(boolean creator) {
		this.creator = creator;
	}

	public AssessmentStatus getStatus() {
		return status;
	}

	public void setStatus(AssessmentStatus status) {
		this.status = status;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
