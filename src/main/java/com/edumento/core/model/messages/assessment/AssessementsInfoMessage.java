package com.edumento.core.model.messages.assessment;

import java.util.Date;
import com.edumento.core.constants.AssessmentType;
import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;

public class AssessementsInfoMessage extends SimpleModel {

  private AssessmentType assessmentType;
  private From from;
  private Long spaceId;
  private Date startDateTime;
  private Date dueDateTime;
  private String spaceName;
  private String categoryName;
  private Long challengeeId;

  public AssessementsInfoMessage() {}

  public AssessementsInfoMessage(
      Long id,
      String name,
      AssessmentType assessmentType,
      From from,
      Long spaceId,
      Date startDateTime,
      Date dueDateTime,
      String spaceName,
      String categoryName,
      Long challengeeId) {
    super(id, name);
    this.assessmentType = assessmentType;
    this.from = from;
    this.spaceId = spaceId;
    this.startDateTime = startDateTime;
    this.dueDateTime = dueDateTime;
    this.spaceName = spaceName;
    this.categoryName = categoryName;
    this.challengeeId = challengeeId;
  }

  public AssessmentType getAssessmentType() {
    return assessmentType;
  }

  public void setAssessmentType(AssessmentType assessmentType) {
    this.assessmentType = assessmentType;
  }

  public From getFrom() {
    return from;
  }

  public void setFrom(From from) {
    this.from = from;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public Date getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(Date startDateTime) {
    this.startDateTime = startDateTime;
  }

  public Date getDueDateTime() {
    return dueDateTime;
  }

  public void setDueDateTime(Date dueDateTime) {
    this.dueDateTime = dueDateTime;
  }

  public String getSpaceName() {
    return spaceName;
  }

  public void setSpaceName(String spaceName) {
    this.spaceName = spaceName;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  @Override
  public String toString() {
    return String.format(
            "AssessementsInfoMessage [assessmentType=%s, from=%s, spaceId=%s, startDateTime=%s, dueDateTime=%s, spaceName=%s, categoryName=%s]",
            assessmentType, from, spaceId, startDateTime, dueDateTime, spaceName, categoryName)
        + super.toString();
  }

public Long getChallengeeId() {
	return challengeeId;
}

public void setChallengeeId(Long challengeeId) {
	this.challengeeId = challengeeId;
}

}
