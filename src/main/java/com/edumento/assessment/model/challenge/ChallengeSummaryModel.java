package com.edumento.assessment.model.challenge;

import com.edumento.core.constants.AssessmentStatus;

import java.util.Date;
import java.util.List;


/** Created by A.Alsayed on 05/01/19. */
public class ChallengeSummaryModel {

	// assessment Id
	
	private Long id;

	// Title of the Challenge (Practice Name)
	private String title;

	// Date of creation:
	private Date creationDate;

	// Username of opponent
	private List<ChallengeesGrade> opponents;

	// Statement indicates expiry date:
	private Date dueDate;

	private AssessmentStatus overallChallengeStatus;

	public AssessmentStatus getOverallChallengeStatus() {
		return overallChallengeStatus;
	}

	public void setOverallChallengeStatus(AssessmentStatus overallChallengeStatus) {
		this.overallChallengeStatus = overallChallengeStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public List<ChallengeesGrade> getOpponents() {
		return opponents;
	}

	public void setOpponents(List<ChallengeesGrade> opponents) {
		this.opponents = opponents;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
