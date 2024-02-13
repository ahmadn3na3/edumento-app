package com.edumento.assessment.model;

import java.util.ArrayList;
import java.util.List;

import com.edumento.core.constants.AssessmentStatus;

/** Created by ayman on 18/10/16. */
public class UserAssessmentGetModel {

  private Long userId;
  private String userName;
  private Integer totalGrade;
  private Integer fullGrade;
  private AssessmentStatus assessmentStatus;
  private String assessmentTitle;
  private Integer numberOfQuestions;

  private List<QuestionAnswerGetModel> questionAnswerGetModels = new ArrayList<>();

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Integer getTotalGrade() {
    return totalGrade;
  }

  public void setTotalGrade(Integer totalGrade) {
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

  public String getAssessmentTitle() {
    return assessmentTitle;
  }

  public void setAssessmentTitle(String assessmentTitle) {
    this.assessmentTitle = assessmentTitle;
  }

  public Integer getNumberOfQuestions() {
    return numberOfQuestions;
  }

  public void setNumberOfQuestions(Integer numberOfQuestions) {
    this.numberOfQuestions = numberOfQuestions;
  }

  public List<QuestionAnswerGetModel> getQuestionAnswerGetModels() {
    return questionAnswerGetModels;
  }

  public void setQuestionAnswerGetModels(List<QuestionAnswerGetModel> questionAnswerGetModels) {
    this.questionAnswerGetModels = questionAnswerGetModels;
  }

  public void addQuestionAnswerGetModel(QuestionAnswerGetModel questionAnswerGetModel) {
    this.questionAnswerGetModels.add(questionAnswerGetModel);
  }

  @Override
  public String toString() {
    return "UserAssessmentGetModel{"
        + "userId="
        + userId
        + ", userName='"
        + userName
        + '\''
        + ", totalGrade="
        + totalGrade
        + ", fullGrade="
        + fullGrade
        + ", assessmentStatus="
        + assessmentStatus
        + ", assessmentTitle='"
        + assessmentTitle
        + '\''
        + ", numberOfQuestions="
        + numberOfQuestions
        + ", questionAnswerGetModels="
        + questionAnswerGetModels
        + '}';
  }
}
