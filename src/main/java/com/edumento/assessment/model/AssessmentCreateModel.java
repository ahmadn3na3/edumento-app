package com.edumento.assessment.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.edumento.core.constants.AssessmentType;

/** Created by ayman on 13/06/16. */
public class AssessmentCreateModel {

  private String title;
  private ZonedDateTime dueDate;
  private AssessmentType assessmentType;
  private Boolean lockMint = Boolean.TRUE;
  private Long limitDuration;
  private ZonedDateTime startDate;
  private Boolean dateOnly;
  private Boolean randomizingQuestion;
  private Boolean viewAnswersAfterSubmit;
  private boolean publish = false;
  private Long spaceId;
  private List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels = new ArrayList<>();
  private Long workSheetContentId;
  private Integer totalAssessmentPoints;
  private Float passingGrade;

  public AssessmentCreateModel() {}

  public AssessmentCreateModel(
      String title,
      AssessmentType assessmentType,
      boolean publish,
      Long spaceId,
      List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels) {
    this.title = title;
    this.assessmentType = assessmentType;
    this.publish = publish;
    this.spaceId = spaceId;
    this.assessmentQuestionCreateModels = assessmentQuestionCreateModels;
  }

  public List<AssessmentQuestionCreateModel> getAssessmentQuestionCreateModels() {
    return assessmentQuestionCreateModels;
  }

  public void setAssessmentQuestionCreateModels(
      List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels) {
    this.assessmentQuestionCreateModels = assessmentQuestionCreateModels;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ZonedDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(ZonedDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public AssessmentType getAssessmentType() {
    return assessmentType;
  }

  public void setAssessmentType(AssessmentType assessmentType) {
    this.assessmentType = assessmentType;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long space) {
    this.spaceId = space;
  }

  public Boolean getLockMint() {
    return lockMint;
  }

  public void setLockMint(Boolean lockMint) {
    this.lockMint = lockMint;
  }

  public Long getLimitDuration() {
    return limitDuration;
  }

  public void setLimitDuration(Long limitDuration) {
    this.limitDuration = limitDuration;
  }

  public boolean isPublish() {
    return publish;
  }

  public void setPublish(boolean publish) {
    this.publish = publish;
  }

  public ZonedDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(ZonedDateTime startDate) {
    this.startDate = startDate;
  }

  public Boolean getDateOnly() {
    return dateOnly;
  }

  public void setDateOnly(Boolean dateOnly) {
    this.dateOnly = dateOnly;
  }

  public Boolean getRandomizingQuestion() {
    return randomizingQuestion;
  }

  public void setRandomizingQuestion(Boolean randomizingQuestion) {
    this.randomizingQuestion = randomizingQuestion;
  }

  public Boolean getViewAnswersAfterSubmit() {
    return viewAnswersAfterSubmit;
  }

  public void setViewAnswersAfterSubmit(Boolean viewAnswersAfterSubmit) {
    this.viewAnswersAfterSubmit = viewAnswersAfterSubmit;
  }

  public Long getWorkSheetContentId() {
    return workSheetContentId;
  }

  public void setWorkSheetContentId(Long workSheetContentId) {
    this.workSheetContentId = workSheetContentId;
  }

  public Integer getTotalAssessmentPoints() {
    return totalAssessmentPoints;
  }

  public void setTotalAssessmentPoints(Integer totalAssessmentPoints) {
    this.totalAssessmentPoints = totalAssessmentPoints;
  }

  public Float getPassingGrade() {
    return passingGrade;
  }

  public void setPassingGrade(Float passingGrade) {
    this.passingGrade = passingGrade;
  }

  @Override
  public String toString() {
    return "AssessmentCreateModel{"
        + "title='"
        + title
        + '\''
        + ", dueDate="
        + dueDate
        + ", assessmentType="
        + assessmentType
        + ", lockMint="
        + lockMint
        + ", limitDuration="
        + limitDuration
        + ", startDate="
        + startDate
        + ", dateOnly="
        + dateOnly
        + ", randomizingQuestion="
        + randomizingQuestion
        + ", viewAnswersAfterSubmit="
        + viewAnswersAfterSubmit
        + ", publish="
        + publish
        + ", spaceId="
        + spaceId
        + '}';
  }
}
