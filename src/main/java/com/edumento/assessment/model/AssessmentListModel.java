package com.edumento.assessment.model;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.domain.UserAssessment;
import com.edumento.content.models.ContentUserModel;
import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.util.DateConverter;
import java.time.ZonedDateTime;
import java.util.List;

/** Created by ayman on 3/11/2016 */
public class AssessmentListModel extends AssessmentCreateModel {

  private Long id;
  private AssessmentStatus assessmentStatus = AssessmentStatus.NOT_STARTED;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModifiedDate;
  private ZonedDateTime publishedDate;
  private List<ContentUserModel> userCommunity;
  private Integer userCounter = 0;
  private Integer corrected = 0;
  private Boolean alreadyTaken = Boolean.FALSE;
  private float totalGrade;
  private float totalPercentage;
  private Boolean topThree;
  private Boolean bestAnswer;
  private Integer rank;
  private Long numberOfQuestions;
  private Boolean limitedByTime;
  
  private Long owner;

  private Boolean isOwner = false;
  private Long duration;

  public AssessmentListModel() {}

  public AssessmentListModel(
          Assessment assessment, List<UserAssessment> userAssessmentsList, Long currentUserId) {
    this.setId(assessment.getId());
    this.setTitle(assessment.getTitle());
    this.setDueDate(DateConverter.convertDateToZonedDateTime(assessment.getDueDate()));
    this.setAssessmentType(assessment.getAssessmentType());
    this.setViewAnswersAfterSubmit(assessment.getViewAnswersAfterSubmit());
    if (assessment.getLimitDuration() != null) {
      this.setLimitedByTime(Boolean.TRUE);
    } else {
      this.setLimitedByTime(Boolean.FALSE);
    }
    this.setTotalAssessmentPoints(assessment.getTotalPoints());
    this.setLimitDuration(assessment.getLimitDuration());
    this.setStartDate(DateConverter.convertDateToZonedDateTime(assessment.getStartDateTime()));
    this.setDateOnly(assessment.getDateOnly());
    this.setPublish(assessment.getPublish());
    this.setSpaceId(assessment.getSpace().getId());
    this.setCreationDate(DateConverter.convertDateToZonedDateTime(assessment.getCreationDate()));
    if (null != assessment.getLastModifiedDate()) {
      this.setLastModifiedDate(
          DateConverter.convertDateToZonedDateTime(assessment.getLastModifiedDate()));
    }
    if (null != assessment.getPublishDate()) {
      this.setPublishedDate(DateConverter.convertDateToZonedDateTime(assessment.getPublishDate()));
    }
    if (null != assessment.getOwner()) {
      this.setOwner(assessment.getOwner().getId());
      this.setIsOwner(this.owner.equals(currentUserId));
    }
    if (userAssessmentsList != null && !userAssessmentsList.isEmpty()) {
      userAssessmentsList.forEach(
          userAssessment -> {
            if (userAssessment.getAssessmentStatus().equals(AssessmentStatus.EVALUATED)) {
              corrected++;
            }
            if (userAssessment.getUserId().equals(currentUserId)) {
              AssessmentListModel.this.setAlreadyTaken(Boolean.TRUE);
              AssessmentListModel.this.setRank(userAssessmentsList.indexOf(userAssessment) + 1);
              AssessmentListModel.this.setTotalGrade(userAssessment.getTotalGrade());
              AssessmentListModel.this.setTotalPercentage(userAssessment.getPercentage());
              AssessmentListModel.this.setAssessmentStatus(userAssessment.getAssessmentStatus());
              AssessmentListModel.this.setBestAnswer(userAssessment.getBestAnswer());
              AssessmentListModel.this.setDuration(userAssessment.getDuration());

              if (AssessmentListModel.this.getRank() < 3) {
                AssessmentListModel.this.setTopThree(true);
              }
              this.setAssessmentStatus(userAssessment.getAssessmentStatus());
            }
          });
      this.setUserCounter(userAssessmentsList.size());
    }
    this.setPassingGrade(assessment.getPassingGrade());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AssessmentStatus getAssessmentStatus() {
    return assessmentStatus;
  }

  public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
    this.assessmentStatus = assessmentStatus;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ZonedDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public boolean isLimitedByTime() {
    return limitedByTime;
  }

  public ZonedDateTime getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(ZonedDateTime publishedDate) {
    this.publishedDate = publishedDate;
  }

  public void setLimitedByTime(boolean limitedByTime) {
    this.limitedByTime = limitedByTime;
  }

  public List<ContentUserModel> getUserCommunity() {
    return userCommunity;
  }

  public void setUserCommunity(List<ContentUserModel> userCommunity) {
    this.userCommunity = userCommunity;
  }

  public Integer getUserCounter() {
    return userCounter;
  }

  public void setUserCounter(Integer userCounter) {
    this.userCounter = userCounter;
  }

  public Boolean getAlreadyTaken() {
    return alreadyTaken;
  }

  public void setAlreadyTaken(Boolean alreadyTaken) {
    this.alreadyTaken = alreadyTaken;
  }

  public float getTotalGrade() {
    return totalGrade;
  }

  public void setTotalGrade(float totalGrade) {
    this.totalGrade = totalGrade;
  }

  public Boolean getTopThree() {
    return topThree;
  }

  public void setTopThree(Boolean topThree) {
    this.topThree = topThree;
  }

  public Boolean getBestAnswer() {
    return bestAnswer;
  }

  public void setBestAnswer(Boolean bestAnswer) {
    this.bestAnswer = bestAnswer;
  }

  public Integer getRank() {
    return rank;
  }

  public void setRank(Integer rank) {
    this.rank = rank;
  }

  public Boolean getLimitedByTime() {
    if (getLimitDuration() == null || getLimitDuration() == 0) {
      return Boolean.FALSE;
    } else {
      return Boolean.TRUE;
    }
  }

  public void setLimitedByTime(Boolean limitedByTime) {
    this.limitedByTime = limitedByTime;
  }

  public float getTotalPercentage() {
    return totalPercentage;
  }

  public void setTotalPercentage(float totalPercentage) {
    this.totalPercentage = totalPercentage;
  }

  public Long getOwner() {
    return owner;
  }

  public void setOwner(Long owner) {
    this.owner = owner;
  }

  public Boolean getIsOwner() {
    return isOwner;
  }

  public void setIsOwner(Boolean owner) {
    isOwner = owner;
  }

  public Long getNumberOfQuestions() {
    return numberOfQuestions;
  }

  public void setNumberOfQuestions(Long numberOfQuestions) {
    this.numberOfQuestions = numberOfQuestions;
  }

  public Integer getCorrected() {
    return corrected;
  }

  public void setCorrected(Integer corrected) {
    this.corrected = corrected;
  }

  @Override
  public String toString() {
    return "AssessmentListModel{"
        + "id="
        + id
        + ", assessmentStatus="
        + assessmentStatus
        + ", creationDate="
        + creationDate
        + ", lastModifiedDate="
        + lastModifiedDate
        + ", publishedDate="
        + publishedDate
        + ", userCommunity="
        + userCommunity
        + ", userCounter="
        + userCounter
        + ", alreadyTaken="
        + alreadyTaken
        + ", totalGrade="
        + totalGrade
        + ", totalPercentage="
        + totalPercentage
        + ", topThree="
        + topThree
        + ", bestAnswer="
        + bestAnswer
        + ", rank="
        + rank
        + ", numberOfQuestions="
        + numberOfQuestions
        + ", limitedByTime="
        + limitedByTime
        + ", owner="
        + owner
        + ", isOwner="
        + isOwner
        + "} "
        + super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AssessmentListModel that = (AssessmentListModel) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }
}
