package com.edumento.assessment.model;

import java.util.List;

import com.edumento.core.constants.AssessmentStatus;

/** Created by ayman on 04/07/16. */
public class UserAssessmentModel {

  private Long userId;
  private Long assessmentId;
  private AssessmentStatus assessmentStatus = AssessmentStatus.FINISHED;
  private List<QuestionAnswerModel> questionAnswerModels;
  private WorkSheetAnswerModel userWorkSheetAnswerModel;
  private WorkSheetAnswerModel ownerWorkSheetAnswerModel;
  private Float totalGrade = 0f;
  private Integer fullGrade;
  private Boolean bestAnswer;
  private Boolean topThree;
  private Long duration;

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

  public List<QuestionAnswerModel> getQuestionAnswerModels() {
    return questionAnswerModels;
  }

  public void setQuestionAnswerModels(List<QuestionAnswerModel> questionAnswerModels) {
    this.questionAnswerModels = questionAnswerModels;
  }

  public Boolean getBestAnswer() {
    return bestAnswer;
  }

  public void setBestAnswer(Boolean bestAnswer) {
    this.bestAnswer = bestAnswer;
  }

  public Boolean getTopThree() {
    return topThree;
  }

  public void setTopThree(Boolean topThree) {
    this.topThree = topThree;
  }

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

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  @Override
  public String toString() {
    return "UserAssessmentModel{"
        + "userId='"
        + userId
        + '\''
        + ", assessmentId="
        + assessmentId
        + ", assessmentStatus="
        + assessmentStatus
        + ", questionAnswerModels="
        + questionAnswerModels
        + ", totalGrade="
        + totalGrade
        + ", fullGrade="
        + fullGrade
        + ", bestAnswer="
        + bestAnswer
        + ", topThree="
        + topThree
        + '}';
  }
}
