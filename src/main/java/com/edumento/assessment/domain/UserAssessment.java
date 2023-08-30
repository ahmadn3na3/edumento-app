package com.edumento.assessment.domain;

import com.edumento.assessment.model.WorkSheetAnswerModel;
import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.domain.AbstractEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/** Created by ayman on 04/07/16. */
@Document(collection = "mint.assessment")
public class UserAssessment extends AbstractEntity {
  @MongoId private String id;
  @Indexed private Long userId;
  @Indexed private Long assessmentId;

  private AssessmentStatus assessmentStatus = AssessmentStatus.NEW;

  @DBRef private List<QuestionAnswer> questionAnswerList = new ArrayList<>();

  private WorkSheetAnswerModel workSheetAnswerModel;
  private WorkSheetAnswerModel ownerWorkSheetAnswerModel;

  private Integer fullGrade = 0;
  private Float totalGrade = 0.0f;
  private Float percentage = 0.0f;
  private Boolean bestAnswer;

  private Boolean topThree;

  private Long duration;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public List<QuestionAnswer> getQuestionAnswerList() {
    return questionAnswerList;
  }

  public void setQuestionAnswerList(List<QuestionAnswer> questionAnswerList) {
    this.questionAnswerList = questionAnswerList;
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

  public Integer getFullGrade() {
    return fullGrade;
  }

  public void setFullGrade(Integer fullGrade) {
    this.fullGrade = fullGrade;
  }

  public Float getTotalGrade() {
    return totalGrade;
  }

  public void setTotalGrade(Float totalGrade) {
    this.totalGrade = totalGrade;
  }

  public WorkSheetAnswerModel getWorkSheetAnswerModel() {
    return workSheetAnswerModel;
  }

  public void setWorkSheetAnswerModel(WorkSheetAnswerModel workSheetAnswerModel) {
    this.workSheetAnswerModel = workSheetAnswerModel;
  }

  public WorkSheetAnswerModel getOwnerWorkSheetAnswerModel() {
    return ownerWorkSheetAnswerModel;
  }

  public void setOwnerWorkSheetAnswerModel(WorkSheetAnswerModel ownerWorkSheetAnswerModel) {
    this.ownerWorkSheetAnswerModel = ownerWorkSheetAnswerModel;
  }

  public Float getPercentage() {
    return percentage;
  }

  public void setPercentage(Float percentage) {
    this.percentage = percentage;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserAssessment that = (UserAssessment) o;

    if (!userId.equals(that.userId)) return false;
    return assessmentId.equals(that.assessmentId);
  }

  @Override
  public int hashCode() {
    int result = userId.hashCode();
    result = 31 * result + assessmentId.hashCode();
    return result;
  }
}
