package com.edumento.assessment.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.domain.AssessmentQuestion;
import com.edumento.assessment.domain.AssessmentQuestionChoice;
import com.edumento.assessment.domain.UserAssessment;
import com.edumento.core.constants.AssessmentType;

/** Created by ayman on 13/06/16. */
public class AssessmentModel extends AssessmentListModel {

	private Boolean lockMint = Boolean.TRUE;
	private Boolean randomizingQuestion;
	private Long workSheetContentId;

	private List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels = new ArrayList<>();
	private WorkSheetAnswerModel userWorkSheetAnswerModel;
	private WorkSheetAnswerModel ownerWorkSheetAnswerModel;

	public AssessmentModel() {
	}

	public AssessmentModel(Assessment assessment, List<UserAssessment> userAssesmenstList, Long currentUserId) {
		super(assessment, userAssesmenstList, currentUserId);
		lockMint = assessment.getLockMint();
		randomizingQuestion = assessment.getRandomizingQuestion();
		if (assessment.getAssessmentType() == AssessmentType.WORKSHEET) {
			workSheetContentId = assessment.getContent().getId();
		}

		assessmentQuestionCreateModels = assessment.getAssessmentQuestions().stream()
				.filter(new Predicate<AssessmentQuestion>() {
					@Override
					public boolean test(AssessmentQuestion assessmentQuestion) {
						return !assessmentQuestion.isDeleted();
					}
				}).map(new Function<AssessmentQuestion, AssessmentQuestionCreateModel>() {
					@Override
					public AssessmentQuestionCreateModel apply(AssessmentQuestion assessmentQuestion) {
						var assessmentQuestionCreateModel = new AssessmentQuestionCreateModel();
						assessmentQuestionCreateModel.setId(assessmentQuestion.getId());
						assessmentQuestionCreateModel.setQuestionType(assessmentQuestion.getQuestionType());
						assessmentQuestionCreateModel.setChoicesList(assessmentQuestion.getAssessmentQuestionChoices()
								.stream().filter(new Predicate<AssessmentQuestionChoice>() {
									@Override
									public boolean test(AssessmentQuestionChoice assessmentQuestionChoice) {
										return !assessmentQuestionChoice.isDeleted();
									}
								})
								.map(new Function<AssessmentQuestionChoice, ChoicesModel>() {
									@Override
									public ChoicesModel apply(AssessmentQuestionChoice assessmentQuestionChoice) {
										var choicesModel = new ChoicesModel();
										choicesModel.setId(assessmentQuestionChoice.getId());
										choicesModel.setPairColumn(assessmentQuestionChoice.getPairCol());
										choicesModel.setCorrectOrder(assessmentQuestionChoice.getCorrectOrder());
										choicesModel.setCorrectAnswer(assessmentQuestionChoice.getCorrectAnswer());
										choicesModel.setCorrectAnswerResourceUrl(
												assessmentQuestionChoice.getCorrectAnswerResourceUrl());
										choicesModel.setLabel(assessmentQuestionChoice.getLabel());
										choicesModel.setResourceType(assessmentQuestionChoice.getResourceType());
										return choicesModel;
									}
								}).collect(Collectors.toList()));

						assessmentQuestionCreateModel.setQuestionWeight(assessmentQuestion.getQuestionWeight());
						assessmentQuestionCreateModel.setBody(assessmentQuestion.getBody());
						assessmentQuestionCreateModel.setCorrectAnswer(assessmentQuestion.getCorrectAnswer());
						assessmentQuestionCreateModel.setBodyResourceUrl(assessmentQuestion.getBodyResourceUrl());
						assessmentQuestionCreateModel.setResourceType(assessmentQuestion.getResourceType());
						return assessmentQuestionCreateModel;
					}
				}).collect(Collectors.toList());
		if (randomizingQuestion == Boolean.TRUE && !assessment.getOwner().getId().equals(currentUserId)) {
			Collections.shuffle(assessmentQuestionCreateModels);
		}
	}

	@Override
	public Boolean getLockMint() {
		return lockMint;
	}

	@Override
	public void setLockMint(Boolean lockMint) {
		this.lockMint = lockMint;
	}

	@Override
	public Boolean getRandomizingQuestion() {
		return randomizingQuestion;
	}

	@Override
	public void setRandomizingQuestion(Boolean randomizingQuestion) {
		this.randomizingQuestion = randomizingQuestion;
	}

	@Override
	public List<AssessmentQuestionCreateModel> getAssessmentQuestionCreateModels() {
		return assessmentQuestionCreateModels;
	}

	@Override
	public void setAssessmentQuestionCreateModels(List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels) {
		this.assessmentQuestionCreateModels = assessmentQuestionCreateModels;
	}

	@Override
	public Long getWorkSheetContentId() {
		return workSheetContentId;
	}

	@Override
	public void setWorkSheetContentId(Long workSheetContentId) {
		this.workSheetContentId = workSheetContentId;
	}

	public WorkSheetAnswerModel getUserWorkSheetAnswerModel() {
		return userWorkSheetAnswerModel;
	}

	public void setUserWorkSheetAnswerModel(WorkSheetAnswerModel userWorkSheetAnswerModel) {
		this.userWorkSheetAnswerModel = userWorkSheetAnswerModel;
	}

	public WorkSheetAnswerModel getOwnerWorkSheetAnswerModel() {
		return ownerWorkSheetAnswerModel;
	}

	public void setOwnerWorkSheetAnswerModel(WorkSheetAnswerModel ownerWorkSheetAnswerModel) {
		this.ownerWorkSheetAnswerModel = ownerWorkSheetAnswerModel;
	}

	@Override
	public String toString() {
		return "AssessmentModel{" + ", lockMint=" + lockMint + ", randomizingQuestion=" + randomizingQuestion
				+ ", assessmentQuestionCreateModels=" + assessmentQuestionCreateModels + "} " + super.toString();
	}
}
